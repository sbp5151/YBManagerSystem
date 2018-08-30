package com.jld.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jld.service.bean.OrderBean;
import com.jld.service.dialog.SetRoomNameAgainDialog;
import com.jld.service.dialog.SetRoomNameDialog;
import com.jld.service.multicast.MulticastReceive;
import com.jld.service.multicast.MulticastSend;
import com.jld.service.receiver.NetworkReceiver;
import com.jld.service.util.Constant;
import com.jld.service.util.IpUtil;
import com.jld.service.util.LogUtil;
import java.util.List;
public class MainActivity extends AppCompatActivity implements MulticastReceive.OnReceiveListen {

    private MulticastSend.MyBinder mSBinder;
    int sendPosition = 0;
    private Gson mGson;
    private boolean sendServiceIsBind = false;
    private boolean receiveServiceIsBind = false;
    private SharedPreferences mSp;
    private MulticastReceive.MyBinder mRBinder;
    private static final String TAG = "MainActivity";
    public static String roomState = "0";//房间状态0未开房、1已开房、2超时
    public static String client_name = "";//客户姓名
    public static int mRemaining_time = -1;//剩余时间 -1未开房(秒)
    public static String mDev_ip = "";//设备IP
    private String mDevice_id;
    public static final int UPDATE_TIME = 0x10;
    public static final int UPDATE_IP = 0x11;
    public static final int KEEP_DIALOG = 0x12;
    public static final int START_SERVICE = 0x13;
    private String keepout_content = "";
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    LogUtil.d(TAG, "UPDATE_TIME---:" + mRemaining_time);
                    if (mRemaining_time <= 0) {//时间到
                        roomState = "2";
                        //open keepout
                        keepout_content = "您的消费时间已到，请到前台充值续时";
                        mHandler.sendEmptyMessage(KEEP_DIALOG);
                        mHandler.removeMessages(UPDATE_TIME);
                    } else if (roomState.equals("1")) {//时间减一
                        mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                        mRemaining_time--;
                    }
                    if (mRemaining_time == 3 * 60) {
                        showToast(3);
                    } else if (mRemaining_time == 2 * 60) {
                        showToast(2);
                    } else if (mRemaining_time == 1 * 60) {
                        showToast(1);
                    }
                    break;
                case UPDATE_IP:
                    mDev_ip = IpUtil.getIp(MainActivity.this);
                    LogUtil.d(TAG, "UPDATE_IP:" + mDev_ip);
                    if (mSBinder != null)
                        mSBinder.update();
                    break;
                case KEEP_DIALOG:
                    LogUtil.d(TAG, "KEEP_DIALOG:");
                    Intent intent = new Intent(MainActivity.this, KeepoutActivity.class);
                    intent.putExtra("content", keepout_content);
                    startActivity(intent);
                    break;
                case START_SERVICE:
                    startService();
                    break;
            }
        }
    };
    private ImageButton mIv_manage;
    private TextView mTv_room_name;
    private TextView mTv_client_name;
    private NetworkReceiver mNetworkReceiver;
    boolean isStart = false;//activity是否处于启动状态，于ok键是否启动腾讯视频
    private View mToastView;
//    boolean isStartActivity = false;//在activity stop的时候是否因为启动activity所致

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate:");
        setContentView(R.layout.activity_main);
        mGson = new Gson();
        mSp = getSharedPreferences(Constant.SHARED_KEY, MODE_PRIVATE);
        getDeviceId();
        mIv_manage = (ImageButton) findViewById(R.id.ib_manage);
        mIv_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String roomName = mSp.getString(Constant.SHARED_ROOM_NAME, "");
                if (TextUtils.isEmpty(roomName)) {
                    showSetRoomDialog();
                } else {
                    SetRoomNameAgainDialog dialog = new SetRoomNameAgainDialog(MainActivity.this, new SetRoomNameAgainDialog.OnProgramTabListen() {
                        @Override
                        public void onSetTab(String roomName) {
                            if (mSBinder != null)
                                mSBinder.update();
                            mTv_room_name.setText(roomName);
                        }
                    });
                    dialog.show(MainActivity.this.getFragmentManager(), "");
                }
            }
        });
        mTv_room_name = (TextView) findViewById(R.id.tv_room_name);
        mTv_client_name = (TextView) findViewById(R.id.tv_client_name);
        String roomName = mSp.getString(Constant.SHARED_ROOM_NAME, "");
        if (TextUtils.isEmpty(roomName)) {
            mTv_room_name.setText("未使用");
            showSetRoomDialog();
        } else {
            mTv_room_name.setText(roomName);
            //开启发送和接收服务
//            startService();
            mHandler.sendEmptyMessage(START_SERVICE);
            //开启腾讯视频
            startTencentVideo();
            if (roomState.equals("0")) {
                //open keepout
                keepout_content = "房间空闲";
                mHandler.sendEmptyMessageDelayed(KEEP_DIALOG, 3000);
            }
        }
        //注册网络状态广播
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkReceiver = new NetworkReceiver(mHandler);
        registerReceiver(mNetworkReceiver, intentFilter);
        mToastView = LayoutInflater.from(this).inflate(R.layout.toast_layout, null);
    }

    private void showToast(int time) {
        Toast toast = new Toast(this);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int height = windowManager.getDefaultDisplay().getHeight();
        toast.setGravity(Gravity.TOP, 0, height / 5);
        toast.setDuration(Toast.LENGTH_LONG);
        TextView tv_center = (TextView) mToastView.findViewById(R.id.tv_alert_dialog_content);
        tv_center.setText("您的包房时长剩余时间不到" + time + "分钟");
        toast.setView(mToastView);
        toast.show();
    }

    private void showSetRoomDialog() {
        //设置房间名称
        SetRoomNameDialog nameDialog = new SetRoomNameDialog(this, new SetRoomNameDialog.OnProgramTabListen() {
            @Override
            public void onSetTab(String roomName) {
                //开启发送和接收服务
                startService();
                //开启腾讯视频
                startTencentVideo();
                if (roomState.equals("0")) {
                    //open keepout
                    keepout_content = "房间空闲";
                    mHandler.sendEmptyMessageDelayed(KEEP_DIALOG, 3000);
                }
            }
        });
        nameDialog.show(getFragmentManager(), "");
    }

    private void startService() {
        //接收service
        Intent intent1 = new Intent(MainActivity.this, MulticastReceive.class);
        startService(intent1);
        bindService(intent1, receiveCon, BIND_AUTO_CREATE);

        //发送service
        Intent intent2 = new Intent(MainActivity.this, MulticastSend.class);
        startService(intent2);
        bindService(intent2, sendCon, BIND_AUTO_CREATE);
    }

    /**
     * 开启腾讯视频APP
     */
    private void startTencentVideo() {
        LogUtil.d(TAG, "startTencentVideo");
        loadApps();
      //  Intent intent = getPackageManager().getLaunchIntentForPackage("com.ktcp.video");
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.ktcp.tvvideo");
//        Intent intent = getPackageManager().getLaunchIntentForPackage("com.jld.InformationRelease");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(intent, 0);
        for (int i = 0; i < apps.size(); i++) {
            ResolveInfo info = apps.get(i);
            String packageName = info.activityInfo.packageName;
            String cls = info.activityInfo.name;
            CharSequence name = info.activityInfo.loadLabel(getPackageManager());
            LogUtil.d(TAG, name + "---" + packageName + "---" + cls);
        }
    }

    /**
     * 数据接收监听
     *
     * @param order
     */
    @Override
    public void receiveListen(String order) {
        OrderBean orderBean = mGson.fromJson(order, OrderBean.class);
        LogUtil.d(TAG, "receiveListen:" + orderBean);
        Intent myReceiver;
        if (!orderBean.getDec_id().equals(mDevice_id))
            return;
        switch (orderBean.getOrder()) {
            case Constant.ORDER_OPEN_ROOM://开房
                LogUtil.d(TAG, "收到开房指令");
                client_name = orderBean.getClient_name();
                mRemaining_time = Integer.parseInt(orderBean.getContinued_time()) * 60;
                mSBinder.update();
                myReceiver = new Intent(Constant.ACTION_CLOSE_KEEPOUT_DIALOG);
                sendBroadcast(myReceiver);
                mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv_client_name.setText(client_name);
                    }
                });
                //close keepout
                if (mKeepOutListen != null)
                    mKeepOutListen.closeKeepOut();
                roomState = "1";
                break;
            case Constant.ORDER_CONTINUED://续时
                LogUtil.d(TAG, "收到续时指令");
                myReceiver = new Intent(Constant.ACTION_CLOSE_KEEPOUT_DIALOG);
                sendBroadcast(myReceiver);
                if (roomState.equals("2"))
                    mHandler.sendEmptyMessageDelayed(UPDATE_TIME, 1000);
                mRemaining_time += Integer.parseInt(orderBean.getContinued_time()) * 60;
                //close keepout
                if (mKeepOutListen != null)
                    mKeepOutListen.closeKeepOut();
                roomState = "1";
                break;
            case Constant.ORDER_CLOSE_ROOM://退房
                //open keepout
                keepout_content = "房间空闲";
                mHandler.sendEmptyMessage(KEEP_DIALOG);

                LogUtil.d(TAG, "收到退房指令");
                mSBinder.update();
                myReceiver = new Intent(Constant.ACTION_OPEN_KEEPOUT_DIALOG);
                sendBroadcast(myReceiver);
                //停止时间
                mHandler.removeMessages(UPDATE_TIME);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv_client_name.setText("未使用");
                    }
                });
                roomState = "0";
                break;
        }
    }

    ServiceConnection sendCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mSBinder = (MulticastSend.MyBinder) iBinder;
            sendServiceIsBind = true;
        }

        @Override

        public void onServiceDisconnected(ComponentName componentName) {
            sendServiceIsBind = false;
        }
    };
    ServiceConnection receiveCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mRBinder = (MulticastReceive.MyBinder) iBinder;
            mRBinder.setOnReceiveListen(MainActivity.this);
            mRBinder.setHandler(mHandler);
            receiveServiceIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            receiveServiceIsBind = false;
        }
    };

    /**
     * 获取设备唯一标识码
     *
     * @return
     */
    private void getDeviceId() {
        SharedPreferences.Editor edit = mSp.edit();
        mDevice_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        LogUtil.d(TAG, "device_id:" + mDevice_id);
        edit.putString(Constant.SHARED_DEVICE_ID, mDevice_id);
        edit.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
        isStart = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.d(TAG, "onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.d(TAG, "onStart");
        isStart = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "onKeyDown:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && isStart) {
            startTencentVideo();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        if (sendServiceIsBind)
            unbindService(sendCon);
        if (receiveServiceIsBind)
            unbindService(receiveCon);
        if (mNetworkReceiver != null)
            unregisterReceiver(mNetworkReceiver);
        mHandler.removeMessages(UPDATE_TIME);
    }

    static KeepOutListen mKeepOutListen;

    public interface KeepOutListen {
        void closeKeepOut();

        void changeContent(String content);
    }

    public static void setKeepOutListen(KeepOutListen keepOutListen) {
        mKeepOutListen = keepOutListen;
    }
}

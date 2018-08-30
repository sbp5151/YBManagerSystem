package com.jld.yb;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jld.yb.adapter.RoomRecyclerAdapter;
import com.jld.yb.bean.OrderBean;
import com.jld.yb.bean.RoomBean;
import com.jld.yb.dialog.CloseRoomAlertDialog;
import com.jld.yb.dialog.ContinuedDialog;
import com.jld.yb.dialog.OpenRoomDialog;
import com.jld.yb.dialog.SetPasswordDialog;
import com.jld.yb.multicast.MulticastReceive;
import com.jld.yb.multicast.MulticastSend;
import com.jld.yb.util.Constant;
import com.jld.yb.util.LogUtil;

import java.util.ArrayList;

public class StartActivity extends BaseActivity implements MulticastReceive.OnReceiveListen {

    private ArrayList<RoomBean> mRooms = new ArrayList<>();
    private RoomRecyclerAdapter mAdapter;
    private static final String TAG = "StartActivity";
    private boolean sendServiceIsBind = false;
    private boolean receiveServiceIsBind = false;
    private MulticastSend.MyBinder mSBinder;
    private MulticastReceive.MyBinder mRBinder;
    private Gson mGson;
    private SharedPreferences mSp;
    private String mRoom_password;
    private SwipeRefreshLayout mRefresh;
    //下拉刷新停止
    public static final int REFRESH_DISMISS = 0x01;
    //更新房间信息tag
    public static final int REFRESH_ROOM = 0x02;
    //房间刷新时间间隔
    private static final int REFRESH_ROOM_TIME = 1000;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DISMISS:
                    if (mRefresh.isRefreshing())
                        mRefresh.setRefreshing(false);
                    break;
                case REFRESH_ROOM://房间信息更新
                    if (mRooms != null && mRooms.size() > 0) {
                        for (int i = 0; i < mRooms.size(); i++) {
                            RoomBean roomBean = mRooms.get(i);
                            //超过十五秒没有更新update time则为离线状态
                            if (System.currentTimeMillis() - Long.parseLong(roomBean.getUpdate_time()) > 1000 * 10) {
                                LogUtil.d(TAG, "cur_time" + System.currentTimeMillis());
                                LogUtil.d(TAG, "update_time" + roomBean.getUpdate_time());
                                roomBean.setLast_room_state(roomBean.getRoom_state());
                                roomBean.setRoom_state(Constant.ROOM_STATE_OFF_LINE);//离线
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    mHandler.sendEmptyMessageDelayed(REFRESH_ROOM, REFRESH_ROOM_TIME);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mGson = new Gson();
        mSp = getSharedPreferences(Constant.SHARED_KEY, MODE_PRIVATE);
        initView();
        setRoomManagePassword(false);

        //创建并绑定发送数据服务
        Intent intent1 = new Intent(this, MulticastSend.class);
        startService(intent1);
        bindService(intent1, sendCon, BIND_AUTO_CREATE);

        //创建并绑定接收数据服务
        Intent intent2 = new Intent(this, MulticastReceive.class);
        startService(intent2);
        bindService(intent2, receiveCon, BIND_AUTO_CREATE);
        mHandler.sendEmptyMessageDelayed(REFRESH_ROOM, REFRESH_ROOM_TIME);
    }

    /**
     * isPassword
     *
     * @param isAgain 是否重新输入密码
     */
    public void setRoomManagePassword(boolean isAgain) {
        mRoom_password = mSp.getString(Constant.ROOM_MANAGE_PASSWORD, "");
        LogUtil.d(TAG, "mRoom_password:" + mRoom_password);
        LogUtil.d(TAG, "isAgain:" + isAgain);
        if (isAgain || TextUtils.isEmpty(mRoom_password)) {
            SetPasswordDialog dialog = new SetPasswordDialog(this, new SetPasswordDialog.OnSetPasswordListen() {
                @Override
                public void onDataBack(String password) {
                    mRoom_password = password;
                }
            });
            dialog.show(getFragmentManager(), "");
        }
    }

    private void initView() {
        //title
        View title = findViewById(R.id.title_bar);
        View title_tool = title.findViewById(R.id.titlebar_tool);
        title_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRoomManagePassword(true);
            }
        });
        //swipeRefresh
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_my_program);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
                mHandler.sendEmptyMessageDelayed(REFRESH_DISMISS, 1000);
            }
        });
        //RecyclerView
        RecyclerView rv_room = (RecyclerView) findViewById(R.id.recycler_room);
//        rv_room.setLayoutManager(new GridLayoutManager(this, 1));


        rv_room.setLayoutManager(new GridLayoutManager(this, 3));
        rv_room.setItemAnimator(new DefaultItemAnimator());
        // addFakeData();

        mAdapter = new RoomRecyclerAdapter(this, mRooms);
        rv_room.setAdapter(mAdapter);
        mAdapter.setOnItemClickListen(new RoomRecyclerAdapter.OnItemClickListen() {

            @Override
            public void openRoomClickListen(View view, int position) {
                LogUtil.d(TAG, "openRoomClickListen:");
                openRoom(position);
            }

            @Override
            public void closeRoomClickListen(View view, final int position) {
                LogUtil.d(TAG, "closeRoomClickListen:");
                CloseRoomAlertDialog dialog = new CloseRoomAlertDialog(StartActivity.this, mRooms.get(position).getRoom_name());
                dialog.show(StartActivity.this.getFragmentManager(), "");
                dialog.setOnCloseRoomClickListen(new CloseRoomAlertDialog.OnCloseRoomClickListen() {
                    @Override
                    public void onConfirmClick(View view) {
                        closeRoom(position);
                    }
                });
            }

            @Override
            public void continuedClickListen(View view, int position) {
                LogUtil.d(TAG, "continuedClickListen:");
                continued(position);
            }
        });
    }

    private void addFakeData() {

        for (int i = 0; i < 20; i++) {
            RoomBean roomBean = new RoomBean("room:" + i, "", "", "", "", "", "", "");
            mRooms.add(roomBean);
        }
    }

    /**
     * 开房
     *
     * @param position
     */
    private void openRoom(final int position) {
        OpenRoomDialog dialog = new OpenRoomDialog(this, new OpenRoomDialog.OnOpenRoomListen() {
            @Override
            public void onDataBack(String continued, String clientName) {
                RoomBean room = mRooms.get(position);
                OrderBean order = new OrderBean();
                order.setDec_id(room.getDev_id());
                order.setOrder(Constant.ORDER_OPEN_ROOM);
                order.setContinued_time(continued);
                order.setClient_name(clientName);
                String jOrder = mGson.toJson(order);
                mSBinder.sendData(jOrder);
            }
        });
        dialog.show(getFragmentManager(), "");
    }

    /**
     * 退房
     *
     * @param position
     */
    private void closeRoom(int position) {
        RoomBean room = mRooms.get(position);
        OrderBean order = new OrderBean();
        order.setDec_id(room.getDev_id());
        order.setOrder(Constant.ORDER_CLOSE_ROOM);
        String jOrder = mGson.toJson(order);
        mSBinder.sendData(jOrder);
    }

    /**
     * 续时
     *
     * @param position
     */
    private void continued(final int position) {

        ContinuedDialog dialog = new ContinuedDialog(this, new ContinuedDialog.OnOpenRoomListen() {
            @Override
            public void onDataBack(String continued) {
                RoomBean room = mRooms.get(position);
                OrderBean order = new OrderBean();
                order.setDec_id(room.getDev_id());
                order.setOrder(Constant.ORDER_CONTINUED);
                order.setContinued_time(continued);
                String jOrder = mGson.toJson(order);
                mSBinder.sendData(jOrder);
            }
        });
        dialog.show(getFragmentManager(), "");
    }

    ServiceConnection receiveCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mRBinder = (MulticastReceive.MyBinder) iBinder;
            mRBinder.setOnReceiveListen(StartActivity.this);
            receiveServiceIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            receiveServiceIsBind = false;
        }
    };
    ServiceConnection sendCon = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            sendServiceIsBind = true;
            mSBinder = (MulticastSend.MyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            sendServiceIsBind = false;
        }
    };

    @Override
    public void receiveListen(String jRoom) {
        LogUtil.d(TAG, "receiveListen:" + jRoom);
        try {
            final RoomBean roomBean = mGson.fromJson(jRoom, RoomBean.class);
            if (!roomBean.getManage_password().equals(mRoom_password))
                return;
            if (!mRooms.contains(roomBean)) {//新添加
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomBean.setUpdate_time(System.currentTimeMillis() + "");
                        mAdapter.addData(roomBean);
                    }
                });
            } else {//更新数据
                int index = mRooms.indexOf(roomBean);
                mRooms.remove(roomBean);
                roomBean.setUpdate_time(System.currentTimeMillis() + "");
                mRooms.add(index, roomBean);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
            }
        } catch (JsonSyntaxException e) {
            LogUtil.e(TAG, "JsonSyntaxException:" + e.toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiveServiceIsBind)
            unbindService(receiveCon);
        if (sendServiceIsBind)
            unbindService(sendCon);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

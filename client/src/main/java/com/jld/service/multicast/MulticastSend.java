package com.jld.service.multicast;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.jld.service.KeepoutActivity;
import com.jld.service.bean.RoomBean;
import com.jld.service.util.Constant;
import com.jld.service.util.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import static com.jld.service.KeepoutActivity.isFinish;
import static com.jld.service.KeepoutActivity.isKeep;
import static com.jld.service.KeepoutActivity.isStop;
import static com.jld.service.MainActivity.client_name;
import static com.jld.service.MainActivity.mDev_ip;
import static com.jld.service.MainActivity.mRemaining_time;
import static com.jld.service.MainActivity.roomState;

/**
 * 发送客户端信息
 */
public class MulticastSend extends Service {

    private MyBinder mMyBinder;
    private boolean isSend = true;
    private boolean isScanKeep = true;
    private boolean isReceive = true;
    private MulticastSocket mMulticastSocket;
    private DatagramPacket mDatagramPacket;
    private static final String TAG = "MulticastSend";
    private InetAddress mAddress;

    /**
     * 每隔1秒发送一次
     */
    private static final int SEND_INTERCAL = 1000;
    private SharedPreferences mSp;
    private Gson mGson;
    private RoomBean mRoomBean;

    @Override
    public IBinder onBind(Intent intent) {
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }

    public class MyBinder extends Binder {
        public void sendData(String jBody) throws UnsupportedEncodingException {
            LogUtil.d(TAG, "sendData:" + jBody);
            mDatagramPacket = new DatagramPacket
                    (jBody.getBytes("UTF-8"), jBody.getBytes("UTF-8").length, mAddress, Constant.SERVICE_PORT);
        }

        //更新发送数据
        public void update() {
            initData();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate---开启发送服务");
        mSp = getSharedPreferences(Constant.SHARED_KEY, MODE_PRIVATE);
        mGson = new Gson();
        try {
            mAddress = InetAddress.getByName(Constant.MULTICAST_IP);
            mMulticastSocket = new MulticastSocket();
            initData();
            new Thread(runSend).start();
            new Thread(keepRun).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initData() {
        LogUtil.d(TAG,"initData");
        if (mRoomBean == null)
            mRoomBean = new RoomBean();
        String name = mSp.getString(Constant.SHARED_ROOM_NAME, "");
        String dev_id = mSp.getString(Constant.SHARED_DEVICE_ID, "");
        String password = mSp.getString(Constant.SHARED_MANAGE_PASSWORD, "");
        mRoomBean.setManage_password(password);
        mRoomBean.setDev_ip(mDev_ip);
        mRoomBean.setRoom_name(name);
        mRoomBean.setDev_id(dev_id);
        mRoomBean.setRoom_state(roomState);
        mRoomBean.setRemaining_time((mRemaining_time / 1000) + "");
        mRoomBean.setClient_name(client_name);
        mRoomBean.setUpdate_time(System.currentTimeMillis()+"");
    }

    /**
     * 发送服务端IP地址
     */
    public Runnable runSend = new Runnable() {
        @Override
        public void run() {
            while (isSend) {
                try {
                    if (mRoomBean == null) {
                        Thread.sleep(2000);
                        LogUtil.d(TAG, "等待");
                        continue;
                    }
                    mRoomBean.setUpdate_time(System.currentTimeMillis()+"");
                    mRoomBean.setRemaining_time(mRemaining_time + "");
                    mRoomBean.setRoom_state(roomState);
                    String jRoom = mGson.toJson(mRoomBean);
                    try {
                        mDatagramPacket = new DatagramPacket
                                (jRoom.getBytes("UTF-8"), jRoom.getBytes("UTF-8").length, mAddress, Constant.SERVICE_PORT);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mMulticastSocket.send(mDatagramPacket);
                    byte[] data = mDatagramPacket.getData();
                    String s = new String(data, 0, data.length);
                    LogUtil.d(TAG, "runSend:" + s);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(SEND_INTERCAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Runnable keepRun = new Runnable() {
        @Override
        public void run() {
            while (isScanKeep) {
                LogUtil.d(TAG, "ScanKeep:" + isKeep + isStop + isFinish);
                if (isKeep && isStop && !isFinish) {
                    Intent intent = new Intent(MulticastSend.this, KeepoutActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                if (isKeep) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isSend = false;
        if (mMulticastSocket != null)
            mMulticastSocket.close();
    }
}

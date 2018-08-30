package com.jld.service.multicast;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.jld.service.util.Constant;
import com.jld.service.util.LogUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * 接收服务器指令
 */
public class MulticastReceive extends Service {

    private MyBinder mMyBinder;
    private boolean isReceive = true;
    private MulticastSocket mMSocket;
    private byte[] mBytes;
    private static final String TAG = "MulticastReceive";
    private InetAddress mAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate---开启接收服务");
        mBytes = new byte[1024];
        try {
            mMSocket = new MulticastSocket(Constant.CLIENT_PORT);
            new Thread(runReceive).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(TAG, "UnknownHostException:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException:" + e.getMessage());
        }
    }

    boolean isJoin = false;
    Runnable runReceive = new Runnable() {
        @Override
        public void run() {
            while (!isJoin) {
                try {
                    mAddress = InetAddress.getByName(Constant.MULTICAST_IP);
                    mMSocket.joinGroup(mAddress);//加入组播地址
                    mMSocket.setSoTimeout(1000 * 10);//超时后重新加入组播地址否则不能接收数据
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtil.e(TAG, "Thread.sleep(3000):" + isJoin);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                isJoin = true;
            }
            while (isReceive) {
                DatagramPacket packet = new DatagramPacket(mBytes, mBytes.length);
                Log.d(TAG, "等待接收---");
                try {
                    mMSocket.receive(packet);//接收
                } catch (InterruptedIOException e) {//超时
                    e.printStackTrace();
                    try {
                        mMSocket.leaveGroup(mAddress);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    try {
                        mMSocket.joinGroup(mAddress);//加入组播地址
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    Log.e(TAG, "接收超时---：" + e.toString());
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String str = null;
                try {
                    str = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "接收成功:" + str);
                if (mOnReceiveListen != null && isSend) {
                    Log.d(TAG, "mOnReceiveListen:" + mOnReceiveListen);
                    isSend = false;
                    mOnReceiveListen.receiveListen(str);
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };
    boolean isSend = true;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isSend = true;
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }

    Handler mainHandler;

    public class MyBinder extends Binder {
        public void setOnReceiveListen(OnReceiveListen onReceiveListen) {
            mOnReceiveListen = onReceiveListen;
        }

        public void setHandler(Handler handler) {
            mainHandler = handler;
        }
    }

    OnReceiveListen mOnReceiveListen;

    public interface OnReceiveListen {
        void receiveListen(String jRoom);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d(TAG, "onDestroy");
        isReceive = false;
        if (mMSocket != null)
            mMSocket.close();
    }
}

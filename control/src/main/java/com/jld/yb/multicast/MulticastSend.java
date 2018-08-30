package com.jld.yb.multicast;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

import com.jld.yb.util.Constant;
import com.jld.yb.util.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastSend extends Service {

    private MyBinder mMyBinder;
    private boolean isSend = true;
    private boolean isReceive = true;
    private MulticastSocket mMulticastSocket;
    private DatagramPacket mDatagramPacket;
    private static final String TAG = "MulticastSend";
    /**
     * 每隔2秒发送一次
     */
    private static final int SEND_INTERCAL = 10000;
    private InetAddress mAddress;
    private Handler mHandler;
    private HandlerThread mHandlerThread;


    @Override
    public IBinder onBind(Intent intent) {
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }

    public class MyBinder extends Binder {
        public void sendData(String jBody) {
            LogUtil.d(TAG, "sendData:" + jBody);
//            jBody = jBody+ "\"1\"}";
            try {
                mDatagramPacket = new DatagramPacket(jBody.getBytes("UTF-8"), jBody.getBytes("UTF-8").length, mAddress, Constant.CLIENT_PORT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //发送五次，以防丢包
            mHandler.sendEmptyMessage(1);
            mHandler.sendEmptyMessageDelayed(1,50);
            mHandler.sendEmptyMessageDelayed(1,100);
            mHandler.sendEmptyMessageDelayed(1,150);
            mHandler.sendEmptyMessageDelayed(1,200);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate");
        try {
            mAddress = InetAddress.getByName(Constant.MULTICAST_IP);
            mMulticastSocket = new MulticastSocket();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //新线程
        mHandlerThread = new HandlerThread("send_handler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    mMulticastSocket.send(mDatagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
        isSend = false;
        if (mMulticastSocket != null)
            mMulticastSocket.close();
        mHandlerThread.quit();
    }
}

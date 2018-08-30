package com.jld.yb.multicast;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.jld.yb.util.Constant;
import com.jld.yb.util.LogUtil;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MulticastReceive extends Service {

    private MyBinder mMyBinder;
    private boolean isReceive = true;
    private MulticastSocket mMSocket;
    private byte[] mBytes = new byte[1024];
    private static final String TAG = "MulticastReceive";
    private InetAddress mAddress;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        try {
            mAddress = InetAddress.getByName(Constant.MULTICAST_IP);
            mMSocket = new MulticastSocket(Constant.SERVICE_PORT);
            mMSocket.joinGroup(mAddress);//加入组播地址
            mMSocket.setSoTimeout(1000*5);//超时后重新加入组播地址否则不能接收数据
            new Thread(runReceive).start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(TAG, "UnknownHostException:" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException:" + e.getMessage());
        }
    }

    Runnable runReceive = new Runnable() {
        @Override
        public void run() {
            while (isReceive) {
                DatagramPacket packet = new DatagramPacket(mBytes, mBytes.length);
                Log.d(TAG, "等待接收");
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
                    Log.e(TAG, "接收超时：" + e.toString());
                    continue;
                }catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "接收错误：" + e.toString());
                }
                String str = null;
                try {
                    str = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "接收成功:" + str);
                Log.d(TAG, "getLength:" + packet.getLength());
                if (mOnReceiveListen != null)
                    mOnReceiveListen.receiveListen(str);
            }
            if (null != mMSocket) {
                mMSocket.close();
                LogUtil.d(TAG, "Client--mMultiSocket.close()");
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        if (mMyBinder == null)
            mMyBinder = new MyBinder();
        return mMyBinder;
    }

    public class MyBinder extends Binder {
        public void setOnReceiveListen(OnReceiveListen onReceiveListen) {
            mOnReceiveListen = onReceiveListen;
        }
    }

    OnReceiveListen mOnReceiveListen;

    public interface OnReceiveListen {
        void receiveListen(String jRoom);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        isReceive = false;
        if (mMSocket != null)
            mMSocket.close();
    }
}

package com.jld.service.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.widget.Toast;

import com.jld.service.util.LogUtil;

import static com.jld.service.MainActivity.UPDATE_IP;

public class NetworkReceiver extends BroadcastReceiver {

    private Handler mHandler;
    public NetworkReceiver(Handler handler) {
        mHandler = handler;
    }

    public NetworkReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                String typeName = info.getTypeName();
                LogUtil.d("NetworkReceiver","typeName:"+typeName);
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    /////WiFi网络
                    mHandler.sendEmptyMessageDelayed(UPDATE_IP,2000);
                } else if (info.getType() == ConnectivityManager.TYPE_ETHERNET) {
                    /////有线网络
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    /////////3g网络
                }
            } else {
                Toast.makeText(context, "请连接WiFi", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

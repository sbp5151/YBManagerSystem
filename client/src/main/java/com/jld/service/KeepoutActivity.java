package com.jld.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jld.service.multicast.MulticastReceive;
import com.jld.service.util.Constant;
import com.jld.service.util.LogUtil;
import com.jld.service.util.ToastUtil;

import java.io.File;

public class KeepoutActivity extends Activity {

    private static final String TAG = "KeepoutActivity";
    public static boolean isFinish = false;
    public static boolean isStop = false;
    public static boolean isKeep = false;
    private TextView mTv_content;
    private EditText mEt_password;
    private Button mBtn_password;
    private SharedPreferences mSp;
    private String mContent;
    private ImageView mAliPay;
    private TextView mWechat_pay;
    private TextView mAli_pay;
    private ImageView mWechatPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate");
        isKeep = true;
        setContentView(R.layout.activity_keepout);

        mSp = getSharedPreferences(Constant.SHARED_KEY, MODE_PRIVATE);
        mContent = getIntent().getStringExtra("content");

        initView();

        MainActivity.setKeepOutListen(new MainActivity.KeepOutListen() {
            @Override
            public void closeKeepOut() {
                isFinish = true;
                finish();
            }

            @Override
            public void changeContent(final String content) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTv_content.setText(content);
                    }
                });
            }
        });
    }

    public void initView() {
        mTv_content = (TextView) findViewById(R.id.tv_content);
        mTv_content.setText(mContent);
        mTv_content.setSelected(true);
        mEt_password = (EditText) findViewById(R.id.et_manage_password);
        Button btn_manage = (Button) findViewById(R.id.btn_manage);
        mBtn_password = (Button) findViewById(R.id.btn_password);
        btn_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordVisibility();
            }
        });
        mBtn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mEt_password.getText())) {
                    ToastUtil.showToast(KeepoutActivity.this, "请输入设备管理密码", 3000);
                } else {
                    String manage_password = mSp.getString(Constant.SHARED_MANAGE_PASSWORD, "");
                    String password = mEt_password.getText().toString();
                    if (password.equals(manage_password) || password.equals(Constant.SUPER_MANAGE_PASSWORD)) {
                        isFinish = true;
                        finish();
                    } else {
                        ToastUtil.showToast(KeepoutActivity.this, "输入密码错误", 3000);
                    }
                }
            }
        });
        mAliPay = (ImageView) findViewById(R.id.iv_ali_pay);
        mWechat_pay = (TextView) findViewById(R.id.tv_weichat_pay);
        mAli_pay = (TextView) findViewById(R.id.tv_ali_pay);
        mWechatPay = (ImageView) findViewById(R.id.iv_wechat_pay);
//        pay
       String payFile = Environment.getExternalStorageDirectory() + File.separator + "tencentPayFile";
        File file = new File(payFile);
        if (!file.exists())
            file.mkdirs();

        LogUtil.d(TAG,"pay_file:"+file.getAbsolutePath());
        File[] files = file.listFiles();
        for (File fileItem : files) {
            if (fileItem.getName().equals("tencentAliPay.png") || fileItem.getName().equals("tencentAliPay.jpg")) {
                Uri alipayuri = Uri.parse(fileItem.getAbsolutePath());
                mAliPay.setImageURI(alipayuri);
                showAliPay();
            } else if (fileItem.getName().equals("tencentWechatPay.png") || fileItem.getName().equals("tencentWechatPay.jpg")) {
                Uri alipayuri = Uri.parse(fileItem.getAbsolutePath());
                mWechatPay.setImageURI(alipayuri);
                showWechatPay();
            }else{
                hiedPay();
            }
        }
        //  永久隐藏二维码支付
        hiedPay();
    }
    private void showAliPay() {
        mAli_pay.setVisibility(View.VISIBLE);
        mAliPay.setVisibility(View.VISIBLE);
        //  永久隐藏二维码支付
        hiedPay();
    }

    private void hiedPay(){
        mAli_pay.setVisibility(View.GONE);
        mAliPay.setVisibility(View.GONE);
        mWechat_pay.setVisibility(View.GONE);
        mWechatPay.setVisibility(View.GONE);
    }
    private void showWechatPay() {
        mWechat_pay.setVisibility(View.VISIBLE);
        mWechatPay.setVisibility(View.VISIBLE);
        //  永久隐藏二维码支付
        hiedPay();
    }


    private void changePasswordVisibility() {
        if (mEt_password.getVisibility() == View.VISIBLE) {
            mEt_password.setVisibility(View.GONE);
            mBtn_password.setVisibility(View.GONE);
            showWechatPay();
            showAliPay();
        } else {
            mEt_password.setVisibility(View.VISIBLE);
            mBtn_password.setVisibility(View.VISIBLE);
            hiedPay();
        }
    }

    private boolean receiveServiceIsBind;
    ServiceConnection receiveCon = new ServiceConnection() {
        public MulticastReceive.MyBinder mRBinder;

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mRBinder = (MulticastReceive.MyBinder) iBinder;
            receiveServiceIsBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            receiveServiceIsBind = false;
        }
    };

    @Override
    protected void onStart() {
        LogUtil.d(TAG, "onStart");
        isStop = false;
        isFinish = false;
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.d(TAG, "onStop");
        isStop = true;
//        if (!isFinish) {
//            Intent intent = new Intent(this, KeepoutActivity.class);
//            startActivity(intent);
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtil.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy");
        super.onDestroy();
        isKeep = false;
        if (receiveServiceIsBind)
            unbindService(receiveCon);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.d(TAG, "keyCode:" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.jld.yb.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jld.yb.R;
import com.jld.yb.util.Constant;
import com.jld.yb.util.ToastUtil;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/7 17:27
 */
@SuppressLint("ValidFragment")
public class SetPasswordDialog extends DialogFragment {

    private Context mContext;
    OnSetPasswordListen mListen;
    private final SharedPreferences mSp;

    public SetPasswordDialog(Context context, OnSetPasswordListen listen) {
        mContext = context;
        mListen = listen;
        mSp = mContext.getSharedPreferences(Constant.SHARED_KEY, Context.MODE_PRIVATE);
    }

    public interface OnSetPasswordListen {
        void onDataBack(String password);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        //view
        View view = LayoutInflater.from(mContext).inflate(R.layout.password_dialog, null);
        Button sure = (Button) view.findViewById(R.id.btn_password_sure);
        final EditText continued = (EditText) view.findViewById(R.id.et_dialog_password);
        view.findViewById(R.id.dialog1_confirm);

        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置节目名称
                if (TextUtils.isEmpty(continued.getText().toString())) {
                    ToastUtil.showToast(mContext, "请输入房间管理密码", 3000);
                } else {
                    if (mListen != null) {
                        mListen.onDataBack(continued.getText().toString());
                    }
                    SharedPreferences.Editor edit = mSp.edit();
                    edit.putString(Constant.ROOM_MANAGE_PASSWORD, continued.getText().toString()).apply();
                    dialog.dismiss();
                }
            }
        });
        //dialog
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
        return dialog;
    }
}

package com.jld.service.dialog;

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

import com.jld.service.R;
import com.jld.service.util.Constant;
import com.jld.service.util.ToastUtil;

/**
 * 项目名称：InformationRelease
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/7 17:27
 */
@SuppressLint("ValidFragment")
public class SetRoomNameAgainDialog extends DialogFragment {

    private Context mContext;
    OnProgramTabListen mListen;
    private  SharedPreferences mSp;
    private  String mPassword;

    public SetRoomNameAgainDialog(Context context, OnProgramTabListen listen) {
        mContext = context;
        mListen = listen;
        mSp = context.getSharedPreferences(Constant.SHARED_KEY, Context.MODE_PRIVATE);
        mPassword = mSp.getString(Constant.SHARED_MANAGE_PASSWORD, "");
    }

    public interface OnProgramTabListen {
        void onSetTab(String roomName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        //view
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_setting__again_dialog, null);
        final EditText room_name = (EditText) view.findViewById(R.id.et_room_name);
        final EditText password = (EditText) view.findViewById(R.id.et_manage_password);
        final EditText password_new = (EditText) view.findViewById(R.id.et_manage_password_again);
        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
        view.findViewById(R.id.dialog1_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置节目名称
                if (TextUtils.isEmpty(room_name.getText().toString()) || TextUtils.isEmpty(password.getText().toString()) || TextUtils.isEmpty(password_new.getText().toString())) {
                    ToastUtil.showToast(mContext, getString(R.string.input_full), 3000);
                } else {
                    if (password.getText().toString().equals(mPassword) || password.getText().toString().equals(Constant.SUPER_MANAGE_PASSWORD)) {
                        SharedPreferences.Editor edit = mSp.edit();
                        edit.putString(Constant.SHARED_ROOM_NAME, room_name.getText().toString());
                        edit.putString(Constant.SHARED_MANAGE_PASSWORD, password_new.getText().toString());
                        edit.apply();
                        mListen.onSetTab(room_name.getText().toString());
                        dialog.dismiss();
                    } else {
                        ToastUtil.showToast(mContext, getString(R.string.passwrod_error), 3000);
                    }
                }
            }
        });
        //dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT));
        dialog.show();
        return dialog;
    }
}

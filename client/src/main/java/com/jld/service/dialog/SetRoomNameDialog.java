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
public class SetRoomNameDialog extends DialogFragment {

    private Context mContext;
    OnProgramTabListen mListen;
    private final SharedPreferences mSp;

    public SetRoomNameDialog(Context context, OnProgramTabListen listen) {
        mContext = context;
        mListen = listen;
        mSp = context.getSharedPreferences(Constant.SHARED_KEY, Context.MODE_PRIVATE);
    }
    public interface OnProgramTabListen {
        void onSetTab(String roomName);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        //view
        View view = LayoutInflater.from(mContext).inflate(R.layout.room_setting_dialog, null);
        final EditText room_name = (EditText) view.findViewById(R.id.et_room_name);
        final EditText password = (EditText) view.findViewById(R.id.et_manage_password);
        final EditText password_again = (EditText) view.findViewById(R.id.et_manage_password_again);
        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置节目名称
                if (TextUtils.isEmpty(room_name.getText().toString())||TextUtils.isEmpty(password.getText().toString())||TextUtils.isEmpty(password_again.getText().toString())) {
                    ToastUtil.showToast(mContext, getString(R.string.input_full), 3000);
                } else {
                    dialog.dismiss();
                    SharedPreferences.Editor edit = mSp.edit();
                    edit.putString(Constant.SHARED_ROOM_NAME,room_name.getText().toString());
                    edit.putString(Constant.SHARED_MANAGE_PASSWORD,password.getText().toString());
                    edit.apply();
                    mListen.onSetTab(room_name.getText().toString());
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

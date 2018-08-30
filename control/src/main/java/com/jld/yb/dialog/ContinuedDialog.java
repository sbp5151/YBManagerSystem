package com.jld.yb.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jld.yb.R;
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
public class ContinuedDialog extends DialogFragment {

    private Context mContext;
    OnOpenRoomListen mListen;

    public ContinuedDialog(Context context, OnOpenRoomListen listen) {
        mContext = context;
        mListen = listen;
    }
    public interface OnOpenRoomListen {
        void onDataBack(String continued);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        //view
        View view = LayoutInflater.from(mContext).inflate(R.layout.continued_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.dialog1_title);
        Button confirm = (Button) view.findViewById(R.id.dialog1_confirm);
        final EditText continued = (EditText) view.findViewById(R.id.dialog_continued);
        view.findViewById(R.id.dialog1_confirm);
        title.setText(getString(R.string.open_room_title));

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置节目名称
                if (TextUtils.isEmpty(continued.getText().toString())) {
                    ToastUtil.showToast(mContext, getString(R.string.open_room_title), 3000);
                } else {
                    if (mListen != null){
                        mListen.onDataBack(continued.getText().toString());
                    }
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

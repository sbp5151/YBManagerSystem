package com.jld.yb.dialog;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jld.yb.R;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/14 11:11
 */
@SuppressLint("ValidFragment")
public class CloseRoomAlertDialog extends DialogFragment {

    private Context mContext;
    private String mRoomName;
    public CloseRoomAlertDialog(Context context,String roomName) {
        mContext = context;
        mRoomName = roomName;
    }

    OnCloseRoomClickListen mOnCloseRoomClickListen;
    public void setOnCloseRoomClickListen(OnCloseRoomClickListen onCloseRoomClickListen){
        mOnCloseRoomClickListen = onCloseRoomClickListen;
    }
    public interface OnCloseRoomClickListen{
        public void onConfirmClick(View view);
    }

    @Override
    public MyDialog onCreateDialog(Bundle savedInstanceState) {
        final MyDialog dialog = new MyDialog(mContext, R.style.CustomDialog);
        //view
        View view = LayoutInflater.from(mContext).inflate(R.layout.close_room_dialog, null);
        Button confirm = (Button) view.findViewById(R.id.btn_close_room_confirm);
        Button cancel = (Button) view.findViewById(R.id.btn_close_room_cancel);
        TextView content = (TextView) view.findViewById(R.id.tv_close_room_content);

        content.setText("确定要关闭 "+mRoomName+" ?");
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//确定

                if(mOnCloseRoomClickListen!=null){
                    mOnCloseRoomClickListen.onConfirmClick(view);
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//取消
                dialog.dismiss();
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

package com.jld.yb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jld.yb.R;
import com.jld.yb.bean.RoomBean;
import com.jld.yb.util.LogUtil;

import java.util.ArrayList;

/**
 * 项目名称：YBManagerSystem
 * 晶凌达科技有限公司所有，
 * 受到法律的保护，任何公司或个人，未经授权不得擅自拷贝。
 *
 * @creator boping
 * @create-time 2017/7/12 14:56
 */
public class RoomRecyclerAdapter extends RecyclerView.Adapter<RoomRecyclerAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<RoomBean> mRooms;
    public static final String TAG = "RoomRecyclerAdapter";

    public RoomRecyclerAdapter(Context context, ArrayList<RoomBean> datas) {
        mContext = context;
        this.mRooms = datas;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_room, null);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        if (mRooms.size() <= position)
            return;
        RoomBean room = mRooms.get(position);
        holder.mRoom_name.setText(room.getRoom_name());
        holder.mClient_name.setText(room.getClient_name());
        holder.mIp.setText(room.getDev_ip());
        if (room.getRoom_state().equals("0")) {//未开房
            holder.mRemaining_time.setText("未开房");
            holder.mRoom_switch.setText("开房");
            holder.mClient_name.setText("");
            holder.mContinued.setVisibility(View.GONE);
            holder.mRoom_switch.setVisibility(View.VISIBLE);
            holder.mLl_room.setBackgroundColor(mContext.getResources().getColor(R.color.room_grey));
        } else if (room.getRoom_state().equals("1")) {//已开房
            int time = Integer.parseInt(room.getRemaining_time());
            LogUtil.d("time:" + time);
            int miao = time % 60;
            int fen = time / 60 % 60;
            int shi = time / 60 / 60;
            holder.mRemaining_time.setText(shi + ":" + fen + ":" + miao);
            holder.mLl_room.setBackgroundColor(mContext.getResources().getColor(R.color.room_blue));
            holder.mRoom_switch.setText("退房");
            holder.mContinued.setVisibility(View.VISIBLE);
            holder.mRoom_switch.setVisibility(View.VISIBLE);
        } else if (room.getRoom_state().equals("2")) {//超时
            holder.mRemaining_time.setText("已超时");
            holder.mLl_room.setBackgroundColor(mContext.getResources().getColor(R.color.room_orange));
            holder.mRoom_switch.setText("退房");
            holder.mContinued.setVisibility(View.VISIBLE);
            holder.mRoom_switch.setVisibility(View.VISIBLE);
        } else if (room.getRoom_state().equals("-1")) {//离线
            holder.mRemaining_time.setText("离线");
            holder.mLl_room.setBackgroundColor(mContext.getResources().getColor(R.color.room_black));
            holder.mRoom_switch.setVisibility(View.GONE);
            holder.mContinued.setVisibility(View.GONE);
        }
        holder.mRoom_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListen != null) {
                    RoomBean roomBean = mRooms.get(holder.getLayoutPosition());
                    LogUtil.d(TAG, "roomBean:" + roomBean);
                    if (roomBean.getRoom_state().equals("0")) {//开房
                        mOnItemClickListen.openRoomClickListen(view, holder.getLayoutPosition());
                    } else if (roomBean.getRoom_state().equals("1") || roomBean.getRoom_state().equals("2")) {//退房
                        mOnItemClickListen.closeRoomClickListen(view, holder.getLayoutPosition());
                    }
                }
            }
        });
        holder.mContinued.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListen != null)
                    mOnItemClickListen.continuedClickListen(view, holder.getLayoutPosition());
            }
        });
    }

    OnItemClickListen mOnItemClickListen;

    public void setOnItemClickListen(OnItemClickListen onItemClickListen) {
        mOnItemClickListen = onItemClickListen;
    }

    public interface OnItemClickListen {

        void openRoomClickListen(View view, int position);

        void closeRoomClickListen(View view, int position);

        void continuedClickListen(View view, int position);
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLl_room;
        private TextView mRoom_name;
        private TextView mClient_name;
        private TextView mRemaining_time;
        private TextView mIp;
        private Button mRoom_switch;
        private Button mContinued;

        public MyHolder(View view) {
            super(view);
            mLl_room = (LinearLayout) view.findViewById(R.id.ll_room);
            mRoom_name = (TextView) view.findViewById(R.id.tv_room_name);
            mClient_name = (TextView) view.findViewById(R.id.tv_client_name);
            mRemaining_time = (TextView) view.findViewById(R.id.tv_remaining_time);
            mIp = (TextView) view.findViewById(R.id.tv_ip);
            mRoom_switch = (Button) view.findViewById(R.id.btn_room_switch);
            mContinued = (Button) view.findViewById(R.id.btn_continued);
        }
    }

    public void addData(RoomBean roomBean) {
        mRooms.add(roomBean);
        notifyItemInserted(mRooms.size() - 1);
    }

    @Override
    public int getItemCount() {
        if (mRooms == null)
            return 0;
        return mRooms.size();
    }
}

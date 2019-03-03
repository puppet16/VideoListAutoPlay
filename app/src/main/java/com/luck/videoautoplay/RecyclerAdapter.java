package com.luck.videoautoplay;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*************************************************************************************
 * Module Name:
 * Description:
 * Author: 李桐桐
 * Date:   2019/3/1
 *************************************************************************************/
public class RecyclerAdapter extends Adapter<RecyclerAdapter.ViewHolder> {
    public static final String TAG = "RecyclerAdapter";
    //播放状态   0播放，1停止
    public static final int VIDEO_CLOSE = 1;
    public static final int VIDEO_OPEN = 0;
    public static final int VIDEO_FLAG_NONE = 0;//无自动播放
    public static final int VIDEO_FLAG_PREv = 1;//自动播放上一个
    public static final int VIDEO_FLAG_NEXT = 2;//自动播放下一个

    List<DynamicBean> mDatas;
    private int mPositionPlay;//播放的位置
    private boolean mIsLooper;//是否自动播放
    private int mLooperFlag;

    private OnItemClickListener.OnItemClickListenerSub mItemClickListener;

    public RecyclerAdapter() {
        mDatas = new ArrayList<>();
        mPositionPlay = -1;
        mIsLooper = true;
        mLooperFlag = VIDEO_FLAG_NEXT;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DynamicBean bean = mDatas.get(position);
        Log.d(TAG, bean.toString() +"  btn可看："+ (holder.mBtn.getVisibility() == View.VISIBLE));
        int state = bean.getState();
        String tv_text = "";
        if (bean.getIsVideo()) {
            holder.mBtn.setVisibility(View.VISIBLE);
            switch (state) {
                case VIDEO_OPEN:
                    tv_text = position + "正在播放";
                    holder.mTv.setTextColor(0xffff0000);
                    holder.mBtn.setText("停");
                    break;
                case VIDEO_CLOSE:
                    tv_text = position + "停止";
                    holder.mTv.setTextColor(0xff454545);
                    holder.mBtn.setText("开");
                    break;
            }
        } else {
            tv_text = position + "这是图片";
            holder.mTv.setTextColor(0xff00ff00);
            holder.mBtn.setVisibility(View.GONE);
        }
        if (mItemClickListener != null) {
            holder.mRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(holder.getAdapterPosition(), bean);
                }
            });
            holder.mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onBtnClick(holder.getAdapterPosition(), bean);
                    //播放状态   0播放，1停止
                    int state = bean.getState();
                    switch (state) {
                        case VIDEO_OPEN:
                            bean.setState(VIDEO_CLOSE);
                            break;
                        case VIDEO_CLOSE:
                            if(mPositionPlay >= 0  && mPositionPlay <= mDatas.size()) {
                                mDatas.get(mPositionPlay).setState(VIDEO_CLOSE);//上次播放的要停止
                                notifyItemChanged(mPositionPlay);
                            }
                            mPositionPlay = holder.getAdapterPosition();
                            bean.setState(VIDEO_OPEN);
                            break;
                    }
                    notifyItemChanged(mPositionPlay);
                }
            });

        }
        holder.mTv.setText(tv_text);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTv;
        private ConstraintLayout mRoot;
        private Button mBtn;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.tv);
            mBtn = itemView.findViewById(R.id.btn);
            mRoot = itemView.findViewById(R.id.item_root);
        }
    }

    public void setNewData(List<DynamicBean> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void setNextData(List<DynamicBean> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void setmPositionPlay(int position) {
        mPositionPlay = position;
    }

    public int getPositionPlay() {
        return mPositionPlay;
    }

    public boolean isIsLooper() {
        return mIsLooper;
    }

    public void setIsLooper(boolean mIsLooper) {
        this.mIsLooper = mIsLooper;
    }

    public void setVideoClose(int position) {
        mDatas.get(position).setState(VIDEO_CLOSE);
        notifyItemChanged(position);
    }

    public void setVideoPlay(int position) {
        mDatas.get(position).setState(VIDEO_OPEN);
        notifyItemChanged(position);
    }

    public int getLooperFlag() {
        return mLooperFlag;
    }

    public void setLooperFlag(int mLooperFlag) {
        this.mLooperFlag = mLooperFlag;
    }

    public List<DynamicBean> getDatas() {
        return mDatas;
    }

    public void setItemClickListener(OnItemClickListener.OnItemClickListenerSub itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, DynamicBean bean);

        void onBtnClick(int position, DynamicBean bean);
        class OnItemClickListenerSub implements OnItemClickListener {

            @Override
            public void onItemClick(int position, DynamicBean bean) {

            }

            @Override
            public void onBtnClick(int position, DynamicBean bean) {

            }
        }
    }

}

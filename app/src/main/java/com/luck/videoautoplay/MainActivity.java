package com.luck.videoautoplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        adapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setNewData(getData());
        mRecyclerView.addOnScrollListener(new onRecyclerViewScrollListener());
        adapter.setItemClickListener(new RecyclerAdapter.OnItemClickListener.OnItemClickListenerSub() {
            @Override
            public void onItemClick(int position, DynamicBean bean) {
                super.onItemClick(position, bean);
            }
        });
        setVideoListAutoPlayFirst();
    }

    /**
     * 设置自动播放当前可见页面里第一个非图片的视频
     * 只在页面一打开时调用
     * 注意 只有item高度小于屏幕高度才返回正常值
     */
    private void setVideoListAutoPlayFirst() {
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                boolean isCanPlay = false;
                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                while ((firstVisiblePosition < lastVisiblePosition - 1) && !isCanPlay) {
                    switch (adapter.getLooperFlag()) {
                        case RecyclerAdapter.VIDEO_FLAG_PREv:
                            //最后一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放倒数第2个可见的item
                            if (adapter.getDatas().get(lastVisiblePosition - 1).getIsVideo()) {
                                adapter.setmPositionPlay(lastVisiblePosition - 1);
                                isCanPlay = true;
                            }
                            lastVisiblePosition--;
                            break;
                        case RecyclerAdapter.VIDEO_FLAG_NEXT:
                            //第一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放第2个可见的item
                            if (adapter.getDatas().get(firstVisiblePosition).getIsVideo()) {
                                adapter.setmPositionPlay(firstVisiblePosition);
                                isCanPlay = true;
                            }
                            firstVisiblePosition++;
                            break;

                    }
                }
                if(isCanPlay) {
                    adapter.setVideoPlay(adapter.getPositionPlay());
                }
            }
        });
    }

    private List<DynamicBean> getData() {
        List<DynamicBean> list = new ArrayList<>();
        //播放状态   0播放，1停止
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, false));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, false));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, false));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, true));
        list.add(new DynamicBean(1, true));
        return list;
    }

    /**
     * 动态列表的滑动事件监听
     * 主要判断要关闭和播放的item位置
     */
    private class onRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            //滑动停止后，
            if (newState == RecyclerView.SCROLL_STATE_IDLE && adapter.isIsLooper() && adapter.getLooperFlag() != 0) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isSetting = handlePlay(adapter.getLooperFlag(),
                        layoutManager.findFirstVisibleItemPosition(),
                        layoutManager.findLastVisibleItemPosition());
                if (isSetting) {
                    adapter.setVideoPlay(adapter.getPositionPlay());
                }
                adapter.setLooperFlag(RecyclerAdapter.VIDEO_FLAG_NONE);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!adapter.isIsLooper()) return;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            View view = layoutManager.findViewByPosition(adapter.getPositionPlay());
            //说明播放的view还未完全消失
            if (view != null) {
                int y_t_rv = ScreenUtils.getViewScreenLocation(mRecyclerView)[1];//RV顶部Y坐标
                int y_b_rv = y_t_rv + mRecyclerView.getHeight();//RV底部Y坐标
                int y_t_view = ScreenUtils.getViewScreenLocation(view)[1];//播放的View顶部Y坐标
                int height_view = view.getHeight();
                int y_b_view = y_t_view + height_view;//播放的View底部Y坐标
                //上滑
                if (dy > 0) {
                    //播放的View上滑，消失了一半了,停止播放，
                    if ((y_t_rv > y_t_view) && ((y_t_rv - y_t_view) > height_view * 1f / 2)) {
                        adapter.setVideoClose(adapter.getPositionPlay());
                        adapter.setLooperFlag(RecyclerAdapter.VIDEO_FLAG_NEXT);
                    }
                } else if (dy < 0) {
                    //下滑
//                        LogUtils.log("y_t_rv", y_t_rv);
//                        LogUtils.log("y_b_rv", y_b_rv);
                    //播放的View下滑，消失了一半了,停止播放
                    if ((y_b_view > y_b_rv) && ((y_b_view - y_b_rv) > height_view * 1f / 2)) {
                        adapter.setVideoClose(adapter.getPositionPlay());
                        adapter.setLooperFlag(RecyclerAdapter.VIDEO_FLAG_PREv);
                    }
                }
            }
        }
    }
    /**
     * @param LooperFlag 是播放上一个还是播放下一个
     * @param firstPosition 当前可见第一个
     * @param lastPosition 当前可见最后一个
     * @return
     */
    public boolean handlePlay(int LooperFlag, int firstPosition, int lastPosition) {
        boolean isHavePlay = false;
        int firstVisiblePosition = firstPosition;
        int lastVisiblePosition = lastPosition;
        while ((firstVisiblePosition < lastVisiblePosition - 1) && !isHavePlay) {
            switch (LooperFlag) {
                case RecyclerAdapter.VIDEO_FLAG_PREv:
                    //最后一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放倒数第2个可见的item
                    if (adapter.getDatas().get(lastVisiblePosition - 1).getIsVideo()) {
                        adapter.setmPositionPlay(lastVisiblePosition - 1);
                        isHavePlay = true;
                    }
                    lastVisiblePosition--;
                    break;
                case RecyclerAdapter.VIDEO_FLAG_NEXT:
                    //第一个可见的item和滑出去的上次播放的view隔了N(N>=1)个Item,所以自动播放第2个可见的item
                    if (adapter.getDatas().get(firstVisiblePosition + 1).getIsVideo()) {
                        adapter.setmPositionPlay(firstVisiblePosition + 1);
                        isHavePlay = true;
                    }
                    firstVisiblePosition++;
                    break;

            }
        }
        return isHavePlay;
    }
}

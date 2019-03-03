package com.luck.videoautoplay;

/*************************************************************************************
 * Module Name:
 * Description:
 * Author: 李桐桐
 * Date:   2019/3/1
 *************************************************************************************/
public class DynamicBean {

    private int state ;//播放状态   0播放，1停止
    private boolean isVideo;

    public DynamicBean(int state, boolean isVideo) {
        this.isVideo = isVideo;
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean getIsVideo() {
        return isVideo;
    }

    @Override
    public String toString() {
        return "DynamicBean{" +
                "state=" + state +
                ", isVideo=" + isVideo +
                '}';
    }
}

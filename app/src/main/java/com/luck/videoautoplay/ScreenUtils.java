package com.luck.videoautoplay;

import android.graphics.RectF;
import android.view.View;

/*************************************************************************************
 * Module Name:
 * Description:
 * Author: 李桐桐
 * Date:   2019/3/1
 *************************************************************************************/
public class ScreenUtils {
    /**
     * 计算指定的 View 在屏幕中的坐标。
     */
    public static int[] getViewScreenLocation(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return location;
    }

    /**
     * 计算指定的 View 在屏幕中的范围。
     */
    public static RectF getViewScreenRectF(View view) {
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
        view.getLocationOnScreen(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }
}

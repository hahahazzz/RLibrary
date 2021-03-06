package com.angcyo.uiview.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import com.angcyo.uiview.recycler.RBaseViewHolder;

/**
 * 用来实现{@link UIItemUIView}界面中, 每个Item的布局
 * Created by angcyo on 2017-03-12.
 */

public interface Item {
    void onBindView(RBaseViewHolder holder, int posInData, Item dataBean);

    @Deprecated
    void setItemOffsets(Rect rect);

    void setItemOffsets2(Rect rect, int edge);

    void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position);

    /**
     * Item对应的布局id, -1表示默认
     */
    int getItemLayoutId();
}

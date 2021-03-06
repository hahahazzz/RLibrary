package com.angcyo.uiview.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;

import com.angcyo.uiview.R;
import com.angcyo.uiview.RApplication;
import com.angcyo.uiview.kotlin.ViewExKt;
import com.angcyo.uiview.resources.ResUtil;
import com.angcyo.uiview.skin.SkinHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/09 17:44
 * 修改人员：Robi
 * 修改时间：2017/05/09 17:44
 * 修改备注：
 * Version: 1.0.0
 */
public class Button extends RTextView {

    /**
     * 默认就是很小的圆角矩形填充样式
     */
    public static final int DEFAULT = 1;
    /**
     * 圆角矩形填充样式
     */
    public static final int ROUND = 2;
    /**
     * 可自定义的圆角边框样式
     */
    public static final int ROUND_BORDER = 3;
    /**
     * 正常边框, 按下主题颜色填充
     */
    public static final int ROUND_BORDER_FILL = 4;

    int mButtonStyle = DEFAULT;

    int themeSubColor;
    int themeDarkColor;
    int disableColor;


    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Button);
        mButtonStyle = typedArray.getInt(R.styleable.Button_r_button_style, DEFAULT);
        if (isInEditMode()) {
            themeSubColor = Color.BLUE;
            themeDarkColor = Color.BLUE;
            disableColor = Color.GRAY;
        } else {
            themeSubColor = typedArray.getInt(R.styleable.Button_r_button_theme_color, SkinHelper.getSkin().getThemeSubColor());
            themeDarkColor = typedArray.getInt(R.styleable.Button_r_button_theme_dark_color, SkinHelper.getSkin().getThemeDarkColor());
            disableColor = ContextCompat.getColor(getContext(), R.color.base_color_disable);
        }
        typedArray.recycle();

        initButton();
    }

    private void initButton() {
        if (getBackground() == null) {
            if (mButtonStyle == ROUND) {
                setBackground(ResUtil.ripple(themeSubColor,
                        ResUtil.selector(
                                ResUtil.createDrawable(themeSubColor, 300),
                                ResUtil.createDrawable(themeDarkColor, 300),
                                ResUtil.createDrawable(disableColor, 300)
                        )));
                setTextColor(getTextColors());
            } else if (mButtonStyle == ROUND_BORDER) {
                setBackground(ResUtil.ripple(themeSubColor,
                        ResUtil.selector(
                                ResUtil.createDrawable(themeSubColor, Color.TRANSPARENT, (int) (1 * density()), 300),
                                ResUtil.createDrawable(themeDarkColor, Color.TRANSPARENT, (int) (1 * density()), 300),
                                ResUtil.createDrawable(disableColor, Color.TRANSPARENT, (int) (1 * density()), 300)
                        )));
                setTextColor(ColorStateList.valueOf(themeSubColor));
            } else if (mButtonStyle == ROUND_BORDER_FILL) {
                setBackground(ResUtil.ripple(themeSubColor,
                        ResUtil.selector(
                                ResUtil.createDrawable(ViewExKt.getColor(this, R.color.default_base_line),
                                        Color.TRANSPARENT, ViewExKt.getDimensionPixelOffset(this, R.dimen.base_line),
                                        ViewExKt.getDimensionPixelOffset(this, R.dimen.base_round_little_radius)),
                                ResUtil.createDrawable(themeSubColor,
                                        ViewExKt.getDimensionPixelOffset(this, R.dimen.base_round_little_radius)),
                                ResUtil.createDrawable(disableColor,
                                        ViewExKt.getDimensionPixelOffset(this, R.dimen.base_round_little_radius))
                        )));
                setTextColor(ResUtil.generateTextColor(getCurrentTextColor(), ViewExKt.getColor(this, R.color.base_text_color)));
            } else {
                setTextColor(getTextColors());
                if (isInEditMode()) {
                    setBackground(ResUtil.generateRippleRoundMaskDrawable(getResources().getDimensionPixelOffset(R.dimen.base_round_little_radius),
                            Color.WHITE, Color.BLUE, disableColor, Color.BLUE));
                } else {
                    //setBackground(SkinHelper.getSkin().getThemeMaskBackgroundRoundSelector());
                    setBackground(
                            ResUtil.generateRippleRoundMaskDrawable(RApplication.getApp().getResources().getDimensionPixelOffset(R.dimen.base_round_little_radius),
                                    Color.WHITE, themeDarkColor,
                                    ContextCompat.getColor(RApplication.getApp(), R.color.base_color_disable),
                                    themeSubColor
                            ));
                }
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();

        setGravity(Gravity.CENTER);
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if ((heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) &&
                getPaddingTop() == 0 && getPaddingBottom() == 0) {
            setMeasuredDimension(getMeasuredWidth(), getResources().getDimensionPixelOffset(R.dimen.base_title_bar_item_size));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}

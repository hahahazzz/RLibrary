package com.angcyo.uiview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.angcyo.uiview.R;
import com.angcyo.uiview.viewgroup.RRelativeLayout;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2016/12/17 12:41
 * 修改人员：Robi
 * 修改时间：2016/12/17 12:41
 * 修改备注：
 * Version: 1.0.0
 */
public class ItemInfoLayout extends RRelativeLayout {

    public static int DEFAULT_TEXT_SIZE = 16;//sp
    public static int DEFAULT_DARK_TEXT_SIZE = 14;//sp
    public static int DEFAULT_DRAW_PADDING_SIZE = 10;//dp

    public static int DEFAULT_TEXT_COLOR = Color.parseColor("#333333");
    public static int DEFAULT_DARK_TEXT_COLOR = Color.parseColor("#999999");
    RTextView mTextView, mDarkTextView;
    ImageView mImageView;//扩展字段,用来显示一张网络图片 星期二 2017-2-21
    View mLineView;//用来显示底部分割线
    /**
     * 主要的文本信息属性
     */
    private String itemText, itemTag;
    private int itemTextSize;//px
    private int itemTextColor;
    /**
     * 次要的文本信息属性
     */
    private String itemDarkText, itemDarkTag;
    private int itemDarkTextSize;//px
    private int itemDarkTextColor;
    private int itemDarkId = View.NO_ID;
    /**
     * 文本与图标的距离,px
     */
    private int leftDrawPadding, rightDrawPadding;
    /**
     * 左边的图标, 右边的图标, 附加的图标
     */
    @DrawableRes
    private int leftDrawableRes = -1, rightDrawableRes = -1, darkDrawableRes = -1;

    /***
     * 图片的大小, 用来显示小红点
     */
    private int darkImageSize = 48;//dp

    /**
     * 是否是红点模式
     */
    private boolean isRedDotMode = false;
    private boolean showLine = false;

    public ItemInfoLayout(Context context) {
        this(context, null);
    }

    public ItemInfoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ItemInfoLayout);

        itemText = array.getString(R.styleable.ItemInfoLayout_item_text);
        itemTextSize = (int) array.getDimension(R.styleable.ItemInfoLayout_item_textSize, dpToPx(DEFAULT_TEXT_SIZE));
        itemTextColor = array.getColor(R.styleable.ItemInfoLayout_item_textColor, DEFAULT_TEXT_COLOR);

        itemDarkText = array.getString(R.styleable.ItemInfoLayout_item_dark_text);
        itemDarkTextSize = (int) array.getDimension(R.styleable.ItemInfoLayout_item_dark_textSize, dpToPx(DEFAULT_DARK_TEXT_SIZE));
        itemDarkTextColor = array.getColor(R.styleable.ItemInfoLayout_item_dark_textColor, DEFAULT_DARK_TEXT_COLOR);

        leftDrawPadding = (int) array.getDimension(R.styleable.ItemInfoLayout_item_left_draw_padding, DEFAULT_DRAW_PADDING_SIZE);
        rightDrawPadding = (int) array.getDimension(R.styleable.ItemInfoLayout_item_right_draw_padding, DEFAULT_DRAW_PADDING_SIZE);

        leftDrawableRes = array.getResourceId(R.styleable.ItemInfoLayout_item_left_res, leftDrawableRes);
        rightDrawableRes = array.getResourceId(R.styleable.ItemInfoLayout_item_right_res, rightDrawableRes);
        darkDrawableRes = array.getResourceId(R.styleable.ItemInfoLayout_item_dark_res, darkDrawableRes);
        itemDarkId = array.getResourceId(R.styleable.ItemInfoLayout_item_dark_id, View.NO_ID);

        itemDarkTag = array.getString(R.styleable.ItemInfoLayout_item_dark_tag);
        itemTag = array.getString(R.styleable.ItemInfoLayout_item_text_tag);

        darkImageSize = array.getDimensionPixelOffset(R.styleable.ItemInfoLayout_item_dark_image_size, darkImageSize);
        isRedDotMode = array.getBoolean(R.styleable.ItemInfoLayout_item_is_red_dot_mode, false);

        array.recycle();

        initLayout();

        if (isInEditMode()) {
            if (TextUtils.isEmpty(itemText)) {
                setItemText("一条Item测试数据");
            }
            //setRightDrawableRes(R.drawable.base_next);
            //setRedDotMode(true);
        }
    }

    static void resize(View view, int size, int margin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.rightMargin = margin;
        layoutParams.width = size;
        layoutParams.height = size;
        view.setLayoutParams(layoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(getMeasuredWidth(),
                    getResources().getDimensionPixelOffset(R.dimen.default_button_height) + getPaddingTop() + getPaddingBottom());
        }
    }

    public float dpToPx(float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
        return px;
    }

    private void initLayout() {
        mTextView = new RTextView(getContext());
        mDarkTextView = new RTextView(getContext());
        mImageView = new ImageView(getContext());
        mLineView = new View(getContext());
        mLineView.setVisibility(showLine ? View.VISIBLE : View.GONE);
        mLineView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.base_chat_bg_color));

        mTextView.setTag(itemTag);
        mTextView.setText(itemText);
        mTextView.setTextColor(itemTextColor);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
        mTextView.setCompoundDrawablePadding(leftDrawPadding);
        mTextView.setGravity(Gravity.CENTER_VERTICAL);
        setLeftDrawableRes(leftDrawableRes);

        mDarkTextView.setId(itemDarkId);
        mDarkTextView.setTag(itemDarkTag);
        mDarkTextView.setText(itemDarkText);
        mDarkTextView.setTextColor(itemDarkTextColor);
        mDarkTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemDarkTextSize);
        mDarkTextView.setCompoundDrawablePadding(rightDrawPadding);
        mDarkTextView.setGravity(Gravity.CENTER_VERTICAL);
        mDarkTextView.setMaxLines(1);
        mDarkTextView.setSingleLine(true);
        mDarkTextView.setEllipsize(TextUtils.TruncateAt.END);
        mDarkTextView.setMaxLength(20);
        mDarkTextView.setMaxWidth((int) (getResources().getDisplayMetrics().density * 260));

        setRightDrawableRes(rightDrawableRes);
        setDarkDrawableRes(darkDrawableRes);

        LayoutParams params = new LayoutParams(-2, -2);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        LayoutParams paramsDark = new LayoutParams(-2, -2);
        paramsDark.addRule(RelativeLayout.CENTER_VERTICAL);
        paramsDark.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        int size = (int) dpToPx(darkImageSize);
        LayoutParams imageParam = new LayoutParams(size, size);
        imageParam.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (isRedDotMode) {
            imageParam.setMargins(0, 0, (int) dpToPx(20), 0);
        } else {
            int padding = (int) dpToPx(2);
            imageParam.setMargins(0, 0, -padding, 0);
            mImageView.setPadding(0, padding, 0, padding);
        }
        //mImageView.setBackgroundColor(Color.RED);

        LayoutParams lineParam = new LayoutParams(-1, getResources().getDimensionPixelOffset(R.dimen.base_line));
        lineParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        addView(mTextView, params);
        addView(mDarkTextView, paramsDark);
        addView(mImageView, imageParam);
        addView(mLineView, lineParam);
    }

    /**
     * 在右边添加一个View
     */
    public ItemInfoLayout addRightView(View view, int width, int height, int rightMargin) {
        LayoutParams imageParam = new LayoutParams(width, height);
        imageParam.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageParam.rightMargin = rightMargin;
        addView(view, imageParam);
        return this;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public RTextView getTextView() {
        return mTextView;
    }

    public RTextView getDarkTextView() {
        return mDarkTextView;
    }

    public ItemInfoLayout setDarkDrawableRes(int darkDrawableRes) {
        this.darkDrawableRes = darkDrawableRes;
        final Drawable[] compoundDrawables = mDarkTextView.getCompoundDrawables();
        if (darkDrawableRes == -1) {
            mDarkTextView.setCompoundDrawablesWithIntrinsicBounds(
                    null, compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        } else {
            mDarkTextView.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(darkDrawableRes),
                    compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
        return this;
    }

    public ItemInfoLayout setLeftDrawPadding(int leftDrawPadding) {
        this.leftDrawPadding = leftDrawPadding;
        mTextView.setCompoundDrawablePadding(leftDrawPadding);
        return this;
    }

    public ItemInfoLayout setRightDrawPadding(int rightDrawPadding) {
        this.rightDrawPadding = rightDrawPadding;
        mDarkTextView.setCompoundDrawablePadding(leftDrawPadding);
        return this;
    }

    public ItemInfoLayout setRightDrawableRes(int rightDrawableRes) {
        this.rightDrawableRes = rightDrawableRes;
        final Drawable[] compoundDrawables = mDarkTextView.getCompoundDrawables();
        if (rightDrawableRes == -1) {
            mDarkTextView.setCompoundDrawablesWithIntrinsicBounds(
                    compoundDrawables[0], compoundDrawables[1], null, compoundDrawables[3]);
        } else {
            mDarkTextView.setCompoundDrawablesWithIntrinsicBounds(
                    compoundDrawables[0], compoundDrawables[1],
                    getResources().getDrawable(rightDrawableRes), compoundDrawables[3]);
        }
        return this;
    }

    public ItemInfoLayout setLeftDrawableRes(int leftDrawableRes) {
        this.leftDrawableRes = leftDrawableRes;
        final Drawable[] compoundDrawables = mTextView.getCompoundDrawables();
        if (leftDrawableRes == -1) {
            mTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                    compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        } else {
            mTextView.setCompoundDrawablesWithIntrinsicBounds(
                    getResources().getDrawable(leftDrawableRes),
                    compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
        }
        return this;
    }

    public ItemInfoLayout setItemDarkText(String itemDarkText) {
        this.itemDarkText = itemDarkText;
        mDarkTextView.setText(itemDarkText);
        return this;
    }

    public ItemInfoLayout setItemDarkTag(String darkTag) {
        itemDarkTag = darkTag;
        mDarkTextView.setTag(darkTag);
        return this;
    }

    public ItemInfoLayout setItemTextTag(String tag) {
        itemTag = tag;
        mTextView.setTag(tag);
        return this;
    }

    public ItemInfoLayout setItemText(String itemText) {
        this.itemText = itemText;
        mTextView.setText(itemText);
        return this;
    }

    public ItemInfoLayout setDarkImageSize(int darkImageSize) {
        this.darkImageSize = darkImageSize;
        return this;
    }

    public ItemInfoLayout setRedDotMode(boolean redDotMode) {
        boolean oldMode = isRedDotMode;

        isRedDotMode = redDotMode;

        if (mImageView != null) {
            if (isRedDotMode) {
                int offset = getResources().getDimensionPixelOffset(R.dimen.base_hdpi);
                if (rightDrawableRes == -1) {
                    resize(mImageView, offset, 0);
                } else {
                    resize(mImageView, offset, offset * 3);
                }
                mImageView.setBackgroundResource(R.drawable.base_red_circle_shape);
            } else {
                if (oldMode) {
                    mImageView.setBackground(null);
                }
            }
        }
        return this;
    }

    public View getLineView() {
        return mLineView;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
        if (mLineView != null) {
            mLineView.setVisibility(showLine ? View.VISIBLE : View.GONE);
        }
    }
}

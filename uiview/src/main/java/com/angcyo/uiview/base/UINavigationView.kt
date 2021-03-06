package com.angcyo.uiview.base

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.angcyo.uiview.R
import com.angcyo.uiview.container.ContentLayout
import com.angcyo.uiview.container.UILayoutImpl
import com.angcyo.uiview.container.UIParam
import com.angcyo.uiview.model.TitleBarPattern
import com.angcyo.uiview.widget.TouchMoveGroupLayout
import com.angcyo.uiview.widget.TouchMoveView

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：自带类似QQ底部导航效果的主页面
 * 创建人员：Robi
 * 创建时间：2017/06/06 10:51
 * 修改人员：Robi
 * 修改时间：2017/06/06 10:51
 * 修改备注：
 * Version: 1.0.0
 */
abstract class UINavigationView : UIContentView() {

    /**默认选择的页面*/
    var selectorPosition = 0
        set(value) {
            touchMoveGroupLayout?.selectorPosition = selectorPosition
        }

    /**保存所有界面*/
    var pages = arrayListOf<PageBean>()

    var lastIndex = selectorPosition

    override fun getTitleBar(): TitleBarPattern? {
        return null
    }

    /**导航的root layout, 用来包裹 TouchMoveView*/
    val touchMoveGroupLayout: TouchMoveGroupLayout? by lazy {
        mViewHolder.v<TouchMoveGroupLayout>(R.id.navigation_bar_wrapper)
    }

    /**底部导航上层的阴影图层*/
    val shadowView: View? by lazy {
        val view = mViewHolder.v<View>(R.id.shadow_view)
        view?.visibility = if (showShadowView) View.VISIBLE else View.GONE
        view
    }

    /**用来管理子页面的ILayout*/
    val mainLayoutImpl: UILayoutImpl? by lazy {
        val impl = mViewHolder.v<UILayoutImpl>(R.id.main_layout_imp)
        setChildILayout(impl)
        impl
    }

    var showShadowView = true
        set(value) {
            field = value
            shadowView?.visibility = if (value) View.VISIBLE else View.GONE
        }

    override fun inflateContentLayout(baseContentLayout: ContentLayout?, inflater: LayoutInflater?) {
        inflate(R.layout.base_navigation_view)
    }

    override fun onViewLoad() {
        super.onViewLoad()
        mainLayoutImpl?.setEnableSwipeBack(false)//关闭侧滑
        createPages(pages)
        onCreatePages()
    }

    fun onCreatePages() {
        pages?.forEach {
            touchMoveGroupLayout?.addView(createNavItem(it))
        }
        touchMoveGroupLayout?.selectorPosition = selectorPosition
        touchMoveGroupLayout?.updateSelectorStyle()
        touchMoveGroupLayout?.listener = object : TouchMoveGroupLayout.OnSelectorPositionListener {
            override fun onRepeatSelectorPosition(targetView: TouchMoveView, position: Int) {
                //重复选择页面
                this@UINavigationView.onRepeatSelectorPosition(targetView, position)
            }

            override fun onSelectorPosition(targetView: TouchMoveView, position: Int) {
                //选择新页面
                mainLayoutImpl?.startIView(pages?.get(position)?.iview?.setIsRightJumpLeft(lastIndex > position),
                        UIParam(lastIndex != position).setLaunchMode(UIParam.SINGLE_TOP))

                lastIndex = position
                this@UINavigationView.onSelectorPosition(targetView, position)
            }
        }
    }

    open fun createNavItem(page: PageBean): TouchMoveView {
        val view = TouchMoveView(mActivity)
        view.textNormal = page.textNormal
        view.textSelected = page.textSelected

        if (page.textColorNormal != null) {
            view.mTextColorNormal = page.textColorNormal
        }
        if (page.textColorSelected != null) {
            view.mTextColorSelected = page.textColorSelected
        }
        if (page.icoResNormal != null) {
            view.mDrawableNormal = getDrawable(page.icoResNormal)
        } else {
            view.mDrawableNormal = null
        }
        if (page.icoResSelected != null) {
            view.mDrawableSelected = getDrawable(page.icoResSelected)
        } else {
            view.mDrawableSelected = null
        }
        if (page.icoSubResNormal != null) {
            view.mSubDrawableNormal = getDrawable(page.icoSubResNormal)
        } else {
            view.mSubDrawableNormal = null
        }
        if (page.icoSubResSelected != null) {
            view.mSubDrawableSelected = getDrawable(page.icoSubResSelected)
        } else {
            view.mSubDrawableSelected = null
        }
        page.iview.bindParentILayout(iLayout)
        return view
    }

    /**创建页面*/
    abstract fun createPages(pages: ArrayList<PageBean>)

    open fun onSelectorPosition(targetView: TouchMoveView, position: Int) {
    }

    open fun onRepeatSelectorPosition(targetView: TouchMoveView, position: Int) {
    }

    /**页面*/
    data class PageBean(
            val iview: UIBaseView,
            val textNormal: String? = null,
            val textSelected: String? = null,
            val textColorNormal: Int? = null,
            val textColorSelected: Int? = null,
            val icoResNormal: Int? = null,
            val icoResSelected: Int? = null,
            val icoSubResNormal: Int? = null,
            val icoSubResSelected: Int? = null
    ) {
        constructor(iview: UIBaseView,
                    textNormal: String? = null,
                    icoResNormal: Int? = null
        ) : this(iview, textNormal, textNormal, null, null, icoResNormal, icoResNormal, null, null)

        constructor(iview: UIBaseView,
                    textNormal: String? = null,
                    textColorNormal: Int? = null,
                    textColorSelected: Int? = null,
                    icoResNormal: Int? = null
        ) : this(iview, textNormal, textNormal, textColorNormal, textColorSelected, icoResNormal, icoResNormal, null, null)
    }
}

package com.angcyo.uiview.viewgroup

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.OverScroller

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：支持 四方向手势
 * 创建人员：Robi
 * 创建时间：2017/10/13 14:59
 * 修改人员：Robi
 * 修改时间：2017/10/13 14:59
 * 修改备注：
 * Version: 1.0.0
 */
open class TouchLayout(context: Context, attributeSet: AttributeSet? = null) : FrameLayout(context, attributeSet) {

    companion object {
        /**当滚动距离大于多少时, 视为滚动了*/
        const val scrollDistanceSlop = 0
        /**当Fling速度大于多少时, 视为Fling*/
        const val flingVelocitySlop = 0

        const val HANDLE_TOUCH_TYPE_DISPATCH = 1
        const val HANDLE_TOUCH_TYPE_INTERCEPT = 2
        const val HANDLE_TOUCH_TYPE_TOUCH = 3
    }

    /**4个方向*/
    enum class ORIENTATION {
        LEFT, RIGHT, TOP, BOTTOM
    }

    /**采用什么方式, 处理touch事件 */
    var handleTouchType = HANDLE_TOUCH_TYPE_INTERCEPT

    protected val overScroller = OverScroller(context)

    /*用来检测手指滑动方向*/
    protected val orientationGestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            //L.e("call: onFling -> \n$e1 \n$e2 \n$velocityX $velocityY")

            val absX = Math.abs(velocityX)
            val absY = Math.abs(velocityY)

            if (absX > flingVelocitySlop || absY > flingVelocitySlop) {
                if (absY > absX) {
                    //竖直方向的Fling操作
                    onFlingChange(if (velocityY > 0) ORIENTATION.BOTTOM else ORIENTATION.TOP, velocityY)
                } else if (absX > absY) {
                    //水平方向的Fling操作
                    onFlingChange(if (velocityX > 0) ORIENTATION.RIGHT else ORIENTATION.LEFT, velocityX)
                }
            }

            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            //L.e("call: onScroll -> \n$e1 \n$e2 \n$distanceX $distanceY")

            val absX = Math.abs(distanceX)
            val absY = Math.abs(distanceY)

            if (absX > scrollDistanceSlop || absY > scrollDistanceSlop) {
                if (absY > absX) {
                    //竖直方向的Scroll操作
                    onScrollChange(if (distanceY > 0) ORIENTATION.TOP else ORIENTATION.BOTTOM, distanceY)
                } else if (absX > absY) {
                    //水平方向的Scroll操作
                    onScrollChange(if (distanceX > 0) ORIENTATION.LEFT else ORIENTATION.RIGHT, distanceX)
                }
            }

            return true
        }

    })

    @CallSuper
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            overScroller.abortAnimation()
        }
        if (handleTouchType == HANDLE_TOUCH_TYPE_DISPATCH) {
            orientationGestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    @CallSuper
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (handleTouchType == HANDLE_TOUCH_TYPE_INTERCEPT) {
            orientationGestureDetector.onTouchEvent(ev)
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        orientationGestureDetector.onTouchEvent(event)
        return true
    }

    override fun computeScroll() {
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.currX, overScroller.currY)
            postInvalidate()
        }
    }

    open fun startScrollY(dy: Int) {
        startScroll(0, dy)
    }

    open fun startScrollX(dx: Int) {
        startScroll(dx, 0)
    }

    open fun startScroll(dx: Int, dy: Int) {
        overScroller.startScroll(scrollX, scrollY, dx, dy, 300)
        postInvalidate()
    }

    open fun startFlingY(velocityY: Int, maxDy: Int) {
        startFling(0, velocityY, 0, maxDy)
    }

    open fun startFlingX(velocityX: Int, maxDx: Int) {
        startFling(velocityX, 0, maxDx, 0)
    }

    open fun startFling(velocityX: Int, velocityY: Int, maxDx: Int, maxDy: Int) {
        overScroller.fling(scrollX, scrollY, velocityX, velocityY, 0, maxDx, 0, maxDy, measuredWidth, measuredHeight)
        postInvalidate()
    }

    fun isVertical(orientation: ORIENTATION) = orientation == ORIENTATION.TOP || orientation == ORIENTATION.BOTTOM
    fun isHorizontal(orientation: ORIENTATION) = orientation == ORIENTATION.LEFT || orientation == ORIENTATION.RIGHT

    /**Fling操作的处理方法*/
    open fun onFlingChange(orientation: ORIENTATION, velocity: Float) {
        //L.e("call: onFlingChange -> $orientation $velocity")
    }

    /**Scroll操作的处理方法*/
    open fun onScrollChange(orientation: ORIENTATION, distance: Float) {
        //L.e("call: onScrollChange -> $orientation $distance")
    }
}
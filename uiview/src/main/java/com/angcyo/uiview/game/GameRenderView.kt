package com.angcyo.uiview.game

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.angcyo.uiview.game.layer.BaseLayer
import com.angcyo.uiview.kotlin.density
import com.angcyo.uiview.resources.RAnimListener

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：按照60帧的速度一直绘制的游戏渲染View
 * 创建人员：Robi
 * 创建时间：2017/12/15 10:41
 * 修改人员：Robi
 * 修改时间：2017/12/15 10:41
 * 修改备注：
 * Version: 1.0.0
 */
class GameRenderView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {
    val layerList = mutableListOf<BaseLayer>()

    /**onDetachedFromWindow*/
    var isGameViewDetached = true

    /**是否在渲染中*/
    var isGameRenderStart = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isGameViewDetached = false
        startRender()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isGameViewDetached = true
        endRender()
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            startRender()
        } else {
            endRender()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        startRender()

        for (i in 0 until layerList.size) {
            layerList[i].onSizeChanged(w, h, oldw, oldh)
        }
    }

    /*多点控制*/
    private val pointList = SparseArray<PointF>()
    private val deviation = 10

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val actionIndex = event.actionIndex
        val id = event.getPointerId(actionIndex)

        val eventX = event.getX(actionIndex)
        val eventY = event.getY(actionIndex)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                pointList.put(id, PointF(eventX, eventY))
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val p = pointList.get(id)
                if (Math.abs(p.x - eventX) < deviation &&
                        Math.abs(p.y - eventY) < deviation) {
                    for (i in layerList.size - 1 downTo 0) {
                        val touchEvent = layerList[i].onTouchEvent(event, p)
                        if (touchEvent) {
                            break
                        }
                    }
                }
                pointList.remove(id)
            }
        }

        return true /*super.onTouchEvent(event)*/
    }

    private var showFps = true
    private val fpsPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14 * density
            color = Color.RED
            strokeWidth = 1 * density
            style = Paint.Style.FILL_AND_STROKE
        }
    }

    private fun time(): Long = System.currentTimeMillis()

    private var lastRenderTime = 0L
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val nowTime = time()
        for (i in 0 until layerList.size) {
            layerList[i].draw(canvas, gameRenderStartTime, lastRenderTime, nowTime)
        }
        if (showFps) {
            val text = fpsText
            canvas.drawText(text, 10 * density, measuredHeight - 10 * density, fpsPaint)
        }
        lastRenderTime = nowTime
    }

    private var fps = 0
    private var fpsText = ""
    private val renderAnim: ValueAnimator by lazy {
        ValueAnimator.ofInt(0, 60).apply {
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 1000
            addListener(object : RAnimListener() {
                override fun onAnimationRepeat(animation: Animator?) {
                    super.onAnimationRepeat(animation)
                    fpsText = "$fps"
                    //L.e("call: onAnimationRepeat -> fps:$fpsText")
                    fps = 0
                }
            })
            addUpdateListener {
                fps++
                postInvalidateOnAnimation()
            }
        }
    }

    /*开始渲染的时间*/
    private var gameRenderStartTime = 0L

    /*开始渲染界面*/
    private fun startRender() {
        if (measuredWidth == 0 || measuredHeight == 0) {
            isGameRenderStart = false
            return
        }

        if (isGameViewDetached) {
            isGameRenderStart = false
            return
        }

        if (visibility != View.VISIBLE) {
            isGameRenderStart = false
            return
        }

        if (isGameRenderStart) {
            return
        }

        isGameRenderStart = true

        if (renderAnim.isStarted) {
        } else {
            gameRenderStartTime = time()
            renderAnim.start()
        }

        for (i in 0 until layerList.size) {
            layerList[i].onRenderStart(this)
        }
    }

    /**结束渲染*/
    fun endRender() {
        if (isGameRenderStart) {
            gameRenderStartTime = 0L
            renderAnim.cancel()
            isGameRenderStart = false

            for (i in 0 until layerList.size) {
                layerList[i].onRenderEnd(this)
            }
        }
    }
}
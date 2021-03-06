package com.angcyo.uiview.view

import android.view.View
import com.angcyo.uiview.R
import com.angcyo.uiview.RApplication
import com.angcyo.uiview.receiver.NetworkStateReceiver
import com.angcyo.uiview.utils.T_

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：可以延迟点击, 无网络判断的点击事件.
 * 创建人员：Robi
 * 创建时间：2017/07/20 15:47
 * 修改人员：Robi
 * 修改时间：2017/07/20 15:47
 * 修改备注：
 * Version: 1.0.0
 */
abstract class RClickListener : View.OnClickListener {

    companion object {
        val DEFAULT_DELAY_CLICK_TIME = 300
    }

    /**快速点击间隔时间(毫秒), 0表示不延迟处理*/
    var delayTime = DEFAULT_DELAY_CLICK_TIME

    /**是否只在有网络的情况下可以点击*/
    var checkNet = false

    constructor() {
        this.delayTime = DEFAULT_DELAY_CLICK_TIME
    }

    constructor(delayTime: Int) {
        this.delayTime = delayTime
    }

    constructor(delayTime: Int, checkNet: Boolean) {
        this.delayTime = delayTime
        this.checkNet = checkNet
    }

    constructor(checkNet: Boolean) {
        this.checkNet = checkNet
    }

    /*点击的时间, 用来判断延迟*/
    private var clickTime = 0L

    final override fun onClick(v: View?) {
        if (NetworkStateReceiver.getNetType().value() < 2) {

            if (checkNet) {
                //3G网络, 网络不佳
                onNetPoor()
                return
            }
        }

        val time = System.currentTimeMillis()
        if (time - clickTime >= delayTime) {
            clickTime = time
            onRClick(v)
        }
    }

    abstract fun onRClick(view: View?)

    /**网络不佳的回调*/
    open fun onNetPoor() {
        T_.error(RApplication.getApp().getString(R.string.base_net_poor_tip))
    }

}
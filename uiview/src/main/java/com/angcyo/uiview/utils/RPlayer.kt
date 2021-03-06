package com.angcyo.uiview.utils

import android.media.AudioManager
import android.media.MediaPlayer
import com.angcyo.library.utils.L
import java.util.concurrent.atomic.AtomicInteger

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：播放音乐工具类
 * 创建人员：Robi
 * 创建时间：2017/10/25 16:20
 * 修改人员：Robi
 * 修改时间：2017/10/25 16:20
 * 修改备注：
 * Version: 1.0.0
 */
class RPlayer {
    private var mediaPlay: MediaPlayer? = null

    /**是否循环播放*/
    var isLoop = false

    var onPlayListener: OnPlayerListener? = null

    var audioStreamType = AudioManager.STREAM_MUSIC

    var leftVolume: Float = 0.5f
    var rightVolume: Float = 0.5f

    /**正在播放的url*/
    private var playUrl = ""

    /**当前播放的状态*/
    private var playState: AtomicInteger = AtomicInteger(STATE_INIT)

    companion object {
        //初始化状态
        const val STATE_INIT = 0
        /**正常情况*/
        const val STATE_NORMAL = 1
        /**播放中*/
        const val STATE_PLAYING = 2
        /**停止播放*/
        const val STATE_STOP = 3
        /**资源释放*/
        const val STATE_RELEASE = 4

        const val STATE_PAUSE = 5

        /**播放完成*/
        const val STATE_COMPLETION = 6
        const val STATE_ERROR = -1
    }

    private var seekToPosition = -1

    @Synchronized
    fun init() {
        if (mediaPlay == null) {
            mediaPlay = MediaPlayer()
        }
    }

    //开始播放
    private fun startPlayInner(mediaPlay: MediaPlayer) {
        setPlayState(STATE_PLAYING)
        startProgress()
        mediaPlay.start()
    }

    /**@param url 可是有效的网络, 和有效的本地地址*/
    fun startPlay(url: String) {
        if (playUrl == url) {
            if (isPlayCall()) {

            } else {
                mediaPlay?.let {
                    startPlayInner(it)
                }
            }
            return
        } else {
            stopPlay()
        }
        if (mediaPlay == null) {
            init()
        }
        mediaPlay?.let {
            it.isLooping = isLoop
            it.setAudioStreamType(audioStreamType)
            it.setVolume(leftVolume, rightVolume)

            it.setOnErrorListener { mp, what, extra ->
                //L.e("call: startPlay -> $what $extra")
                setPlayState(STATE_ERROR)
                onPlayListener?.onPlayError(what, extra)

                it.reset()
                true
            }
            it.setOnCompletionListener {
                setPlayState(STATE_COMPLETION)
                onPlayListener?.onPlayCompletion(it.duration)
                it.reset()
            }
            it.setOnPreparedListener {
                //L.e("call: startPlay -> onPrepared ${it.duration}")
                onPlayListener?.onPreparedCompletion(it.duration)
                if (playState.get() == STATE_NORMAL) {
                    startPlayInner(it)
                    playSeekTo(seekToPosition)
                }
            }
            it.setDataSource(url)
            playUrl = url

            setPlayState(STATE_NORMAL)
            it.prepareAsync()
        }
    }

    /**停止播放, 不释放资源, 下次可以重新setDataSource*/
    fun stopPlay() {

        mediaPlay?.let {
            if (isPlaying()) {
                it.stop()
            }
            it.reset()
        }

        setPlayState(STATE_STOP)
    }

    fun pausePlay() {

        mediaPlay?.let {
            if (isPlaying()) {
                it.pause()
            }
        }

        setPlayState(STATE_PAUSE)
    }

    /**释放资源, 下次需要重新创建*/
    fun release() {
        setPlayState(STATE_RELEASE)
        stopPlay()
        mediaPlay?.let {
            it.release()
        }
        mediaPlay = null
    }

    /**设置音量*/
    fun setVolume(value: Float) {
        leftVolume = value
        rightVolume = value
        mediaPlay?.let {
            it.setVolume(value, value)
        }
    }

    /**正在播放中, 解析也完成了*/
    fun isPlaying() = playState.get() == STATE_PLAYING

    /**是否调用了播放, 但是有可能还在解析数据中*/
    fun isPlayCall() = (playState.get() == STATE_PLAYING || playState.get() == STATE_NORMAL)

    fun isPause() = playState.get() == STATE_PAUSE

    private fun setPlayState(state: Int) {
        val oldState = playState.get()
        playState.set(state)

        when (state) {
            STATE_STOP, STATE_RELEASE, STATE_ERROR, STATE_COMPLETION -> playUrl = ""
        }

        L.d("RPlayer: onPlayStateChange -> $oldState->$state")

        if (oldState != state) {
            onPlayListener?.onPlayStateChange(playUrl, oldState, state)
        }
    }

    /**播放中的进度, 毫秒*/
    var currentPosition = 0

    /*开始进度读取*/
    private fun startProgress() {
        Thread(Runnable {
            while ((isPlayCall() || isPause()) &&
                    mediaPlay != null &&
                    onPlayListener != null) {
                ThreadExecutor.instance().onMain {
                    if (isPlaying() && mediaPlay != null) {
                        currentPosition = mediaPlay!!.currentPosition
                        L.d("RPlayer: startProgress -> $currentPosition:$mediaPlay!!.duration")
                        onPlayListener?.onPlayProgress(currentPosition, mediaPlay!!.duration)
                    }
                }
                try {
                    Thread.sleep(300)
                } catch (e: Exception) {
                }
            }
        }).apply {
            start()
        }
    }

    interface OnPlayerListener {
        /**@param duration 媒体总时长 毫秒*/
        fun onPreparedCompletion(duration: Int)

        /**播放进度回调*/
        fun onPlayProgress(progress: Int, duration: Int)

        /**播放完成*/
        fun onPlayCompletion(duration: Int)

        /**播放错误*/
        fun onPlayError(what: Int, extra: Int)

        /**播放状态回调*/
        fun onPlayStateChange(playUrl: String, from: Int, to: Int)

    }

    fun playSeekTo(msec: Int /*毫秒*/) {
        seekToPosition = msec
        if (msec >= 0 && playState.get() == STATE_PLAYING) {
            mediaPlay?.let {
                it.seekTo(msec)
                seekToPosition = -1
            }
        }
    }
}

abstract class SimplePlayerListener : RPlayer.OnPlayerListener {
    override fun onPreparedCompletion(duration: Int) {
    }

    override fun onPlayProgress(progress: Int, duration: Int) {
    }

    override fun onPlayCompletion(duration: Int) {
    }

    override fun onPlayError(what: Int, extra: Int) {
    }

    override fun onPlayStateChange(playUrl: String, from: Int, to: Int) {
    }
}
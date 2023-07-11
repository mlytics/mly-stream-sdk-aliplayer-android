package com.mlytics.aliplayer.example

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import androidx.activity.ComponentActivity
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.source.UrlSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.layout_main)

        val aliPlayer = AliPlayerFactory.createAliPlayer(this)
//埋点日志上报功能默认开启，当traceId设置为DisableAnalytics时，则关闭埋点日志上报。当traceId设置为其他参数时，则开启埋点日志上报。
//建议传递traceId，便于跟踪日志。traceId为设备或用户的唯一标识符，通常为imei或idfa。
//埋点日志上报功能默认开启，当traceId设置为DisableAnalytics时，则关闭埋点日志上报。当traceId设置为其他参数时，则开启埋点日志上报。
//建议传递traceId，便于跟踪日志。traceId为设备或用户的唯一标识符，通常为imei或idfa。
        aliPlayer.setTraceId("aliPlayer")


        aliPlayer.setOnErrorListener { errorInfo ->

            //此回调会在使用播放器的过程中，出现了任何错误，都会回调此接口。
            val errorCode = errorInfo.code //错误码。
            val errorMsg = errorInfo.msg //错误描述。
            //出错后需要停止掉播放器。
            aliPlayer.stop()
        }
        aliPlayer.setOnPreparedListener { //一般调用start开始播放视频。
            aliPlayer.start()
        }
        aliPlayer.setOnCompletionListener { //一般调用stop停止播放视频。
            aliPlayer.stop()
        }
        aliPlayer.setOnInfoListener { infoBean ->

            //播放器中的一些信息，包括：当前进度、缓存位置等等。
            val code = infoBean.code //信息码。
            val msg = infoBean.extraMsg //信息内容。
            val value = infoBean.extraValue //信息值。

            //当前进度：InfoCode.CurrentPosition
            //当前缓存位置：InfoCode.BufferedPosition
        }
        aliPlayer.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
            //播放器的加载状态, 网络不佳时，用于展示加载画面。
            override fun onLoadingBegin() {
                //开始加载。画面和声音不足以播放。
                //一般在此处显示圆形加载。
            }

            override fun onLoadingProgress(percent: Int, netSpeed: Float) {
                //加载进度。百分比和网速。
            }

            override fun onLoadingEnd() {
                //结束加载。画面和声音可以播放。
                //一般在此处隐藏圆形加载。
            }
        })

        val urlSource = UrlSource()
        urlSource.uri = "https://vsp-stream.s3.ap-northeast-1.amazonaws.com/HLS/raw/SpaceX.m3u8" //播放地址，可以是第三方点播地址，或阿里云点播服务中的播放地址，也可以是本地视频地址。

        aliPlayer.setDataSource(urlSource)

        val surfaceView = findViewById<SurfaceView>(R.id.surface_view)
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                aliPlayer.setSurface(holder.surface)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                aliPlayer.surfaceChanged()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                aliPlayer.setSurface(null)
            }
        })

        playButton = findViewById(R.id.player_button)

        playButton.setOnClickListener {
            this.play(aliPlayer)
        }

    }

    fun play(aliPlayer: AliPlayer) {

        aliPlayer.isAutoPlay = true
        aliPlayer.prepare()
        aliPlayer.start()

    }

    lateinit var playButton: Button

}

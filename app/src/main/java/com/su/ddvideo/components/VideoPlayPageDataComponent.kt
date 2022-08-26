package com.su.ddvideo.components

import android.util.Log
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.su.ddvideo.danmaku.OyydsDanmaku
import com.su.ddvideo.danmaku.OyydsDanmakuParser
import com.su.ddvideo.util.Text.trimAll
import com.su.ddvideo.util.oyydsDanmakuApis
import com.su.mediabox.pluginapi.components.IVideoPlayPageDataComponent
import com.su.mediabox.pluginapi.data.VideoPlayMedia
import com.su.mediabox.pluginapi.util.PluginPreferenceIns
import com.su.mediabox.pluginapi.util.WebUtilIns
import org.jsoup.Jsoup

class VideoPlayPageDataComponent : IVideoPlayPageDataComponent {

//    companion object {
//        private val vregex = Regex("url\\\":\\\"(.*)\\\"\\}")
//    }

    private var episodeDanmakuId = ""
    private var videoType = ""
    override suspend fun getDanmakuData(
        videoName: String,
        episodeName: String,
        episodeUrl: String
    ): List<DanmakuItemData>? {
        try {
            val config = PluginPreferenceIns.get(OyydsDanmaku.OYYDS_DANMAKU_ENABLE, true)
            if (!config)
                return null
            var name = videoName.trimAll()
            //去除更新信息影响
            val nameIndex = name.indexOf("(")
            if (nameIndex != -1) {
                name = name.substring(0, nameIndex)
            }
            var episode = episodeName.trimAll()
            //剧集对集去除所有额外字符，增大弹幕适应性
            val episodeIndex = episode.indexOf("集")
            if (episodeIndex > -1 && episodeIndex != episode.length - 1) {
                episode = episode.substring(0, episodeIndex + 1)
            }
            //去除季度信息影响
            val episodeIndex2 = episode.indexOf("季")
            if (episodeIndex2 != -1 && episodeIndex2 != episode.length - 1) {
                name += episode.substring(0, episodeIndex2 + 1)
                episode = episode.substring(episodeIndex2 + 1)
            }
            Log.d("请求Oyyds弹幕", "媒体:$name 剧集:$episode")
            return oyydsDanmakuApis.getDanmakuData(
                name.trimAll(), episode.trimAll(),
                OyydsDanmakuParser.getType(videoType)
            ).data.let { danmukuData ->
                val data = mutableListOf<DanmakuItemData>()
                danmukuData?.data?.forEach { dataX ->
                    OyydsDanmakuParser.convert(dataX)?.also { data.add(it) }
                }
                episodeDanmakuId = danmukuData?.episode?.id ?: ""
                data
            }
        } catch (e: Exception) {
            throw RuntimeException("弹幕加载错误：${e.message}")
        }
    }

    override suspend fun putDanmaku(
        videoName: String,
        episodeName: String,
        episodeUrl: String,
        danmaku: String,
        time: Long,
        color: Int,
        type: Int
    ): Boolean = try {
        Log.d("发送弹幕到Oyyds", "内容:$danmaku 剧集id:$episodeDanmakuId")
        oyydsDanmakuApis.addDanmaku(
            danmaku,
            //Oyyds弹幕标准时间是秒
            (time / 1000F).toString(),
            episodeDanmakuId,
            OyydsDanmakuParser.danmakuTypeMap.entries.find { it.value == type }?.key ?: "scroll",
            String.format("#%02X", color)
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    override suspend fun getVideoPlayMedia(episodeUrl: String): VideoPlayMedia {
        //TODO 性能待优化
        val url = WebUtilIns.interceptResource(
            episodeUrl,
            actionJs = """  
                    setTimeout(function() {
	                    const evt = document.createEvent('MouseEvent');
	                    evt.initEvent('click', false, false);
	                    document.getElementsByClassName('vjs-big-play-button')[0].dispatchEvent(evt);   
                    },500);
            """.trimIndent(),
            //regex = "(.*)getvddr(.*)"
            regex = "(.*)filename(.*)"
        )

//        val vjson = Jsoup.connect(url).ignoreContentType(true)
//            .header("referer", Const.host)
//            .get().body().text().apply {
//                Log.i("数据", this)
//            }
//
//        if (vjson.contains("error"))
//            return VideoPlayMedia("", "")

        val doc = Jsoup.parse(WebUtilIns.getRenderedHtmlCode(episodeUrl))
        val name = doc.getElementsByClass("wp-playlist-playing").first()?.text()
            ?.let { it.substring(it.indexOf(".") + 1) }?.trimAll() ?: ""

        videoType = doc.getElementsByClass("cat-links").text()
        //Log.i("播放类型", "$name type=$videoType")

        //return VideoPlayMedia(name, vregex.find(vjson)?.groups?.get(1)?.value ?: "")
        return VideoPlayMedia(name, url)
    }

}
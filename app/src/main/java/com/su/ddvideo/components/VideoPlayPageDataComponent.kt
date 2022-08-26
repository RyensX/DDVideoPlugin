package com.su.ddvideo.components

import android.util.Log
import com.su.ddvideo.util.Text.trimAll
import com.su.mediabox.pluginapi.components.IVideoPlayPageDataComponent
import com.su.mediabox.pluginapi.data.VideoPlayMedia
import com.su.mediabox.pluginapi.util.WebUtilIns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class VideoPlayPageDataComponent : IVideoPlayPageDataComponent {

//    companion object {
//        private val vregex = Regex("url\\\":\\\"(.*)\\\"\\}")
//    }

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

        //return VideoPlayMedia(name, vregex.find(vjson)?.groups?.get(1)?.value ?: "")
        return VideoPlayMedia(name, url)
    }

}
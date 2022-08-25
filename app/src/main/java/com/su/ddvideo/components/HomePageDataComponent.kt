package com.su.ddvideo.components

import com.su.ddvideo.components.Const.host
import com.su.ddvideo.util.JsoupUtil
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.components.IHomePageDataComponent
import com.su.mediabox.pluginapi.data.*
import com.su.mediabox.pluginapi.util.UIUtil.dp

class HomePageDataComponent : IHomePageDataComponent {

    companion object {
        private val coverRegex = Regex("url\\((.*)\\);")
    }

    override suspend fun getData(page: Int): List<BaseData> =
        mutableListOf<BaseData>().apply {
            val url = "${host}/page/$page/"
            JsoupUtil.getDocument(url).select("#main > div.post-box-list").first()?.children()
                ?.forEach {
                    runCatching {
                        var cover =
                            it.getElementsByClass("post-box-image").first()?.attr("style")
                        cover = cover?.let { coverRegex.find(it)?.groups?.get(1)?.value } ?: ""
                        it.getElementsByClass("post-box-text").first()?.apply {
                            val titleEm = getElementsByClass("post-box-title").first()!!.child(0)
                            val title = titleEm.text()
                            val vurl = titleEm.attr("href")
                            val desc = it.getElementsByTag("p").first()?.text() ?: ""

                            add(MediaInfo1Data(
                                title, cover, vurl, desc,
                                coverHeight = 240.dp
                            ).apply {
                                spanSize = 4
                                action = DetailAction.obtain(vurl)
                            })
                        }
                    }
                }
        }
}
package com.su.ddvideo.components

import com.su.ddvideo.util.JsoupUtil
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.components.IMediaSearchPageDataComponent
import com.su.mediabox.pluginapi.data.BaseData
import com.su.mediabox.pluginapi.data.MediaInfo2Data
import com.su.mediabox.pluginapi.data.TagData

class MediaSearchPageDataComponent : IMediaSearchPageDataComponent {
    override suspend fun getSearchData(keyWord: String, page: Int): List<BaseData> {
        val result = mutableListOf<BaseData>()
        if (page != 1)
            return result

        val doc = JsoupUtil.getDocument("${Const.host}?s=$keyWord")

        doc.getElementsByClass("row").forEach {
            runCatching {
                val cover = it.getElementsByTag("img")[0].attr("src")
                it.getElementsByClass("post-content").first()?.also { infoEm ->
                    val titleEm =
                        infoEm.getElementsByClass("post-title")[0].getElementsByTag("a")[0]
                    val title = titleEm.text()
                    val vurl = titleEm.attr("href")
                    val update = infoEm.getElementsByClass("updated")[0].text()
                    val updateCycle = infoEm.getElementsByClass("entry-content")[0].text()

                    val tags = mutableListOf<TagData>()
                    infoEm.getElementsByClass("cat-links")[0].children().forEach {
                        tags.add(TagData(it.text()))
                    }

                    result.add(MediaInfo2Data(title, cover, vurl, update, updateCycle, tags).apply {
                        action = DetailAction.obtain(vurl)
                    })
                }
            }
        }
        return result;
    }
}
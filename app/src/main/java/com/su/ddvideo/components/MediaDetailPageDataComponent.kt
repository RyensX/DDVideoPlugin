package com.su.ddvideo.components

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import com.su.ddvideo.util.JsoupUtil
import com.su.ddvideo.util.Text.trimAll
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.action.PlayAction
import com.su.mediabox.pluginapi.action.WebBrowserAction
import com.su.mediabox.pluginapi.components.IMediaDetailPageDataComponent
import com.su.mediabox.pluginapi.data.*
import com.su.mediabox.pluginapi.util.WebUtilIns
import org.jsoup.Jsoup

class MediaDetailPageDataComponent : IMediaDetailPageDataComponent {

    companion object {
        private val tagRegex = Regex(" (.*?) ")
        private val descIndex = "简介: "
    }

    override suspend fun getMediaDetailData(url: String): Triple<String, String, List<BaseData>> {
        val doc = Jsoup.parse(WebUtilIns.getRenderedHtmlCode(url))

        val name = doc.getElementsByClass("post-title").first()!!.text()
        var bc: String? = null

        val data = mutableListOf<BaseData>()

        val spanTotal = 12

        //剧集信息
        doc.getElementsByClass("doulist-item").forEach {
            runCatching {
                val cover = it.getElementsByClass("post").first()!!.child(0).attr("src")
                if (bc == null)
                    bc = cover
                val titleEm = it.getElementsByClass("title").first()!!.child(0)
                val title = titleEm.text()
                val doubanUrl = titleEm.attr("href")
                val doubanScore =
                    it.getElementsByClass("rating_nums").first()?.text()?.toFloat() ?: -1F

                val infoEmText = it.getElementsByClass("abstract").first()!!.text()

                //标题
                data.add(SimpleTextData(title).apply {
                    fontColor = Color.WHITE
                    fontSize = 20F
                    fontStyle = 1
                    spanSize = spanTotal
                    gravity = Gravity.CENTER
                })

                //类别标签
                val di = infoEmText.indexOf(descIndex) + descIndex.length
                val tags = mutableListOf<TagData>()
                tagRegex.findAll(
                    infoEmText.substring(
                        infoEmText.indexOf("类型"),
                        di - descIndex.length
                    )
                ).forEach { result ->
                    result.groups[1]?.value?.also {
                        tags.add(TagData(it))
                    }
                }
                if (tags.isNotEmpty()) {
                    data.add(TagFlowData(tags).apply {
                        spanSize = spanTotal
                    })
                }

                //左侧封面
                data.add(Cover1Data(cover, doubanScore).apply {
                    spanSize = spanTotal * 1 / 3
                    action = WebBrowserAction.obtain(doubanUrl)
                })
                //右侧介绍
                data.add(SimpleTextData(infoEmText.substring(di)).apply {
                    fontColor = Color.WHITE
                    spanSize = spanTotal * 2 / 3
                })
            }
        }

        //季度
        val seasonList = mutableListOf<EpisodeData>()
        doc.getElementsByClass("page-links").first()?.children()?.forEach {
            if (it.tagName() == "a") {
                seasonList.add(EpisodeData(it.text(), "").apply {
                    action = DetailAction.obtain(it.attr("href"))
                })
            } else {
                seasonList.add(EpisodeData(">${it.text()}<", ""))
            }
        }
        if (seasonList.isNotEmpty()) {
            data.add(SimpleTextData("季度列表").apply {
                fontSize = 16F
                fontColor = Color.WHITE
                spanSize = spanTotal
            })
            data.add(EpisodeListData(seasonList).apply {
                spanSize = spanTotal
            })
        }

        //播放列表
        val playList = mutableListOf<EpisodeData>()
        doc.getElementsByClass("wp-playlist-item").forEach {
            val raw = it.text()
            val spiltIndex = raw.indexOf(".")
            if (spiltIndex != -1) {
                val eurl = "$url?ep=${raw.substring(0, spiltIndex)}"
                val ename = raw.substring(spiltIndex + 1).trimAll()
                playList.add(EpisodeData(ename, eurl).apply {
                    action = PlayAction.obtain(eurl)
                })
            }
        }
        if (playList.isNotEmpty()) {
            data.add(SimpleTextData("播放列表").apply {
                fontSize = 16F
                fontColor = Color.WHITE
                spanSize = spanTotal
            })
            data.add(EpisodeListData(playList).apply {
                spanSize = spanTotal
            })
        }


        //配置
        data[0].layoutConfig = BaseData.LayoutConfig(spanTotal)

        return Triple(bc ?: "", name, data)
    }

}
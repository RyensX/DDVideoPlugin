package com.su.ddvideo.components

import com.su.ddvideo.util.JsoupUtil
import com.su.mediabox.pluginapi.components.IMediaUpdateDataComponent

class MediaUpdateDataComponent : IMediaUpdateDataComponent {

    override suspend fun enableUpdateCheck(updateTag: String?): Boolean =
        updateTag?.isNotBlank() ?: true

    override suspend fun getUpdateTag(detailUrl: String): String? {
        val doc = JsoupUtil.getDocument(detailUrl)
        return doc.getElementsByClass("post-title").first()?.let {
            val raw = it.text()
            val ei = raw.indexOf(")")
            if (ei == -1) ""
            else {
                val flag = "更新至"
                raw.substring(raw.indexOf(flag) + flag.length, ei)
            }
        }
    }
}
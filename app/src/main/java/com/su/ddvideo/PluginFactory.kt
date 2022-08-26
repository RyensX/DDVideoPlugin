package com.su.ddvideo

import com.su.ddvideo.components.*
import com.su.ddvideo.danmaku.OyydsDanmaku
import com.su.mediabox.pluginapi.components.*
import com.su.mediabox.pluginapi.IPluginFactory
import com.su.mediabox.pluginapi.util.PluginPreferenceIns

/**
 * 每个插件必须实现本类
 *
 * 注意包和类名都要相同，且必须提供公开的无参数构造方法
 */
class PluginFactory : IPluginFactory() {

    override val host: String = Const.host

    override fun pluginLaunch() {
        PluginPreferenceIns.initKey(OyydsDanmaku.OYYDS_DANMAKU_ENABLE, defaultValue = true)
    }

    override fun <T : IBasePageDataComponent> createComponent(clazz: Class<T>) = when (clazz) {
        IHomePageDataComponent::class.java -> HomePageDataComponent()
        IMediaDetailPageDataComponent::class.java -> MediaDetailPageDataComponent()
        IVideoPlayPageDataComponent::class.java -> VideoPlayPageDataComponent()
        IMediaSearchPageDataComponent::class.java -> MediaSearchPageDataComponent()
        IMediaUpdateDataComponent::class.java -> MediaUpdateDataComponent()
        else -> null
    } as? T

}
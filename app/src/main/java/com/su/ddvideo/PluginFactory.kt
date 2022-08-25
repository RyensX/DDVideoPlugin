package com.su.ddvideo

import com.su.ddvideo.components.Const
import com.su.ddvideo.components.HomePageDataComponent
import com.su.ddvideo.components.MediaDetailPageDataComponent
import com.su.ddvideo.components.VideoPlayPageDataComponent
import com.su.mediabox.pluginapi.components.*
import com.su.mediabox.pluginapi.IPluginFactory

/**
 * 每个插件必须实现本类
 *
 * 注意包和类名都要相同，且必须提供公开的无参数构造方法
 */
class PluginFactory : IPluginFactory() {

    override val host: String = Const.host

    override fun <T : IBasePageDataComponent> createComponent(clazz: Class<T>) = when (clazz) {
        IHomePageDataComponent::class.java -> HomePageDataComponent()
        IMediaDetailPageDataComponent::class.java -> MediaDetailPageDataComponent()
        IVideoPlayPageDataComponent::class.java -> VideoPlayPageDataComponent()
        else -> null
    } as? T

}
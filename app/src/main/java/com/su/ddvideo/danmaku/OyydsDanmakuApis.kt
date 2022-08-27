package com.su.ddvideo.danmaku

import com.su.mediabox.pluginapi.util.PluginPreferenceIns
import retrofit2.http.*

interface OyydsDanmakuApis {

    @Headers("user-agent: MediaBox/DDVideo")
    @GET("https://api.danmu.oyyds.top/api/message/getSomeV3")
    suspend fun getDanmakuData(
        @Query("keyword") keyword: String,
        @Query("number") number: String,
        @Query("type") type: String,
        @Query("platforms") platforms: String
    ): OyydsDanmaku

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("https://api.danmu.oyyds.top/api/message/addOne")
    suspend fun addDanmaku(
        @Field("content") content: String,
        @Field("time") time: String,
        @Field("episodeId") episodeId: String,
        @Field("type") type: String,
        @Field("color") color: String
    )

}
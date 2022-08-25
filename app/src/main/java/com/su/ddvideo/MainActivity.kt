package com.su.ddvideo

import android.app.Activity
import android.os.Bundle
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<WebView>(R.id.web)?.apply {
            setWebContentsDebuggingEnabled(true)
            settings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
        }?.loadUrl("https://ddys.tv/resident-alien/?ep=2")
    }
}
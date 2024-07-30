package com.powersoaps.distributorsales.ui.main.activity.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.databinding.ActivityWebviewBinding

class WebviewActivity : AppCompatActivity() {
    private val webviewBinding by lazy { ActivityWebviewBinding.inflate(layoutInflater) }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(webviewBinding.root)
        webviewBinding.Back.setOnClickListener {
            onBackPressed()
        }
        webviewBinding.Title.text = intent.getStringExtra("Title")
        val URL = intent.getStringExtra("URL")!!
        webviewBinding.webview.webViewClient = WebViewClient()
        webviewBinding.webview.loadUrl(URL)
        val webSettings = webviewBinding.webview.settings
        webSettings.javaScriptEnabled = true
    }
}
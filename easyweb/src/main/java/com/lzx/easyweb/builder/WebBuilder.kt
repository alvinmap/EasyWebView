package com.lzx.easyweb.builder

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.lzx.easyweb.code.*
import com.lzx.easyweb.EasyWeb
import com.lzx.easyweb.cache.WebCacheMode
import com.lzx.easyweb.js.IJsInterface
import com.lzx.easyweb.js.JsInterfaceImpl
import com.lzx.easyweb.js.WebTimesJsInterface
import com.lzx.easyweb.ui.IProgressView
import com.lzx.easyweb.ui.WebViewUIManager
import java.lang.ref.WeakReference

class WebBuilder(activity: Activity) {
    //上下文
    internal var activityWeak: WeakReference<Activity>? = WeakReference<Activity>(activity)

    //WebView
    internal var proxyWebView: IProxyWebView? = null
    internal var view: View? = null

    //WebView的父layout
    internal var viewGroup: ViewGroup? = null
    internal var layoutParams: ViewGroup.LayoutParams? = null
    internal var index: Int = 0

    //WebView配置
    internal var webViewClient: WebViewClient? = null
    internal var webChromeClient: WebChromeClient? = null
    internal var onWebViewLongClick: OnWebViewLongClick? = null
    internal var webViewSetting: IWebViewSetting? = null
    internal var jsInterface: IJsInterface? = null

    //加载器
    internal var urlLoader: IUrlLoader? = null
    internal var jsLoader: IJsLoader? = null

    //是否debug模式
    internal var isDebug = false

    //cache
    internal var cacheMode: WebCacheMode? = null

    //UI相关
    internal var uiManager: WebViewUIManager? = null
    internal var needProgressBar: Boolean = false
    internal var isCustomProgressBar: Boolean = false
    internal var errorView: View? = null
    internal var loadView: View? = null

    @ColorInt
    internal var progressBarColor = -1
    internal var progressBarHeight = 0
    internal var progressView: IProgressView? = null

    @LayoutRes
    internal var errorLayout = -1

    @IdRes
    internal var reloadViewId = -1

    @LayoutRes
    internal var loadLayout = -1

    fun buildWebUI(): WebUIBuilder {
        return WebUIBuilder(this)
    }

    fun setWebViewClient(webViewClient: WebViewClient?) = apply {
        this.webViewClient = webViewClient
    }

    fun setWebChromeClient(webChromeClient: WebChromeClient?) =
        apply { this.webChromeClient = webChromeClient }

    fun setWebViewSetting(webViewSetting: IWebViewSetting?) = apply {
        this.webViewSetting = webViewSetting
    }

    fun setJsInterface(jsInterface: IJsInterface) = apply {
        this.jsInterface = jsInterface
    }

    fun setUrlLoader(urlLoader: IUrlLoader) = apply {
        this.urlLoader = urlLoader
    }

    fun setJsLoader(jsLoader: IJsLoader) = apply {
        this.jsLoader = jsLoader
    }

    fun setOnWebViewLongClick(onWebViewLongClick: OnWebViewLongClick?) = apply {
        this.onWebViewLongClick = onWebViewLongClick
    }

    fun setWebView(webView: IProxyWebView) = apply {
        this.proxyWebView = webView
        this.view = proxyWebView?.getWebView()
    }

    fun setWebCacheMode(cacheMode: WebCacheMode) = apply {
        this.cacheMode = cacheMode
    }

    fun debug(isDebug: Boolean) = apply {
        this.isDebug = isDebug
    }

    fun ready(): EasyWeb {
        if (viewGroup == null) {
            throw NullPointerException("ViewGroup not null,please check your code!")
        }
        try {
            initWebView()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return EasyWeb(this)
    }

    private fun initWebView() {
        val activity =
            this.activityWeak?.get() ?: throw NullPointerException("context is null")
        //WebView
        if (proxyWebView == null) {
            proxyWebView = AndroidWebView(activity)
        }
        //WebViewUIManager
        uiManager = WebViewUIManager.Builder()
            .setActivity(activity)
            .setProxyWebView(proxyWebView)
            .setViewGroup(viewGroup)
            .setLayoutParams(layoutParams)
            .setIndex(index)
            .setErrorLayout(errorLayout)
            .setErrorView(errorView)
            .setLoadLayout(loadLayout)
            .setLoadView(loadView)
            .setReloadViewId(reloadViewId)
            .setNeedProgressBar(needProgressBar, isCustomProgressBar)
            .setProgressBarColor(progressBarColor)
            .setProgressBarHeight(progressBarHeight)
            .setProgressView(progressView)
            .build()

        proxyWebView?.setWebUiManager(uiManager)

        //WebViewClient
        if (webViewClient != null) {
            proxyWebView?.setWebViewClient(webViewClient)
        }
        //WebChromeClient
        if (webChromeClient != null) {
            proxyWebView?.setWebChromeClient(webChromeClient)
        }
        //点击事件
        if (onWebViewLongClick != null) {
            proxyWebView?.setOnWebViewLongClick(onWebViewLongClick)
        }
        //缓存模式
        if (cacheMode == null) {
            cacheMode = WebCacheMode.NOCACHE
        }
        proxyWebView?.setCacheMode(cacheMode)
        //WebSettings
        if (webViewSetting == null) {
            webViewSetting = DefaultWebSettings(activity, cacheMode, isDebug)
        }
        webViewSetting?.setWebSetting(proxyWebView)
        view = proxyWebView?.getWebView() as WebView?

        proxyWebView?.removeRiskJavascriptInterface()

        if (urlLoader == null) {
            urlLoader = UrlLoader(proxyWebView)
        }
        if (jsLoader == null) {
            jsLoader = JsLoader(proxyWebView)
        }

        if (jsInterface == null) {
            jsInterface = JsInterfaceImpl(activity, proxyWebView)
        }

        jsInterface?.addJsInterface(WebTimesJsInterface(), "android")
    }
}
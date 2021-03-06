package com.lzx.easyweb.ui

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.lzx.easyweb.R
import com.lzx.easyweb.code.IProxyWebView

class WebViewParentLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(context, attrs, defStyleAttr) {

    private var webView: View? = null
    private var errorView: View? = null
    private var proxyWebView: IProxyWebView? = null
    @LayoutRes
    private var errorLayoutRes: Int = R.layout.layout_web_error_page
    private var errorUrl: String? = null

    @IdRes
    private var clickId = -1
    private var mErrorLayout: FrameLayout? = null

    fun showErrorPage() {
        if (mErrorLayout != null) {
            mErrorLayout!!.visibility = View.VISIBLE
        } else {
            createErrorView()
        }
    }

    fun createErrorView() {
        if (!errorUrl.isNullOrEmpty()) {
            proxyWebView?.loadWebUrl(errorUrl)
            return
        }
        if (errorView == null && errorLayoutRes == -1) {
            return
        }
        val frameLayout = FrameLayout(context)
        frameLayout.setBackgroundColor(Color.WHITE)
        if (errorView == null) {
            LayoutInflater.from(context).inflate(errorLayoutRes, frameLayout, true)
        } else {
            frameLayout.addView(errorView)
        }
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.addView(frameLayout.also { mErrorLayout = it }, lp)
        frameLayout.visibility = View.VISIBLE
        if (clickId != -1) {
            val clickView = frameLayout.findViewById<View>(clickId)
            clickView.setOnClickListener {
                proxyWebView?.reloadUrl()
            }
        } else {
            frameLayout.setOnClickListener {
                proxyWebView?.reloadUrl()
            }
        }
    }

    fun hideErrorPage() {
        mErrorLayout?.visibility = View.GONE
    }

    fun setErrorLayoutRes(
        @LayoutRes layoutRes: Int = -1,
        @IdRes clickId: Int = -1
    ) {
        this.clickId = clickId
        errorLayoutRes = layoutRes
    }

    fun setErrorUrl(errorUrl: String?) {
        this.errorUrl = errorUrl
    }

    fun setErrorView(view: View?) {
        errorView = view
    }

    fun bindWebView(proxyWebView: IProxyWebView?) {
        this.proxyWebView = proxyWebView
        webView = proxyWebView?.getWebView()
    }
}
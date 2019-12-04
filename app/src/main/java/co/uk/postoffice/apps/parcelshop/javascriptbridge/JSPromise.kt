package co.uk.postoffice.apps.parcelshop.javascriptbridge

import android.os.Build
import android.webkit.JavascriptInterface
import android.webkit.WebView

import com.google.gson.Gson

import java.util.concurrent.Callable
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class JSPromise {
    private val TAG = JSPromise::class.java.simpleName

    private val executor =
        ThreadPoolExecutor(4, 8, 10, TimeUnit.SECONDS, LinkedBlockingDeque<Runnable>())

    private var mWebView: WebView? = null
    private var mCallable: Callable<Any>
    private var hasValue = false
    private var value: Any? = null

    private var mCallbackJavaScript: String? = null
    private var removeJavaScriptMethodAfterCallback = false

    constructor(callable: Callable<Any>): this(null, callable)


    constructor(webView: WebView?, callable: Callable<Any>) {
        mCallable = callable
        mWebView = webView
        run()
    }

    private fun run() {
        executor.execute {
            try {
                value = mCallable.call()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            hasValue = true
            callback()
        }
    }

    /**
     * returns this
     */
    fun setWebView(webView: WebView): JSPromise {
        mWebView = webView
        return this
    }

    @JavascriptInterface
    fun hasValue(): Boolean {
        return hasValue
    }

    @JavascriptInterface
    fun getValueAsJson(): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    /**
     * calls javascriptFunctionName when promise is ready
     *
     * @param javascriptFunctionName the function
     */
    @JavascriptInterface
    fun thenJS(javascriptFunctionName: String) {
        thenJS(javascriptFunctionName, false)
    }

    /**
     * calls javascriptFunctionName when promise is ready
     *
     * @param javascriptFunctionName    the function
     * @param removeMethodAfterCallback check if method should be removed from window after the callback is done
     * window[javascriptFunctionName] = null
     * this could be needed when the methodname is generated to enable the callback
     */
    @JavascriptInterface
    fun thenJS(javascriptFunctionName: String, removeMethodAfterCallback: Boolean) {
        mCallbackJavaScript = javascriptFunctionName
        removeJavaScriptMethodAfterCallback = removeMethodAfterCallback
        if (hasValue) {
            callback()
        }
    }

    private fun callback() {
        if (mWebView == null) {
            return
        }
        mWebView!!.post {
            if (mCallbackJavaScript != null && mWebView != null) {
                val js = mCallbackJavaScript + "(" + getValueAsJson() + ");"
                evaluateJavaScript(js)
                if (removeJavaScriptMethodAfterCallback) {
                    evaluateJavaScript(String.format("window[%s]=null;", mCallbackJavaScript))
                }
            }

            //callback done
            mCallbackJavaScript = null
        }
    }

    private fun evaluateJavaScript(javascript: String) {
        if (mWebView != null) {
            val js = "javascript:$javascript"
            if (Build.VERSION.SDK_INT >= 23) {
                mWebView!!.evaluateJavascript(js, null)
            } else {
                mWebView!!.loadUrl(js)
            }
        }
    }
}
package co.uk.postoffice.apps.parcelshop.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import co.uk.postoffice.apps.parcelshop.MainActivity

class ScreenReceiver : BroadcastReceiver() {
    private var wasScreenOn = true

    override fun onReceive(context: Context?, intent: Intent?) {
        var fragment = (context as MainActivity).fragmentWeb
        var webView = fragment.webView
        if (intent?.action == Intent.ACTION_SCREEN_OFF) {
            logout(webView)
            wasScreenOn = false
        } else if (intent?.action== Intent.ACTION_SCREEN_ON) {
            wasScreenOn = true
        }
    }

    private fun logout(mWebView: WebView){
        mWebView.evaluateJavascript("javascript:window.loginService.logout()",null)
    }
}
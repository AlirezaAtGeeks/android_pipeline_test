package co.uk.postoffice.apps.parcelshop.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import co.uk.postoffice.apps.parcelshop.BuildConfig
import co.uk.postoffice.apps.parcelshop.R
import co.uk.postoffice.apps.parcelshop.javascriptbridge.AndroidInject
import co.uk.postoffice.apps.parcelshop.javascriptbridge.AndroidInjectSettings
import co.uk.postoffice.apps.parcelshop.singleton.Constants
import com.payzone.libs.E200Printer
import org.slf4j.Logger
import java.io.File
import android.content.Intent
import android.content.IntentFilter
import kotlinx.android.synthetic.main.fragment_web.*

class FragmentWeb(): Fragment() {
    private lateinit var androidInject:AndroidInject
    private lateinit var androidInjectSettings:AndroidInjectSettings
    private lateinit var mReceiver: ScreenReceiver
    lateinit var webView:WebView

    companion object {

        lateinit var printer:E200Printer
        lateinit var LOGGER:Logger

        fun getInstance(printer:E200Printer,androidInject: AndroidInject, androidInjectSettings:AndroidInjectSettings ,logger: Logger): FragmentWeb {
            this.printer = printer

            LOGGER = logger
            val fragment = FragmentWeb()
            fragment.setInterfaces(androidInject,androidInjectSettings)

            return fragment
        }
    }

    private fun setInterfaces(androidInject: AndroidInject, androidInjectSettings: AndroidInjectSettings){
        this.androidInject = androidInject
        this.androidInjectSettings = androidInjectSettings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LOGGER.info(resources.getString(R.string.entering))
        retainInstance = true

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
        return inflater.inflate(R.layout.fragment_web, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LOGGER.info(resources.getString(R.string.entering))

        var progressBar = view.findViewById(R.id.progressBar) as ProgressBar
        webView = view.findViewById(R.id.webview) as WebView

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webView.visibility = WebView.GONE
                progressBar.visibility = WebView.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.visibility = WebView.VISIBLE
                progressBar.visibility = WebView.GONE
            }
        }

        // INITIALIZE RECEIVER
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        mReceiver = ScreenReceiver()
        context?.registerReceiver(mReceiver, filter)

        androidInject.setWebView(webView)
        androidInjectSettings.setWebView(webView)

        fetchWebApp(webView)

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onStart() {
        super.onStart()
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onResume() {
        super.onResume()
        LOGGER.info(resources.getString(R.string.entering))
        /* request camera permission if it is not already granted */
        if(!isCameraUsePermitted()){
            requestCameraPermission()
        }

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onPause() {
        super.onPause()
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onDetach() {
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onStop() {
        LOGGER.info(resources.getString(R.string.entering))

        LOGGER.info(resources.getString(R.string.leaving))
        super.onStop()
    }

    override fun onDestroy() {
        LOGGER.info(resources.getString(R.string.entering))

        webView.clearCache(true)
        webView.destroy()
        context?.unregisterReceiver(mReceiver)

        LOGGER.info(resources.getString(R.string.leaving))
        super.onDestroy()
    }

    /**
     *  A function to initialize webview
     */
      private fun fetchWebApp(webView:WebView){
        LOGGER.info(resources.getString(R.string.entering))

        webView.webChromeClient = MyWebChromeClient()
        webView.settings?.javaScriptEnabled = true
        webView.settings?.allowContentAccess = true
        webView.settings?.allowFileAccess = true
        webView.settings?.allowFileAccessFromFileURLs = true
        webView.settings?.allowUniversalAccessFromFileURLs = true
        webView.settings?.domStorageEnabled = true
        webView.settings?.databaseEnabled = true

        LOGGER.info("User Agent Info: {}",webView.settings?.userAgentString)

        /* injecting native interfaces */
        webView.addJavascriptInterface(androidInject,"androidInject")
        webView.addJavascriptInterface(androidInjectSettings,"androidInjectSetting")

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        /* only enable for a debug build */
        if(BuildConfig.DEBUG){
            WebView.setWebContentsDebuggingEnabled(true)
        }
        }
        /*loading web app from external files directory */
        loadWebApp()

        LOGGER.info(resources.getString(R.string.leaving))
    }

    private fun loadWebApp(){
        webView.loadUrl("file:///" +  activity?.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.APP_LIVE_FOLDER + File.separator + Constants.FILE_INDEX_HTML)
    }

    private fun isCameraUsePermitted(): Boolean {

        LOGGER.info(resources.getString(R.string.entering))


        var status =
            ContextCompat.checkSelfPermission(activity!!.applicationContext, android.Manifest.permission.CAMERA)
        LOGGER.info("camera grant status => {}", status)

        if (status != PackageManager.PERMISSION_GRANTED) {
            LOGGER.info(resources.getString(R.string.leaving))
            return false
        }

        LOGGER.info(resources.getString(R.string.leaving))
        return true
    }

    private fun requestCameraPermission(){
        LOGGER.info(resources.getString(R.string.entering))
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA),
            Constants.REQUEST_PERMISSION_CAMERA)
        LOGGER.info(resources.getString(R.string.leaving))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        webview.restoreState(savedInstanceState)
        super.onActivityCreated(savedInstanceState)
    }

    inner class MyWebChromeClient: WebChromeClient(){
        override fun onPermissionRequestCanceled(request: PermissionRequest?) {
            super.onPermissionRequestCanceled(request)
            LOGGER.info(resources.getString(R.string.entering))

            LOGGER.info(resources.getString(R.string.leaving))
        }

        override fun onPermissionRequest(request: PermissionRequest?) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                LOGGER.info("sdkVersion -> {}", Build.VERSION.SDK_INT)
                LOGGER.info("versionCode -> {}", Build.VERSION_CODES.LOLLIPOP)
                LOGGER.info("request origin:  {}",request?.origin)
                LOGGER.info("Permission Resources:: {}", request?.resources)
                request?.grant(request.resources)

            }
        }
    }
}
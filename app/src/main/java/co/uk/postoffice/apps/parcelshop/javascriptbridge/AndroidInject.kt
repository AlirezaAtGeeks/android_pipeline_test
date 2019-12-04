package co.uk.postoffice.apps.parcelshop.javascriptbridge

import android.content.Context
import android.content.pm.PackageManager
import android.webkit.JavascriptInterface
import android.widget.Toast
import co.uk.postoffice.apps.parcelshop.R
import org.slf4j.LoggerFactory
import android.util.*
import co.uk.postoffice.apps.parcelshop.singleton.Constants
import com.payzone.libs.E200Printer
import java.io.File
import java.nio.ByteBuffer
import android.webkit.WebView



/**
 *  A class to offer native utility features to javascript world
 */
class AndroidInject(private val context: Context?, private val printer: E200Printer?){

    private lateinit var mWebView: WebView

    companion object {
        val LOGGER = LoggerFactory.getLogger(AndroidInject::class.java.name)
    }

    fun setWebView(webView: WebView){
        this.mWebView = webView
    }

    /**
     * A function to show android toast
     */
    @JavascriptInterface
    fun showToast(message:String){
        LOGGER.info(context?.resources?.getString(R.string.entering))

        Toast.makeText(context,message,Toast.LENGTH_LONG).show()

        LOGGER.info(context?.resources?.getString(R.string.entering))
    }

    @JavascriptInterface
    fun getCertificate():String?{
        LOGGER.info(context?.resources?.getString(R.string.entering))

        var base64:String? = null

        val file = File(context?.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.DEVICE_CERTIFICATE)
        var byteArray:ByteArray = file.readBytes()
        val size:Int = byteArray.size

        LOGGER.info("file len is \n {}",size)

        if(size > 0){
            base64 = Base64.encodeToString(byteArray,Base64.DEFAULT)
        }

        LOGGER.info(context?.resources?.getString(R.string.leaving))
        return base64
    }



    @JavascriptInterface
    fun getKey():String?{
        LOGGER.info(context?.resources?.getString(R.string.entering))


        var base64:String? = null

        val file = File(context?.getExternalFilesDir(null)?.absolutePath + File.separator + Constants.CERTIFICATE_KEY)


        var byteArray:ByteArray = file.readBytes()
        val size:Int = byteArray.size

        LOGGER.info("file len is \n {} bytes.",size)

        if(size > 0){
            base64 = Base64.encodeToString(byteArray,Base64.DEFAULT)
        }


        LOGGER.info(context?.resources?.getString(R.string.leaving))
        return base64
    }

    @JavascriptInterface
    fun printIt(base64:String){
        printer?.open()
        var imageData = Base64.decode(base64,Base64.DEFAULT)
        val byteBuffer:ByteBuffer = ByteBuffer.wrap(imageData)


         /* now send it to printer queue */
        printer?.printTicket(byteBuffer)

    }


    /**
     * This function facilitate to trigger other application such as
     * android system settings , xaioutility and production test.
     */
    @JavascriptInterface
    fun startApp(name:String){

        when(name){
            Constants.ANDROID_SETTINGS ->{

                val pm:PackageManager? = context?.packageManager
                val launchIntent = pm?.getLaunchIntentForPackage(Constants.ANDROID_SETTINGS_PACKAGE_NAME)
                context?.startActivity(launchIntent)


            }
            Constants.XAC_XAIOUTILITY->{

                val pm:PackageManager? = context?.packageManager
                val launchIntent = pm?.getLaunchIntentForPackage(Constants.XAIOUTILITY_PACKAGE_NAME)
                context?.startActivity(launchIntent)

            }
            Constants.XAC_PRODUCTIONTEST->{

                val pm:PackageManager? = context?.packageManager
                val launchIntent = pm?.getLaunchIntentForPackage(Constants.PRODUCTIONTEST_PACKAGE_NAME)
                context?.startActivity(launchIntent)

            }
            else ->{

                val mesg:String = name + ", not found."
                Toast.makeText(context,mesg, Toast.LENGTH_LONG).show()
                LOGGER.info("{name}, not found")

            }
        }
    }
}
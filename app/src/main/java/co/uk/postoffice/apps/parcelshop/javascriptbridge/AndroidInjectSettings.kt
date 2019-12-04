package co.uk.postoffice.apps.parcelshop.javascriptbridge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import android.os.Build
import org.slf4j.Logger
import android.provider.Settings.SettingNotFoundException
import android.provider.Settings.System
import android.webkit.JavascriptInterface
import android.webkit.WebView
import co.uk.postoffice.apps.parcelshop.R
import co.uk.postoffice.apps.parcelshop.services.IBatteryService
import co.uk.postoffice.apps.parcelshop.services.IBluetoothService
import co.uk.postoffice.apps.parcelshop.services.IWifiService
import java.util.concurrent.Callable
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.WindowManager

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

class AndroidInjectSettings(
    private var activity: Activity,
    private var context:Context){
    private lateinit var mWebView: WebView

    companion object {

        lateinit var LOGGER:Logger
        lateinit var audioManager:AudioManager
        lateinit var contentResolver: ContentResolver
        lateinit var batteryService: IBatteryService
        lateinit var wifiService: IWifiService
        lateinit var bluetoothService: IBluetoothService
        private var gson: Gson = GsonBuilder().setPrettyPrinting().create()

        fun getInstance(activity: Activity,context: Context,logger: Logger, batteryService: IBatteryService, wifiService: IWifiService, bluetoothService: IBluetoothService):AndroidInjectSettings{

            this.audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            this.contentResolver = context.contentResolver
            this.batteryService = batteryService
            this.wifiService = wifiService
            this.bluetoothService = bluetoothService
            LOGGER = logger
            return AndroidInjectSettings(activity,context)
        }
    }


    /* brightness settings constants */
    private val MINIMUM_BRIGHTNESS_VALUE = 0
    private val MAXIMUM_BRIGHTNESS_VALUE = 255

    /* sound settings constants */
    private val MINIMUM_VOLUME_VALUE = 1
    private val MAXIMUM_VOLUME_VALUE = 15

    fun setWebView(webView: WebView){
        this.mWebView = webView
    }

    /**
     * A function to get all the bluetooth pair devices
     */
    @JavascriptInterface
    fun getBluetoothPairedDevices(): String{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        var result = bluetoothService.getPairedDevices()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return result
    }

    /**
     * A function to get all the bluetooth pair devices in Async mode
     */
    @JavascriptInterface
    fun getBluetoothPairedDevicesAsync(): JSPromise{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        val result = Callable<Any> { bluetoothService.getPairedDevices() }
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return JSPromise(result).setWebView(mWebView)
    }

    /**
     * A function to turn on the bluetooth
     */
    @JavascriptInterface
    fun turnOnBluetooth(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.turnOn()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to turn off the bluetooth
     */
    @JavascriptInterface
    fun turnOffBluetooth(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.turnOff()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to cancel the bluetooth discovery
     */
    @JavascriptInterface
    fun cancelBluetoothDiscovery(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.cancelDiscovery()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to start the bluetooth discovery
     */
    @JavascriptInterface
    fun startBluetoothDiscovery(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.startDiscovery()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to pair the current device with the printer
     */
    @JavascriptInterface
    fun pairBluetoothDevice(address: String){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.pairDevice(address)
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to unpair the current device from the printer
     */
    @JavascriptInterface
    fun unpairBluetoothDevice(address: String){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.unpairDevice(address)
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to unpair the current device from the printer
     */
    @JavascriptInterface
    fun unpairBluetoothDevice(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.unpairDevice()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to print base64 image on bluetooth printer
     */
    @JavascriptInterface
    fun printWithBluetoothPrinter(address: String, base64Image: String){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.print(address, base64Image)
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to print base64 image on bluetooth printer
     */
    @JavascriptInterface
    fun printWithBluetoothPrinter(base64Image: String){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        bluetoothService.print(base64Image)
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to check the connectivy of a bluetooth printer
     */
    @JavascriptInterface
    fun isBluetoothPrinterConnected(): Boolean{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return bluetoothService.isBluetoothConnected()
    }

    /**
     * A function to check the connectivy of a bluetooth printer
     */
    @JavascriptInterface
    fun isBluetoothPrinterConnectedAsync(): JSPromise{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        val result = Callable<Any> { bluetoothService.isBluetoothConnected() }
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return JSPromise(result).setWebView(mWebView)
    }

    /**
     * A function to get all the access points info
     */
    @JavascriptInterface
    fun getWifiNetworks(): String{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        var result = wifiService.getAllNetworks()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return result
    }

    /**
     * A function to get all the access points info in Async mode
     */
    @JavascriptInterface
    fun getWifiNetworksAsync(): JSPromise{
        val networks = Callable<Any> { wifiService.getAllNetworks() }
        return JSPromise(networks).setWebView(mWebView)
    }

    /**
     * A function to turn off wifi
     */
    @JavascriptInterface
    fun turnOffWifi(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        wifiService.turnOff()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to disconnect access point
     */
    @JavascriptInterface
    fun disconnectWifi(){
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        wifiService.disconnect()
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
    }

    /**
     * A function to connect to wifi  in Async mode
     */
    @JavascriptInterface
    fun connectToWifiAsync(networkSSID: String, networkPassKey: String): JSPromise{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        val result = Callable<Any> { wifiService.connectToAP(networkSSID,networkPassKey) }
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return JSPromise(result).setWebView(mWebView)
    }

    /**
     * A function to connect to wifi
     */
    @JavascriptInterface
    fun connectToWifi(networkSSID: String, networkPassKey: String): Boolean{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        val result = wifiService.connectToAP(networkSSID,networkPassKey)
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return result
    }

    /**
     * A function to check if wifi isenabled
     */
    @JavascriptInterface
    fun isWifiEnabled(): Boolean{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        return wifiService.isWifiEnabled()
    }

    /**
     * A function to check if wifi isenabled in Async mode
     */
    @JavascriptInterface
    fun isWifiEnabledAsync(): JSPromise{
        AndroidInject.LOGGER.info(context.resources?.getString(R.string.entering))
        var result = Callable<Any> { wifiService.isWifiEnabled()}
        return JSPromise(result).setWebView(mWebView)
    }

    /**
     * A function to provided information about device's network connectivity status
     */
    @JavascriptInterface
    fun isWifiConnected():Boolean{
        return wifiService.isWifiConnected()
    }

    /**
     * A function to provided information about device's network connectivity status
     */
    @JavascriptInterface
    fun isWifiConnectedAsync():JSPromise{
        var result = Callable<Any> { wifiService.isWifiConnected()}
        return JSPromise(result).setWebView(mWebView)
    }

    /**
     * call this function to get the current brightness settings
     *
     * @return brightness value from 0 to 100.
     */
    @JavascriptInterface
    fun getBrightness(): Int {
        LOGGER.info(context.resources.getString(R.string.entering))
        LOGGER.info(context.resources.getString(R.string.leaving))
        return (activity.window.attributes.screenBrightness * 100F).toInt()
    }


    /**
     * call this function to set the new brightness settings required.
     *
     * @param brightness
     */
    @JavascriptInterface
    fun setBrightness(brightness: Int) {

        val window = activity.window

        var normalisedBrightness: Float = when {
            brightness > 100 -> 100F
            brightness < 0 -> 0F
            else -> brightness.toFloat() / 100F
        }

        activity.runOnUiThread {
            val layoutPrams = window.attributes
            layoutPrams.screenBrightness = normalisedBrightness
            window.attributes = layoutPrams
            window.addFlags(WindowManager.LayoutParams.FLAGS_CHANGED)
        }
    }


    /**
     * call this function to get the current volume settings
     *
     * @return volume value between 1 to 15
     */
    @JavascriptInterface
    fun getSystemVolume(): Int {
        LOGGER.info(context.resources.getString(R.string.entering))

        /*if (audioManager != null) {*/
        var  volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        /*} else {
            LOGGER.info("AudioManager is null.")
        }*/
        LOGGER.info(context.resources.getString(R.string.leaving))
        return volume
    }


    /**
     * call this function to set the volume settings
     *
     * @param volume
     */
    @JavascriptInterface
    fun setSystemVolume(volume: Int) {

        LOGGER.info(context.resources.getString(R.string.entering))

            if (volume in MINIMUM_VOLUME_VALUE..MAXIMUM_VOLUME_VALUE) {

                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    volume, 0
                )
            } else {
                LOGGER.info("volume is out of range.(allowed 1 to 15) but found $volume")
            }

        LOGGER.info(context.resources.getString(R.string.leaving))

    }

    /**
     * call this function to get the current battery percentage
     *
     * @return volume value between 0 to 100
     */
    @JavascriptInterface
    fun getBatteryPercentage(): Int {
        LOGGER.info(context.resources.getString(R.string.entering))
        var  result = batteryService.getBatteryPercentage()
        LOGGER.info(context.resources.getString(R.string.leaving))

        return result
    }

    /**
     * call this function to get the current battery percentage
     *
     * @return volume value between 0 to 100
     */
    @JavascriptInterface
    fun getBatteryPercentageWithChargingState(): JSPromise {
        LOGGER.info(context.resources.getString(R.string.entering))
        val percentage = batteryService.getBatteryPercentage()
        val isCharging = batteryService.isCharging()
        var result = Callable<Any> { gson.toJson(mutableMapOf("percentage" to percentage, "isCharging" to isCharging)) }
        LOGGER.info(context.resources.getString(R.string.leaving))
        return JSPromise(result).setWebView(mWebView)
    }

    /**PHONE_STATE_PERMISSION
     * Returns the serial number of the device.
     *
     * @return
     */
    @JavascriptInterface
    fun getDeviceSerialNumber(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && checkPermission(Manifest.permission.READ_PHONE_STATE,
                activity)) {
            Build.getSerial()

        } else {
            Build.SERIAL
        }
    }

    private fun checkPermission(permission: String, activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

class WifiCallable: Callable<Any>{
    override fun call(): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
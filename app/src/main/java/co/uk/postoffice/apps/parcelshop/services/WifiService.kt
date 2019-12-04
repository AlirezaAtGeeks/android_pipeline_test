package co.uk.postoffice.apps.parcelshop.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import android.net.wifi.WifiConfiguration
import co.uk.postoffice.apps.parcelshop.R
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import co.uk.postoffice.apps.parcelshop.dtos.AccessPointDTO

class WifiService(val context: Context): IWifiService {
    private val wifiManager: WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    companion object : SingletonHolder<WifiService, Context> (::WifiService) {
        val LOGGER = LoggerFactory.getLogger(WifiService::class.java.name)
        var apList = ArrayList<AccessPointDTO>()
        var gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    override fun turnOff() {
        wifiManager.isWifiEnabled = false
    }

    override fun disconnect() {
        wifiManager.disconnect()
    }

    override fun isWifiEnabled(): Boolean {
        try {
            Thread.sleep(500)
        } catch (e: InterruptedException) {
            LOGGER.debug("InterruptException $e")
        }
        return wifiManager.isWifiEnabled
    }

    override fun isWifiConnected(): Boolean {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = cm.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting?:false
    }

    /*
    * Search all the wifi network. Turn on the wifi if it is off.
    * The value returned is the list of
    * */
    override fun getAllNetworks(): String {
        if(!wifiManager.isWifiEnabled)
            wifiManager.isWifiEnabled = true

        this.context.registerReceiver(mWifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        WifiService.LOGGER.info("Starting Scan...")
        apList.clear()
        // retry to make sure if the wifi is connected
        for (i in 1..5) {
            wifiManager.startScan()
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                LOGGER.debug("InterruptException $e")
            }
            if(apList.isEmpty()) {
                LOGGER.info("Retry getting access points: $i")
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    LOGGER.debug("InterruptException $e")
                }
                continue
            }
            break
        }
        LOGGER.info(context.resources?.getString(R.string.entering))

        return gson.toJson(apList)
    }

    /*
    * Connect to an access point with a networkSSID and networkPassKey
    * The value returned is the ID of the newly created network description.
    * This is used in other operations to specified the network to be acted upon. Returns -1 on failure.
    *
    * @param networkSSID is the network ssid
    * @param networkPassKey is the network pass key
    * @return the ID of the newly created network description
    *
    * */
    override fun connectToAP(networkSSID: String, networkPassKey: String): Boolean{
        var isConnected = false

        for (result in wifiManager.scanResults) {
            if (result.SSID == networkSSID) {
                val securityMode = getScanResultSecurity(result)
                val wifiConfiguration = createAPConfiguration(networkSSID, networkPassKey, securityMode)
                val res = wifiManager.addNetwork(wifiConfiguration)

                LOGGER.debug("# addNetwork returned $res")
                val b = wifiManager.enableNetwork(res, true)
                LOGGER.debug("# enableNetwork returned $b")

                wifiManager.isWifiEnabled = true

                // retry to make sure if the wifi is connected
                for (i in 1..5) {
                    isConnected = isConnectedToWifi()
                    if(!isConnected) {
                        LOGGER.info("Retry connecting to Wifi: $i")
                        try {
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            LOGGER.debug("InterruptException $e")
                        }
                        continue
                    }
                    break
                }

                if (res != -1) {
                    LOGGER.debug("# Change happen: $networkSSID")
                } else {
                    LOGGER.debug("# Change NOT happen")
                }
                return isConnected
            }
        }
        return isConnected
    }

    private fun getScanResultSecurity(scanResult: ScanResult): String {
        val cap = scanResult.capabilities
        val securityModes = arrayOf("WEP", "PSK", "EAP")
        for (i in securityModes.indices.reversed()) {
            if (cap.contains(securityModes[i])) {
                return securityModes[i]
            }
        }
        return "OPEN"
    }

    private fun createAPConfiguration(
        networkSSID: String,
        networkPasskey: String,
        securityMode: String
    ): WifiConfiguration? {
        val wifiConfiguration = WifiConfiguration()
        wifiConfiguration.SSID = "\"" + networkSSID + "\""
        if (securityMode.equals("OPEN", ignoreCase = true)) {
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
        } else if (securityMode.equals("WEP", ignoreCase = true)) {
            wifiConfiguration.wepKeys[0] = "\"" + networkPasskey + "\""
            wifiConfiguration.wepTxKeyIndex = 0
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
        } else if (securityMode.equals("PSK", ignoreCase = true)) {
            wifiConfiguration.preSharedKey = "\"" + networkPasskey + "\""
            wifiConfiguration.hiddenSSID = true
            wifiConfiguration.status = WifiConfiguration.Status.ENABLED
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
        } else {
            LOGGER.info("# Unsupported security mode: $securityMode")
            return null
        }
        return wifiConfiguration
    }

    private fun isConnectedToWifi(): Boolean {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            LOGGER.debug("InterruptException $e")
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var capabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                }
            }
        } else {
            val activeNetwork = connManager.activeNetworkInfo
            if (activeNetwork != null) {
                // connected to the internet
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnectedOrConnecting) {
                    // connected to wifi
                    return true
                }
            }
        }

        return false
    }

    private val mWifiScanReceiver = object: BroadcastReceiver() {
        override fun onReceive(c:Context, intent: Intent) {
            for (result in wifiManager.scanResults.distinctBy { s -> s.SSID })
            {
                var isConnected = wifiManager.connectionInfo.ssid.replace("\"","") == result.SSID
                WifiService.apList.add(AccessPointDTO(result.SSID, result.capabilities, isConnected))
            }
        }
    }
}
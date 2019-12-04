package co.uk.postoffice.apps.parcelshop.services

interface IWifiService {
    fun getAllNetworks(): String
    fun turnOff()
    fun connectToAP(networkSSID: String, networkPassKey: String): Boolean
    fun disconnect()
    fun isWifiConnected(): Boolean
    fun isWifiEnabled(): Boolean
}
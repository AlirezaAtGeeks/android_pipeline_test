package co.uk.postoffice.apps.parcelshop.services

interface IBluetoothService {
    fun getPairedDevices(): String
    fun turnOn()
    fun turnOff()
    fun pairDevice(address: String)
    fun unpairDevice(address: String)
    fun unpairDevice()
    fun print(printerAddress: String, base64Image: String)
    fun print(base64Image: String)
    fun isBluetoothConnected(): Boolean
    fun isBluetoothEnabled(): Boolean
    fun cancelDiscovery()
    fun startDiscovery()
}
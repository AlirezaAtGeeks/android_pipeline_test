package co.uk.postoffice.apps.parcelshop.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.bluetooth.BluetoothDevice
import android.webkit.WebView
import co.uk.postoffice.apps.parcelshop.MainActivity


class BluetoothBroadcastReceiver : BroadcastReceiver() {
    var deviceList = arrayListOf<BluetoothDevice>()
        private set(value){
            field = value
        }
    var deviceState = DISCONNECTED
        private set(value){
            field = value
        }
    companion object{
        const val CONNECTED = 1
        const val DISCONNECTED = 2
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action

        if (BluetoothDevice.ACTION_FOUND == action) {
            // Discovery has found a device. Get the BluetoothDevice
            // object and its info from the Intent.
            val device = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            var fragment = (context as MainActivity).fragmentWeb
            var webView = fragment.webView

            if(device?.bondState != BluetoothDevice.BOND_BONDED &&
                ((device?.bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.IMAGING &&    // Major device class
                device.bluetoothClass?.deviceClass == 1664 &&                                           // Minor device class
                device.bluetoothClass.hasService(BluetoothClass.Service.RENDER)) ||                     // Major service class
                device?.bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.UNCATEGORIZED &&
                        !device.name.isNullOrEmpty()))

                if(!deviceList.contains(device)) {
                    deviceList.add(device)
                    addDevice(webView, device)
                }

            if(device?.bondState == BluetoothDevice.BOND_BONDED)
                deviceState = CONNECTED

        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action){
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
            context?.unregisterReceiver(this)
        }
    }

    private fun addDevice(mWebView: WebView, device: BluetoothDevice){
        mWebView.evaluateJavascript("javascript:window.bluetoothPage.addBluetoothDevice({ name:'${device.name}', address: '${device.address}'})",null)
    }
}
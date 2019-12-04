package co.uk.postoffice.apps.parcelshop.services

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.widget.Toast
import co.uk.postoffice.apps.parcelshop.MainActivity

class BluetoothPairingBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var action = intent?.action
        var fragment = (context as MainActivity).fragmentWeb
        var webView = fragment.webView

       if(BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
            val state       = intent?.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
            val prevState   = intent?.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)

            if (state == BluetoothDevice.BOND_BONDED) {
                showToast(context, "Paired")
                context?.unregisterReceiver(this)
                refresh(webView)
            } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                showToast(context,"Unpaired")
                context?.unregisterReceiver(this)
                refresh(webView)
            }
        }
    }

    private fun refresh(mWebView: WebView){
        mWebView.evaluateJavascript("javascript:window.bluetoothPage.refresh()",null)
    }

    private fun showToast(context: Context?, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
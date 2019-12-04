package co.uk.postoffice.apps.parcelshop.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import co.uk.postoffice.apps.parcelshop.dtos.BluetoothDeviceDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.graphics.internal.ZebraImageAndroid
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinterFactory
import org.slf4j.LoggerFactory
import java.io.IOException


class BluetoothService(val context: Context): IBluetoothService {
    private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bAdapter: BluetoothAdapter = bluetoothManager.adapter

    init {
        Printooth.init(context)
    }

    companion object : SingletonHolder<BluetoothService, Context> (::BluetoothService) {
        val LOGGER = LoggerFactory.getLogger(BluetoothService::class.java.name)
        var deviceList = arrayListOf<BluetoothDevice>()
        var gson: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    override fun getPairedDevices(): String {
        if(!bAdapter.isEnabled)
            bAdapter.enable()

        var pairedDevices: Set<BluetoothDevice> = bAdapter.bondedDevices
        // retry to make sure if the wifi is connected
        for (i in 1..2) {
            if(pairedDevices.isEmpty()) {
                LOGGER.info("Retry getting paired bluetooth devices: $i")
                try {
                    Thread.sleep(750)
                    pairedDevices = bAdapter.bondedDevices
                } catch (e: InterruptedException) {
                    LOGGER.debug("InterruptException $e")
                }
                continue
            }
            break
        }

        return gson.toJson(pairedDevices.map{d -> BluetoothDeviceDTO(d.name,d.address)})
    }

    override fun startDiscovery(){
        LOGGER.info("Start Discovery ...")
        if(!bAdapter.isEnabled)
            bAdapter.enable()

        bAdapter.cancelDiscovery()

        var receiver = registerBroadcaster()
        deviceList = receiver.deviceList

        bAdapter.startDiscovery()
    }

    override fun cancelDiscovery(){
        LOGGER.info("Cancel Discovery")
        bAdapter.cancelDiscovery()
    }


    private fun registerBroadcaster(): BluetoothBroadcastReceiver{
        val bluetoothBroadcastReceiver = BluetoothBroadcastReceiver()
        context.registerReceiver(bluetoothBroadcastReceiver, setupFilter())
        return bluetoothBroadcastReceiver
    }

    private fun registerPairingBroadcastReceiver(): BluetoothPairingBroadcastReceiver{
        val bluetoothPairingBroadcastReceiver = BluetoothPairingBroadcastReceiver()
        context.registerReceiver(bluetoothPairingBroadcastReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))
        return bluetoothPairingBroadcastReceiver
    }

    private fun setupFilter(): IntentFilter {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)

        return filter
    }

    override fun turnOn() {
        bAdapter.enable()
    }

    override fun turnOff() {
        bAdapter.disable()
    }

    override fun pairDevice(address: String) {
        unpairAllDevices()
        registerPairingBroadcastReceiver()
        for (device in deviceList){
            if(device.address == address){
                try {
                    device::class.java.getMethod("createBond").invoke(device)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private fun unpairAllDevices(){
        for(device in bAdapter.bondedDevices){
            unpairDevice(device.address)
        }
    }

    override fun unpairDevice(address: String) {
        registerPairingBroadcastReceiver()
        for (device in bAdapter.bondedDevices){
            if(device.address == address){
                try {
                    device::class.java.getMethod("removeBond").invoke(device)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun unpairDevice() {
        registerPairingBroadcastReceiver()
        var device = bAdapter.bondedDevices.first()
        if (device != null){
            try {
                device::class.java.getMethod("removeBond").invoke(device)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun print(printerAddress: String, base64Image: String) {
        for (device in bAdapter.bondedDevices){
            if(device.address == printerAddress){
                var image = convertBase64ImageToBitmap(base64Image)
                sendImageToPrinter(device, image)
            }
        }
    }

    override fun print(base64Image: String) {
        LOGGER.info("Printing label ... ")

        var device = bAdapter.bondedDevices.first()
        if (device != null){
            val image = convertBase64ImageToBitmap(base64Image)
            var connection = BluetoothConnection(device.address)
            try {
                connection.open()

                var printer = ZebraPrinterFactory.getInstance(PrinterLanguage.CPCL, connection)
                printer.printImage(ZebraImageAndroid(image), 0, 0, 0, 0, false)

                connection.close()
            } catch(e: ConnectionException){
                LOGGER.error("Connection failed: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                connection.close()
            }
        }else{
            LOGGER.error("Please pair a Zebra printer and then try again.")
        }
    }


    @Throws(IOException::class)
    private fun sendCommand(connection: Connection,command: String) {
        try {
            connection.write(command.toByteArray(Charsets.ISO_8859_1))

        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun sendImageToPrinter(device: BluetoothDevice, image: Bitmap){
        Printooth.removeCurrentPrinter()
        Printooth.setPrinter(device.name, device.address)
        val printable = ImagePrintable
            .Builder(image)
            .setAlignment(StarPrinter.ALLIGMENT_REGHT)
            .build()
        val printer = Printooth.printer(StarPrinter())
        printer.printingCallback = BluetoothPrintingCallback(context)
        printer.print(arrayListOf(printable))
    }

    private fun convertBase64ImageToBitmap(image: String): Bitmap{
        val decodeString = Base64.decode(image,Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodeString, 0, decodeString.size)
    }

    override fun isBluetoothConnected(): Boolean {
        if(!bAdapter.isEnabled)
            return false

        bAdapter.cancelDiscovery()

        var bluetoothBroadcastReceiver = registerBroadcaster()

        bAdapter.startDiscovery()

        for (i in 1..5) {
            if(bluetoothBroadcastReceiver.deviceList.isEmpty()) {
                LOGGER.info("Retry getting paired bluetooth devices: $i")
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    LOGGER.debug("InterruptException $e")
                }
                continue
            }
            break
        }

        var deviceState = bluetoothBroadcastReceiver.deviceState

        bAdapter.cancelDiscovery()
        return when(deviceState){
            BluetoothBroadcastReceiver.CONNECTED -> true
            else -> false
        }
    }

    override fun isBluetoothEnabled(): Boolean {
        return bAdapter.isEnabled
    }
}

class BluetoothPrintingCallback(private val context: Context): PrintingCallback {
    override fun connectingWithPrinter() =
        Toast.makeText(context, "Connecting to printer ... ", Toast.LENGTH_SHORT).show()

    override fun printingOrderSentSuccessfully() =
        Toast.makeText(context, "printer was received your printing order successfully.", Toast.LENGTH_LONG).show()

    override fun connectionFailed(error: String) =
        Toast.makeText(context, "Connection failed: $error",Toast.LENGTH_LONG).show()

    override fun onError(error: String) = Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()

    override fun onMessage(message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}


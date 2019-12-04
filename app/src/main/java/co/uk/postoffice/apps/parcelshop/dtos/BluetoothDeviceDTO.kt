package co.uk.postoffice.apps.parcelshop.dtos

import android.support.annotation.Keep

@Keep
data class BluetoothDeviceDTO(
    val name: String,
    val address: String)
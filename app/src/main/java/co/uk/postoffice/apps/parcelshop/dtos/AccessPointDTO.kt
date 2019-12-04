package co.uk.postoffice.apps.parcelshop.dtos

import android.support.annotation.Keep

@Keep
data class AccessPointDTO(
    val ssid: String,
    val capabilities: String,
    val isConnected: Boolean)

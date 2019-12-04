package co.uk.postoffice.apps.parcelshop.services

interface IBatteryService {
    fun getBatteryPercentage(): Int
    fun isCharging(): Boolean
}
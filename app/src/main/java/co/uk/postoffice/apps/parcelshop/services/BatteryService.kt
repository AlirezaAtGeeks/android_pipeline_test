package co.uk.postoffice.apps.parcelshop.services

import android.content.Context
import org.slf4j.LoggerFactory
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryService(val context: Context): IBatteryService {
    companion object: SingletonHolder<BatteryService, Context> (::BatteryService) {
        val LOGGER = LoggerFactory.getLogger((BatteryService::class.java.name))
    }

    override fun getBatteryPercentage(): Int {
        val batteryStatus = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        var batteryLevel = -1
        var batteryScale = 1
        if (batteryStatus != null) {
            batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel)
            batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale)
        }

        val batteryPercentage = ((batteryLevel / batteryScale.toFloat()) * 100).toInt()
        LOGGER.info("The battery percentage is $batteryPercentage %")
        return batteryPercentage
    }

    override fun isCharging(): Boolean {
        val batteryStatus = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )
        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        return status == BatteryManager.BATTERY_STATUS_CHARGING
    }
}
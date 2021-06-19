package com.bairei.electricpowermeter.meter.dto

data class DisplayMeasurementStatistics(
    val averagePowerConsumption: Int,
    val biggestPowerConsumption: Int,
    val measurements: List<MeterEntryDto>,
    val consumptions: List<DisplayConsumptionEntry>,
    val recharges: List<DisplayRechargeEntry>
)

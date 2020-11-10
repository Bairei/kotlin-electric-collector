package com.bairei.electricpowercollector.collector.dto

data class DisplayMeasurementStatistics(
    val averagePowerConsumption: Int,
    val biggestPowerConsumption: Int,
    val measurements: List<CollectorEntryDto>,
    val consumptions: List<DisplayConsumptionEntry>,
    val recharges: List<DisplayRechargeEntry>
)

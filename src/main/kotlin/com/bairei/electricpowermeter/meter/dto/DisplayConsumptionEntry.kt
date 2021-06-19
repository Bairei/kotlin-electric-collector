package com.bairei.electricpowermeter.meter.dto

import java.time.LocalDate

data class DisplayConsumptionEntry(val firstDate: LocalDate, val secondDate: LocalDate, val consumptionValue: Int)

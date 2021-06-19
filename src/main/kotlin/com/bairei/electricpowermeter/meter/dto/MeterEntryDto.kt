package com.bairei.electricpowermeter.meter.dto

import java.time.LocalDateTime
import java.util.UUID

data class MeterEntryDto(
    val businessId: UUID,
    val createdAt: LocalDateTime?,
    val readingDate: LocalDateTime,
    val reading: Int
)

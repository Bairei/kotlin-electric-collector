package com.bairei.electricpowercollector.collector.dto

import java.time.LocalDateTime
import java.util.UUID

data class CollectorEntryDto(
    val businessId: UUID,
    val createdAt: LocalDateTime?,
    val readingDate: LocalDateTime,
    val reading: Int
)

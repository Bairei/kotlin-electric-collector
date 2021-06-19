package com.bairei.electricpowermeter.meter

import lombok.Builder
import lombok.EqualsAndHashCode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "collector")
data class MeterEntity(
    val businessId: UUID = UUID.randomUUID(),
    var readingDate: LocalDateTime,
    var collectorReading: Int,
    @Id @EqualsAndHashCode.Include var id: String? = null,
    var createdAt: LocalDateTime? = null
)

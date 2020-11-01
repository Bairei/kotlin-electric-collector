package com.bairei.electricpowercollector.collector

import lombok.Builder
import lombok.EqualsAndHashCode
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "collector")
class CollectorEntity(
        val businessId: UUID = UUID.randomUUID(),
        var readingDate: LocalDateTime,
        var collectorReading: Int,
        @Id @EqualsAndHashCode.Include var id: String? = null,
        var createdAt: LocalDateTime? = null)

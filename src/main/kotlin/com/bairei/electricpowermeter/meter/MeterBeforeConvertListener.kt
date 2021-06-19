package com.bairei.electricpowermeter.meter

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Objects.isNull

@Component
class MeterBeforeConvertListener : AbstractMongoEventListener<MeterEntity>() {

    override fun onBeforeConvert(event: BeforeConvertEvent<MeterEntity>) {
        if (isNull(event.source.createdAt)) {
            event.source.createdAt = LocalDateTime.now()
        }
    }
}

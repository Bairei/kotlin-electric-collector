package com.bairei.electricpowercollector.collector

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Objects.isNull

@Component
class CollectorBeforeConvertListener : AbstractMongoEventListener<CollectorEntity>() {

    override fun onBeforeConvert(event: BeforeConvertEvent<CollectorEntity>) {
        if (isNull(event.source.createdAt)) {
            event.source.createdAt = LocalDateTime.now()
        }
    }
}

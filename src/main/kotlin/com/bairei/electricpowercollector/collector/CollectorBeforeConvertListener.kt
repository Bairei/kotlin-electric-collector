package com.bairei.electricpowercollector.collector

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CollectorBeforeConvertListener : AbstractMongoEventListener<CollectorEntity>() {

    override fun onBeforeConvert(event: BeforeConvertEvent<CollectorEntity>) {
        if (event.source.createdAt == null) {
            event.source.createdAt = LocalDateTime.now()
        }
    }
}
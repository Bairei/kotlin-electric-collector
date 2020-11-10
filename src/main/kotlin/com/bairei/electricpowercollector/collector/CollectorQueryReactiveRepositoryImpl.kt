package com.bairei.electricpowercollector.collector

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class CollectorQueryReactiveRepositoryImpl(val mongoTemplate: ReactiveMongoTemplate) :
    CollectorQueryReactiveRepository {
    val log: Logger = LoggerFactory.getLogger(CollectorQueryReactiveRepositoryImpl::class.java)

    override fun findByReadingBetween(from: Int, to: Int): Flux<CollectorEntity> {
        log.debug("Executing findByReadingBetween with values: from = {}, to = {}", from, to)
        val result = mongoTemplate.query(CollectorEntity::class.java)
            .matching(query(where(CollectorEntity::collectorReading).gte(from).lte(to))).all()
        log.debug("FindByReadingBetween result: {}", result)
        return result
    }
}

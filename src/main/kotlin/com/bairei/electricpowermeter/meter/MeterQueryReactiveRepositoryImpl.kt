package com.bairei.electricpowermeter.meter

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.where
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class MeterQueryReactiveRepositoryImpl(val mongoTemplate: ReactiveMongoTemplate) :
    MeterQueryReactiveRepository {
    val log: Logger = LoggerFactory.getLogger(MeterQueryReactiveRepositoryImpl::class.java)

    override fun findByReadingBetween(from: Int, to: Int): Flux<MeterEntity> {
        log.debug("Executing findByReadingBetween with values: from = {}, to = {}", from, to)
        val result = mongoTemplate.query(MeterEntity::class.java)
            .matching(query(where(MeterEntity::collectorReading).gte(from).lte(to))).all()
        log.debug("FindByReadingBetween result: {}", result)
        return result
    }
}

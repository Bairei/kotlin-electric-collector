package com.bairei.electricpowermeter.meter

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface MeterRepository : ReactiveMongoRepository<MeterEntity, String>, MeterQueryReactiveRepository

interface MeterQueryReactiveRepository {
    fun findByReadingBetween(from: Int, to: Int): Flux<MeterEntity>
}

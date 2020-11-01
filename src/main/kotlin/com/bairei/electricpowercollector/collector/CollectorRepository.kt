package com.bairei.electricpowercollector.collector

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface CollectorRepository : ReactiveMongoRepository<CollectorEntity, String>, CollectorQueryReactiveRepository

interface CollectorQueryReactiveRepository {
    fun findByReadingBetween(from: Int, to: Int): Flux<CollectorEntity>
}

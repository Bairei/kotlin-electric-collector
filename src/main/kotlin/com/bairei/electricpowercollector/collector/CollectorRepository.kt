package com.bairei.electricpowercollector.collector

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface CollectorRepository : ReactiveMongoRepository<CollectorEntity, String>
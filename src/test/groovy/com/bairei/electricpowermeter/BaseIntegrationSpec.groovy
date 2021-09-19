package com.bairei.electricpowermeter

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
@SpringBootTest
abstract class BaseIntegrationSpec extends Specification {

    @Shared
    static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0")

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        if (!mongoDBContainer.isRunning()) {
            mongoDBContainer.start()
        }
        def replicaSetUrl = mongoDBContainer.getReplicaSetUrl()
        registry.add("spring.data.mongodb.uri", () -> replicaSetUrl)
    }
}

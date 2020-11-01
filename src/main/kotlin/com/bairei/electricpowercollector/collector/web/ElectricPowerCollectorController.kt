package com.bairei.electricpowercollector.collector.web

import com.bairei.electricpowercollector.collector.CollectorEntity
import com.bairei.electricpowercollector.collector.CollectorRepository
import com.bairei.electricpowercollector.collector.dto.CollectorEntryDto
import com.bairei.electricpowercollector.collector.dto.CreateCollectorEntryCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping(value = ["/collector"], produces = [APPLICATION_JSON_VALUE])
class ElectricPowerCollectorController(val collectorRepository: CollectorRepository) {

    val log: Logger = LoggerFactory.getLogger(ElectricPowerCollectorController::class.java)

    @GetMapping("/list")
    fun index(): Flux<CollectorEntryDto> {
        return collectorRepository.findAll().map { toResponse(it) }
    }

    @PostMapping
    fun create(@RequestBody command: CreateCollectorEntryCommand): Mono<CollectorEntryDto> {
        log.info("Received request: {}", command);
        return collectorRepository.save(CollectorEntity(readingDate = command.readingAt, collectorReading = command.reading))
                .map { result -> toResponse(result) }
    }

    private fun toResponse(entity: CollectorEntity): CollectorEntryDto =
            CollectorEntryDto(businessId = entity.businessId, createdAt = entity.createdAt, readingDate = entity.readingDate, reading = entity.collectorReading)
}
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
import javax.validation.constraints.Min

@RestController
@RequestMapping(value = ["/collector"], produces = [APPLICATION_JSON_VALUE])
class ElectricPowerCollectorController(val collectorRepository: CollectorRepository) {

    val log: Logger = LoggerFactory.getLogger(ElectricPowerCollectorController::class.java)

    @GetMapping("/list")
    fun listAll(): Flux<CollectorEntryDto> {
        return collectorRepository.findAll().map { toResponse(it) }
    }

    @PostMapping
    fun create(@RequestBody command: CreateCollectorEntryCommand): Mono<CollectorEntryDto> {
        log.info("Received request: {}", command);
        return collectorRepository
                .save(CollectorEntity(readingDate = command.readingAt, collectorReading = command.reading))
                .map { toResponse(it) }
    }

    @GetMapping("/reading")
    fun listReadingsBetween(@RequestParam(required = false) @Min(0) readingFrom: Int?,
                            @RequestParam(required = false) readingTo: Int?): Flux<CollectorEntryDto> {
        log.info("Received request for listing readings from between {} and {}", readingFrom, readingTo)
        return collectorRepository
                .findByReadingBetween(readingFrom ?: 0,
                        readingTo ?: Int.MAX_VALUE)
                .map { toResponse(it) }
    }

    private fun toResponse(entity: CollectorEntity): CollectorEntryDto =
            CollectorEntryDto(businessId = entity.businessId,
                    createdAt = entity.createdAt,
                    readingDate = entity.readingDate,
                    reading = entity.collectorReading)
}
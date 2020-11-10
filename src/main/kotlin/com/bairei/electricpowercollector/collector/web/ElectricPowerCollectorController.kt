package com.bairei.electricpowercollector.collector.web

import com.bairei.electricpowercollector.collector.CollectorEntity
import com.bairei.electricpowercollector.collector.CollectorRepository
import com.bairei.electricpowercollector.collector.dto.CollectorEntryDto
import com.bairei.electricpowercollector.collector.dto.CreateCollectorEntryCommand
import com.bairei.electricpowercollector.collector.dto.DisplayConsumptionEntry
import com.bairei.electricpowercollector.collector.dto.DisplayMeasurementStatistics
import com.bairei.electricpowercollector.collector.dto.DisplayRechargeEntry
import com.bairei.electricpowercollector.csv.CsvMeasurementExtractor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.validation.constraints.Min

@RestController
@RequestMapping(value = ["/collector"], produces = [APPLICATION_JSON_VALUE])
class ElectricPowerCollectorController(
    val collectorRepository: CollectorRepository,
    val csvMeasurementExtractor: CsvMeasurementExtractor
) {

    val log: Logger = LoggerFactory.getLogger(ElectricPowerCollectorController::class.java)

    @GetMapping("/list")
    fun listAll(): Flux<CollectorEntryDto> {
        return collectorRepository.findAll().map { toResponse(it) }
    }

    @PostMapping
    fun create(@RequestBody command: CreateCollectorEntryCommand): Mono<CollectorEntryDto> {
        log.info("Received request: {}", command)
        return collectorRepository.save(
            CollectorEntity(
                readingDate = command.readingAt,
                collectorReading = command.reading
            )
        ).map { toResponse(it) }
    }

    @GetMapping("/reading")
    fun listReadingsBetween(
        @RequestParam(required = false) @Min(0) readingFrom: Int?,
        @RequestParam(required = false) readingTo: Int?
    ): Flux<CollectorEntryDto> {
        log.info("Received request for listing readings from between {} and {}", readingFrom, readingTo)
        return collectorRepository.findByReadingBetween(readingFrom ?: 0, readingTo ?: Int.MAX_VALUE)
            .map { toResponse(it) }
    }

    @GetMapping("/stat")
    fun displayMeasurementStatistics(): Mono<DisplayMeasurementStatistics> {
        log.info("Received request for displaying measurement statistics")
        return collectorRepository.findAll().collectList().map { toMeasurementStatistics(it) }
    }

    @PostMapping("/csv")
    fun importCsvMeasurements() {
        log.info("Received request to import csv measurements")
        csvMeasurementExtractor.executeExtraction()
    }

    private fun toResponse(entity: CollectorEntity): CollectorEntryDto =
        CollectorEntryDto(
            businessId = entity.businessId,
            createdAt = entity.createdAt,
            readingDate = entity.readingDate,
            reading = entity.collectorReading
        )

    private fun toMeasurementStatistics(collectorEntityList: List<CollectorEntity>): DisplayMeasurementStatistics {
        val powerConsumptionValues = ArrayList<Int>()
        val consumptions = ArrayList<DisplayConsumptionEntry>()
        val recharges = ArrayList<DisplayRechargeEntry>()
        val sortedList = collectorEntityList.sortedWith(Comparator.comparing(CollectorEntity::readingDate))
        sortedList.reduce { first, second ->
            if (first.collectorReading > second.collectorReading) {
                val powerConsumption = first.collectorReading - second.collectorReading
                powerConsumptionValues.add(powerConsumption)
                consumptions.add(
                    DisplayConsumptionEntry(
                        firstDate = first.readingDate.toLocalDate(),
                        secondDate = second.readingDate.toLocalDate(),
                        consumptionValue = powerConsumption
                    )
                )
            } else {
                val powerRecharge = second.collectorReading - first.collectorReading
                recharges.add(
                    DisplayRechargeEntry(
                        rechargeDate = second.readingDate.toLocalDate(),
                        rechargeAmount = powerRecharge
                    )
                )
            }
            second
        }

        val averageConsumption: Int = powerConsumptionValues.average().toInt()
        val maximumConsumption: Int? = powerConsumptionValues.maxOrNull()

        return DisplayMeasurementStatistics(
            averagePowerConsumption = averageConsumption,
            biggestPowerConsumption = maximumConsumption ?: 0,
            measurements = collectorEntityList.map { toResponse(it) },
            consumptions = consumptions,
            recharges = recharges
        )
    }
}

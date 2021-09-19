package com.bairei.electricpowermeter.meter.web

import com.bairei.electricpowermeter.csv.CsvMeasurementExtractor
import com.bairei.electricpowermeter.meter.MeterEntity
import com.bairei.electricpowermeter.meter.MeterRepository
import com.bairei.electricpowermeter.meter.dto.CreateMeterEntryCommand
import com.bairei.electricpowermeter.meter.dto.DisplayConsumptionEntry
import com.bairei.electricpowermeter.meter.dto.DisplayMeasurementStatistics
import com.bairei.electricpowermeter.meter.dto.DisplayRechargeEntry
import com.bairei.electricpowermeter.meter.dto.MeterEntryDto
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
class ElectricPowerMeterController(
    val meterRepository: MeterRepository,
    val csvMeasurementExtractor: CsvMeasurementExtractor
) {

    val log: Logger = LoggerFactory.getLogger(ElectricPowerMeterController::class.java)

    @GetMapping("/list")
    fun listAll(): Flux<MeterEntryDto> {
        return meterRepository.findAll().map { toResponse(it) }
    }

    @PostMapping
    fun create(@RequestBody command: CreateMeterEntryCommand): Mono<MeterEntryDto> {
        log.info("Received request: {}", command)
        return meterRepository.save(
            MeterEntity(
                readingDate = command.readingAt,
                collectorReading = command.reading
            )
        ).map { toResponse(it) }
    }

    @GetMapping("/reading")
    fun listReadingsBetween(
        @RequestParam(required = false) @Min(0) readingFrom: Int?,
        @RequestParam(required = false) readingTo: Int?
    ): Flux<MeterEntryDto> {
        log.info("Received request for listing readings from between {} and {}", readingFrom, readingTo)
        return meterRepository.findByReadingBetween(readingFrom ?: 0, readingTo ?: Int.MAX_VALUE)
            .map { toResponse(it) }
    }

    @GetMapping("/stat")
    fun displayMeasurementStatistics(): Mono<DisplayMeasurementStatistics> {
        log.info("Received request for displaying measurement statistics")
        return meterRepository.findAll().collectList().map { toMeasurementStatistics(it) }
    }

    @PostMapping("/csv")
    fun importCsvMeasurements() {
        log.info("Received request to import csv measurements")
        csvMeasurementExtractor.executeExtraction()
    }

    private fun toResponse(entity: MeterEntity): MeterEntryDto =
        MeterEntryDto(
            businessId = entity.businessId,
            createdAt = entity.createdAt,
            readingDate = entity.readingDate,
            reading = entity.collectorReading
        )

    private fun toMeasurementStatistics(meterEntityList: List<MeterEntity>): DisplayMeasurementStatistics {
        val powerConsumptionValues = ArrayList<Int>()
        val consumptions = ArrayList<DisplayConsumptionEntry>()
        val recharges = ArrayList<DisplayRechargeEntry>()
        val sortedList = meterEntityList.sortedWith(Comparator.comparing(MeterEntity::readingDate))
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
            measurements = meterEntityList.map { toResponse(it) },
            consumptions = consumptions,
            recharges = recharges
        )
    }
}

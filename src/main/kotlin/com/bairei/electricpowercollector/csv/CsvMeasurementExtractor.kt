package com.bairei.electricpowercollector.csv

import com.bairei.electricpowercollector.collector.CollectorEntity
import com.bairei.electricpowercollector.collector.CollectorRepository
import com.opencsv.bean.CsvToBeanBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.MathContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Objects.nonNull
import java.util.regex.Pattern

@Component
@Transactional
class CsvMeasurementExtractor(val collectorRepository: CollectorRepository) {
    private val log: Logger = LoggerFactory.getLogger(CsvMeasurementExtractor::class.java)

    fun executeExtraction() {
        try {
            val csvFile = this::class.java.getResourceAsStream(csvFileName)
            if (nonNull(csvFile)) {
                val entriesFromCsv = CsvToBeanBuilder<CsvMeasurementEntry>(csvFile.reader()).withSeparator(' ')
                    .withType(CsvMeasurementEntry::class.java).build().parse().map { toCollectorEntity(it) }
                log.info("Mapped entries: {}", entriesFromCsv)
                collectorRepository.saveAll(entriesFromCsv).doOnNext { log.info("Entity saved: {}", it) }.subscribe()
            } else {
                log.warn("No csv file detected, skipping extraction!")
            }
        } catch (e: Exception) {
            log.error("Error detected, aborting extraction", e)
        }
    }

    private fun toCollectorEntity(csvEntry: CsvMeasurementEntry): CollectorEntity {
        val measurementValue =
            BigDecimal(csvEntry.measurementValue.replace(',', '.'), MathContext.DECIMAL64).multiply(BigDecimal.TEN)
                .intValueExact()
        val measurementHour = csvEntry.measurementHour.substring(IntRange(0, 1))
        val measurementMinutes = csvEntry.measurementHour.substring(IntRange(2, 3))
        val measurementDay = csvEntry.measurementDate.substring(IntRange(0, 1))
        val measurementMonth = csvEntry.measurementDate.substring(IntRange(2, 3))
        val measurementDate = LocalDateTime.of(
            LocalDate.now().year,
            measurementMonth.toInt(),
            measurementDay.toInt(),
            measurementHour.toInt(),
            measurementMinutes.toInt()
        )
        return CollectorEntity(readingDate = measurementDate, collectorReading = measurementValue)
    }

    companion object {
        const val csvFileName = "/measurement.csv"
        val twoDigitsPattern = Pattern.compile("\\d{2}")
    }
}

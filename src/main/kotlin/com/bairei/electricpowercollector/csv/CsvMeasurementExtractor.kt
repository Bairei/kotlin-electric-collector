package com.bairei.electricpowercollector.csv

import com.bairei.electricpowercollector.collector.CollectorRepository
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.Objects.nonNull

@Component
class CsvMeasurementExtractor(collectorRepository: CollectorRepository) {
    private val log: Logger = LoggerFactory.getLogger(CsvMeasurementExtractor::class.java)

    fun executeExtraction() {
        try {
            val csvFile = this::class.java.getResourceAsStream(csvFileName)
            if (nonNull(csvFile)) {
                val csvParser = CSVParserBuilder()
                        .withSeparator(' ')
                        .build()
                CSVReaderBuilder(csvFile.reader())
                        .withCSVParser(csvParser)
                        .build()
                        .use {
                            it.readAll().forEach { line -> log.info("{}", line) }
                        }
            }
        } catch (e: Exception) {
            log.error("Error detected, aborting extraction", e)
        }

    }

    companion object {
        const val csvFileName = "/measurement.csv"
    }
}

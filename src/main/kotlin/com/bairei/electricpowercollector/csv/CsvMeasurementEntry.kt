package com.bairei.electricpowercollector.csv

import com.opencsv.bean.CsvBindByPosition

data class CsvMeasurementEntry(
    @CsvBindByPosition(position = 0) var measurementHour: String = "",
    @CsvBindByPosition(position = 1) var measurementDate: String = "",
    @CsvBindByPosition(position = 2) var measurementValue: String = ""
)

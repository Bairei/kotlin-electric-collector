package com.bairei.electricpowercollector.csv

import com.opencsv.bean.CsvBindByName

data class CsvMeasurementEntry(@CsvBindByName(column = "GODZINA") val measurementHour: String,
                               @CsvBindByName(column = "DATA") val measurementDate: String,
                               @CsvBindByName(column = "WARTOSC") val measurementValue: String)
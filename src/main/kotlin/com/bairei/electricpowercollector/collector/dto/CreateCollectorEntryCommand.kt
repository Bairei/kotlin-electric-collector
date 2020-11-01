package com.bairei.electricpowercollector.collector.dto

import java.time.LocalDateTime

data class CreateCollectorEntryCommand(var reading: Int, var readingAt: LocalDateTime)
package com.bairei.electricpowermeter.meter.dto

import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

data class CreateMeterEntryCommand(
    @Min(0)
    @ApiModelProperty(
        value = "The collector reading value, integer value multiplied by 10" +
            "(e.g. if reading value = 100.1 (kWh), then reading = 1001"
    )
    var reading: Int,
    @NotNull
    @ApiModelProperty(value = "The collector status reading date", required = true)
    var readingAt: LocalDateTime
)

package com.bairei.electricpowermeter.web

import com.bairei.electricpowermeter.BaseIntegrationSpec
import com.bairei.electricpowermeter.meter.dto.CreateMeterEntryCommand
import com.bairei.electricpowermeter.meter.dto.MeterEntryDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

import java.time.LocalDateTime

@AutoConfigureWebTestClient
class ElectricPowerMeterControllerSpec extends BaseIntegrationSpec {

    @Autowired
    private WebTestClient webTestClient;

    def "should save a power reading"() {
        given:
        LocalDateTime givenDate = LocalDateTime.now()
        CreateMeterEntryCommand command = new CreateMeterEntryCommand(100, givenDate)
        when: "Saving the power reading"
        WebTestClient.ResponseSpec responseSpec = webTestClient
                .post()
                .uri("/collector")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(command)
                .exchange()

        then: "Everything should go OK"
        responseSpec.expectStatus().isOk()
        and: "Should return same reading values"
        responseSpec.expectBody(MeterEntryDto.class)
        MeterEntryDto meterEntryDto = responseSpec.returnResult(MeterEntryDto.class).getResponseBody().blockFirst()
        meterEntryDto.getBusinessId() != null
        meterEntryDto.getCreatedAt() != null
        meterEntryDto.getReadingDate() == command.getReadingAt()
        meterEntryDto.getReading() == command.getReading()
        and: "List collector entries"
        webTestClient.get()
                .uri("/collector/list")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MeterEntryDto)
                .hasSize(1)
    }

}

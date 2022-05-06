package net.shyshkin.study.webfluxpatterns.sec02.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FlightsControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void searchFlights_ok() {
        //given
        String from = "KYIV";
        String to = "LONDON";

        //when
        var flights = webTestClient.get()
                .uri("/sec02/flights/{from}/{to}", from, to)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(FlightResult.class)
                .getResponseBody();

        StepVerifier.create(flights)
                .thenConsumeWhile(fr -> true, fr -> {
                    log.debug("{}", fr);
                    assertThat(fr)
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("from", from)
                            .hasFieldOrPropertyWithValue("to", to);
                    assertThat(fr.getAirline()).isIn("FRONTIER", "DELTA", "JETBLUE");
                })
                .verifyComplete();
    }
}
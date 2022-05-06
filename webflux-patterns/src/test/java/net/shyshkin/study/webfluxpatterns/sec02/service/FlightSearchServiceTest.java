package net.shyshkin.study.webfluxpatterns.sec02.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class FlightSearchServiceTest extends ExternalServiceAbstractTest {

    @Autowired
    FlightSearchService service;

    @Test
    void getFlights() {

        //given
        String from = "KBP";
        String to = "LHR";

        //when
        StepVerifier.create(service.getFlights(from, to))

                //then
                .thenConsumeWhile(flightResult -> true, flightResult -> {
                    log.debug("{}", flightResult);
                    assertThat(flightResult)
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("from", from)
                            .hasFieldOrPropertyWithValue("to", to);
                    assertThat(flightResult.getAirline()).isIn("FRONTIER", "DELTA", "JETBLUE");
                })
                .verifyComplete();
    }
}
package net.shyshkin.study.webfluxpatterns.sec02.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec02.dto.FlightResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "app.external.service.url.frontier=http://${SERVICES_HOST}:${SERVICES_PORT}/sec02/frontier",
        "app.external.service.url.delta=http://${SERVICES_HOST}:${SERVICES_PORT}/sec02/delta",
        "app.external.service.url.jetblue=http://${SERVICES_HOST}:${SERVICES_PORT}/sec02/jetblue"
})
@ContextConfiguration(initializers = FlightsControllerTest.Initializer.class)
class FlightsControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Container
    static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/vinsguru-external-services")
            .withExposedPorts(7070)
            .waitingFor(Wait.forLogMessage(".*Started ExternalServicesApplication.*\\n", 1));

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

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            log.debug("ProductAggregateControllerTest.Initializer.initialize()");
            String host = externalServices.getHost();
            Integer port = externalServices.getMappedPort(7070);

            System.setProperty("SERVICES_HOST", host);
            System.setProperty("SERVICES_PORT", String.valueOf(port));
        }
    }
}
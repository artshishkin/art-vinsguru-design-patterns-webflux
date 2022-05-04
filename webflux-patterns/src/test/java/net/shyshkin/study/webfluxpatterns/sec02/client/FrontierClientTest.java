package net.shyshkin.study.webfluxpatterns.sec02.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "app.external.service.url.frontier=http://${SERVICES_HOST}:${SERVICES_PORT}/sec02/frontier"
})
@ContextConfiguration(initializers = FrontierClientTest.Initializer.class)
class FrontierClientTest {

    @Autowired
    FrontierClient client;

    @Container
    static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/vinsguru-external-services")
            .withExposedPorts(7070)
            .waitingFor(Wait.forLogMessage(".*Started ExternalServicesApplication.*\\n", 1));

    @Test
    void getFlights() {

        //given
        String from = "KBP";
        String to = "LHR";

        //when
        StepVerifier.create(client.getFlights(from, to))

                //then
                .thenConsumeWhile(flightResult -> true, flightResult -> {
                    log.debug("{}", flightResult);
                    assertThat(flightResult)
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("from", from)
                            .hasFieldOrPropertyWithValue("to", to)
                            .hasFieldOrPropertyWithValue("airline", "FRONTIER");
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
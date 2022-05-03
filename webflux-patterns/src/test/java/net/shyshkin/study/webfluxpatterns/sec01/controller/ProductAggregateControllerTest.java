package net.shyshkin.study.webfluxpatterns.sec01.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec01.dto.ProductAggregate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "app.external.service.url.product=http://${SERVICES_HOST}:${SERVICES_PORT}/sec01/product",
        "app.external.service.url.promotion=http://${SERVICES_HOST}:${SERVICES_PORT}/sec01/promotion",
        "app.external.service.url.review=http://${SERVICES_HOST}:${SERVICES_PORT}/sec01/review"
})
@ContextConfiguration(initializers = ProductAggregateControllerTest.Initializer.class)
class ProductAggregateControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Container
    static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/vinsguru-external-services")
            .withExposedPorts(7070)
            .waitingFor(Wait.forLogMessage(".*Started ExternalServicesApplication.*\\n", 1));

    @Test
    @DisplayName("When every service returns correct data aggregator service should respond correctly")
    void getProductAggregate_ok() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec01/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(ProductAggregate.class)
                .value(aggregate -> assertAll(
                                () -> assertThat(aggregate)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("id", productId),
                                () -> assertThat(aggregate.getReviews())
                                        .hasSizeGreaterThanOrEqualTo(1)
                                        .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties())
                        )
                )
                .value(aggregate -> log.debug("{}", aggregate));
    }

    @ParameterizedTest
    @DisplayName("When one of services respond with 404 or 500 Status code we got 500 from Aggregation controller")
    @ValueSource(ints = {5, 7})
    void getProductAggregate_500Error(int productId) {

        //when
        webTestClient.get()
                .uri("/sec01/product/{id}", productId)
                .exchange()

                //then
                .expectStatus().is5xxServerError()
                .expectBody(String.class)
                .value(aggregate -> log.debug("Error: {}", aggregate));
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
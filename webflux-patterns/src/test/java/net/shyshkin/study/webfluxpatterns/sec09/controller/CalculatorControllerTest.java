package net.shyshkin.study.webfluxpatterns.sec09.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec09")
class CalculatorControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webClient;

    @RepeatedTest(5)
    @Order(10)
    void doubleInput_ok(RepetitionInfo repetitionInfo) {
        //given
        int input = repetitionInfo.getCurrentRepetition();

        //when
        webClient.get()
                .uri("/sec09/calculator/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Integer.TYPE)
                .value(res -> assertThat(res).isEqualTo(input * 2));
    }

    @RepeatedTest(5)
    @Order(20)
    void doubleInput_rateLimit(RepetitionInfo repetitionInfo) {
        //given
        int input = repetitionInfo.getCurrentRepetition();
        String expectedErrorMessage = "RateLimiter 'calculator-service' does not permit further calls";

        //when
        webClient.get()
                .uri("/sec09/calculator/{input}", input)
                .exchange()

                //then
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                .expectBody(String.class)
                .value(res -> assertThat(res).isEqualTo(expectedErrorMessage));
    }

}
package net.shyshkin.study.webfluxpatterns.sec09.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("sec09")
class CalculatorControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void doubleInput() {
        //given
        int input = 4;

        //when
        webClient.get()
                .uri("/sec09/calculator/{input}", input)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Integer.TYPE)
                .value(res -> assertThat(res).isEqualTo(input * 2));
    }
}
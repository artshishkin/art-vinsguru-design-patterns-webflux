package net.shyshkin.study.webfluxpatterns.sec08.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.sec07.dto.ProductAggregate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@ActiveProfiles("sec08")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "app.external.service.baseUrl=http://localhost:7075"
})
@Disabled("Only for manual testing. Run external services on port 7075 first. `docker-compose up`")
class ProductAggregateControllerManualTest {

    @Autowired
    WebTestClient webTestClient;

    @RepeatedTest(10)
    @DisplayName("All requests should return OK")
    void getProductAggregate_ok() {
        //given
        Integer productId = 1;

        //when
        webTestClient.get()
                .uri("/sec08/product/{id}", productId)
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
                                .allSatisfy(review -> assertThat(review).hasNoNullFieldsOrProperties()),
                        () -> log.debug("Aggregate: {}", aggregate)
                ));
    }

}
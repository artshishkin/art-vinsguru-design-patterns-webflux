package net.shyshkin.study.webfluxpatterns.sec05.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.CarCategory;
import net.shyshkin.study.webfluxpatterns.sec05.dto.CarReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section05 CarClientTest")
@ActiveProfiles("sec05")
class CarClientTest extends ExternalServiceAbstractTest {

    @Autowired
    CarClient client;

    @Test
    void reserve_ok() {
        //given
        int count = 5;
        var requestFlux = Flux.range(1, count)
                .map(i -> CarReservationRequest.builder()
                        .city("City" + i)
                        .category(CarCategory.STANDARD)
                        .pickup(LocalDate.now())
                        .drop(LocalDate.now().plusDays(i))
                        .build()
                );

        //when
        StepVerifier.create(client.reserve(requestFlux))

                //then
                .consumeNextWith(response -> assertAll(
                                () -> assertThat(response).hasNoNullFieldsOrProperties(),
                                () -> assertThat(response.getCategory()).isEqualTo(CarCategory.STANDARD),
                                () -> assertThat(response.getPickup()).isEqualTo(LocalDate.now()),
                                () -> assertThat(response.getCity()).startsWith("City"),
                                () -> assertThat(response.getPrice()).isGreaterThan(0),
                                () -> log.debug("Response: {}", response)
                        )
                )
                .expectNextCount(count - 1)
                .verifyComplete();
    }
}
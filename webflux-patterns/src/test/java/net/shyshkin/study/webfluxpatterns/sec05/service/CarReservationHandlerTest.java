package net.shyshkin.study.webfluxpatterns.sec05.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType;
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
@DisplayName("Section05 CarReservationHandlerTest")
@ActiveProfiles("sec05")
class CarReservationHandlerTest extends ExternalServiceAbstractTest {

    @Autowired
    CarReservationHandler handler;

    @Test
    void getType() {
        //given
        ReservationType expectedType = ReservationType.CAR;

        //when
        ReservationType actualType = handler.getType();

        //then
        assertThat(actualType).isEqualTo(expectedType);

    }

    @Test
    void reserve_ok() {
        //given
        int count = 5;
        var itemRequests = Flux.range(1, count)
                .map(i -> ReservationItemRequest.builder()
                        .type(ReservationType.CAR)
                        .from(LocalDate.now())
                        .to(LocalDate.now().plusDays(i))
                        .category("LUXURY")
                        .city("City" + i)
                        .build()
                );

        //when
        StepVerifier.create(handler.reserve(itemRequests))

                //then
                .thenConsumeWhile(resp -> true, response -> assertAll(
                        () -> assertThat(response).hasNoNullFieldsOrProperties(),
                        () -> assertThat(response.getCategory()).isEqualTo("LUXURY"),
                        () -> assertThat(response.getCity()).startsWith("City"),
                        () -> assertThat(response.getFrom()).isEqualTo(LocalDate.now()),
                        () -> assertThat(response.getTo()).isAfter(LocalDate.now()),
                        () -> assertThat(response.getType()).isEqualTo(ReservationType.CAR),
                        () -> log.debug("Item: {}", response)
                ))
                .verifyComplete();
    }

    @Test
    void reserve_wrongCategory() {
        //given
        int count = 5;
        var itemRequests = Flux.range(1, count)
                .map(i -> ReservationItemRequest.builder()
                        .type(ReservationType.CAR)
                        .from(LocalDate.now())
                        .to(LocalDate.now().plusDays(i))
                        .category("FAKE")
                        .city("City" + i)
                        .build()
                );

        //when
        StepVerifier.create(handler.reserve(itemRequests))

                //then
                .verifyComplete();
    }

}
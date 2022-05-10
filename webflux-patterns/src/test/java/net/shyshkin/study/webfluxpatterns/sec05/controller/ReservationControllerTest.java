package net.shyshkin.study.webfluxpatterns.sec05.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

import static net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType.CAR;
import static net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType.ROOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@ActiveProfiles("sec05")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationControllerTest extends ExternalServiceAbstractTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void reserve_ok() {

        //given
        int count = 10;
        var itemRequests = Flux.range(1, count)
                .map(i -> ReservationItemRequest.builder()
                        .type(getType(i))
                        .from(LocalDate.now())
                        .to(LocalDate.now().plusDays(i))
                        .category(getCategory(i))
                        .city("City" + i)
                        .build()
                );

        //when
        webTestClient
                .post()
                .uri("/sec05/reserve")
                .body(itemRequests, ReservationItemRequest.class)
                .exchange()

                //then
                .expectBody(ReservationResponse.class)
                .value(reservationResponse -> assertAll(
                        () -> assertThat(reservationResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(reservationResponse.getItems())
                                .hasSize(count)
                                .allSatisfy(response -> assertAll(
                                        () -> assertThat(response).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(response.getCity()).startsWith("City"),
                                        () -> assertThat(response.getFrom()).isEqualTo(LocalDate.now()),
                                        () -> assertThat(response.getTo()).isAfter(LocalDate.now()),
                                        () -> log.debug("Item: {}", response)
                                )),
                        () -> assertThat(reservationResponse.getPrice()).isGreaterThan(0),
                        () -> log.debug("Response: {}", reservationResponse)
                ));
    }

    @Test
    void reserve_wrongCategory() {

        //given
        int count = 20;
        var itemRequests = Flux.range(1, count)
                .map(i -> ReservationItemRequest.builder()
                        .type(getType(i))
                        .from(LocalDate.now())
                        .to(LocalDate.now().plusDays(i))
                        .category(getWrongCategory(i))
                        .city("City" + i)
                        .build()
                );

        //when
        webTestClient
                .post()
                .uri("/sec05/reserve")
                .body(itemRequests, ReservationItemRequest.class)
                .exchange()

                //then
                .expectBody(ReservationResponse.class)
                .value(reservationResponse -> assertAll(
                        () -> assertThat(reservationResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(reservationResponse.getItems())
                                .hasSize(count / 2 + count / 4)
                                .allSatisfy(response -> assertAll(
                                        () -> assertThat(response).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(response.getCity()).startsWith("City"),
                                        () -> assertThat(response.getFrom()).isEqualTo(LocalDate.now()),
                                        () -> assertThat(response.getTo()).isAfter(LocalDate.now()),
                                        () -> log.debug("Item: {}", response)
                                )),
                        () -> assertThat(reservationResponse.getPrice()).isGreaterThan(0),
                        () -> log.debug("Response: {}", reservationResponse)
                ));
    }


    private ReservationType getType(int i) {
        return i % 2 == 0 ? ROOM : CAR;
    }

    private String getCategory(int i) {
        return i % 2 == 0 ? RoomCategory.QUEEN.toString() : CarCategory.SPORTS.toString();
    }

    private String getWrongCategory(int i) {
        return i % 2 == 0 ?
                i % 4 == 0 ?
                        RoomCategory.QUEEN.toString() :
                        "FAKE_ROOM" :
                CarCategory.SPORTS.toString();
    }

}
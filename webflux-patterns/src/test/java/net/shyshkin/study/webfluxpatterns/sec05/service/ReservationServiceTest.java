package net.shyshkin.study.webfluxpatterns.sec05.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.CarCategory;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationItemRequest;
import net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType;
import net.shyshkin.study.webfluxpatterns.sec05.dto.RoomCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType.CAR;
import static net.shyshkin.study.webfluxpatterns.sec05.dto.ReservationType.ROOM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section05 ReservationServiceTest")
@ActiveProfiles("sec05")
class ReservationServiceTest extends ExternalServiceAbstractTest {

    @Autowired
    ReservationService reservationService;

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
        StepVerifier.create(reservationService.reserve(itemRequests))

                //then
                .consumeNextWith(reservationResponse -> assertAll(
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
                ))
                .verifyComplete();
    }

    @Test
    void reserve_wrongCategory() {
        //given
        int count = 10;
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
        StepVerifier.create(reservationService.reserve(itemRequests))

                //then
                .consumeNextWith(reservationResponse -> assertAll(
                        () -> assertThat(reservationResponse).hasNoNullFieldsOrProperties(),
                        () -> assertThat(reservationResponse.getItems())
                                .hasSize(count/2)
                                .allSatisfy(response -> assertAll(
                                        () -> assertThat(response).hasNoNullFieldsOrProperties(),
                                        () -> assertThat(response.getCity()).startsWith("City"),
                                        () -> assertThat(response.getFrom()).isEqualTo(LocalDate.now()),
                                        () -> assertThat(response.getTo()).isAfter(LocalDate.now()),
                                        () -> assertThat(response.getType()).isEqualTo(ReservationType.CAR),
                                        () -> log.debug("Item: {}", response)
                                )),
                        () -> assertThat(reservationResponse.getPrice()).isGreaterThan(0),
                        () -> log.debug("Response: {}", reservationResponse)
                ))
                .verifyComplete();
    }

    private ReservationType getType(int i) {
        return i % 2 == 0 ? ROOM : CAR;
    }

    private String getCategory(int i) {
        return i % 2 == 0 ? RoomCategory.QUEEN.toString() : CarCategory.SPORTS.toString();
    }

    private String getWrongCategory(int i) {
        return i % 2 == 0 ? "FAKE_ROOM" : CarCategory.SPORTS.toString();
    }
}
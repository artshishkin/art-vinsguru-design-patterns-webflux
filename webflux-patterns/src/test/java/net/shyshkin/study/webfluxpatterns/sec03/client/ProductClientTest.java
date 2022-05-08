package net.shyshkin.study.webfluxpatterns.sec03.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.webfluxpatterns.common.ExternalServiceAbstractTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@DisplayName("Section03 ProductClientTest")
@ActiveProfiles("sec03")
class ProductClientTest extends ExternalServiceAbstractTest {

    @Autowired
    ProductClient client;

    @Test
    void getProduct_ok() {
        //given
        Integer productId = 1;

        //when
        StepVerifier.create(client.getProduct(productId))

                //then
                .consumeNextWith(product -> assertAll(
                        () -> assertThat(product).hasNoNullFieldsOrProperties(),
                        () -> assertThat(product.getId()).isEqualTo(productId)
                ))
                .verifyComplete();
    }

    @Test
    void getProduct_absent() {
        //given
        Integer productId = 51;

        //when
        StepVerifier.create(client.getProduct(productId))

                //then
                .verifyComplete();
    }
}
package net.shyshkin.study.webfluxpatterns.sec03.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.webfluxpatterns.sec03.dto.PaymentRequest;
import net.shyshkin.study.webfluxpatterns.sec03.dto.PaymentResponse;
import net.shyshkin.study.webfluxpatterns.sec03.dto.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

    private static final String DEDUCT = "/deduct";
    private static final String REFUND = "/refund";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public UserClient(
            WebClient.Builder builder,
            @Value("${app.external.service.url.sec03.user}") String serverUrl,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        webClient = builder.baseUrl(serverUrl).build();
    }

    public Mono<User> getUser(Integer id) {
        return webClient.get()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(User.class)
                .onErrorResume(ex -> Mono.empty());
    }

    public Mono<PaymentResponse> deduct(PaymentRequest request) {
        return this.callUserService(DEDUCT, request);
    }

    public Mono<PaymentResponse> refund(PaymentRequest request) {
        return this.callUserService(REFUND, request);
    }

    private Mono<PaymentResponse> callUserService(String endpoint, PaymentRequest request) {
        return webClient
                .post()
                .uri(endpoint)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponse.class);
    }

}

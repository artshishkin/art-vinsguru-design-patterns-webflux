package net.shyshkin.study.webfluxpatterns.sec04.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrderRequest;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrderResponse;
import net.shyshkin.study.webfluxpatterns.sec04.service.OrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("sec04/order")
public class OrderController {

    private final OrchestratorService service;

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> placeOrder(@RequestBody Mono<OrderRequest> request) {
        return service
                .placeOrder(request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}

package net.shyshkin.study.webfluxpatterns.sec07.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec07.dto.ProductAggregate;
import net.shyshkin.study.webfluxpatterns.sec07.service.ProductAggregatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec07")
@RequiredArgsConstructor
public class ProductAggregateController {

    private final ProductAggregatorService aggregatorService;

    @GetMapping("product/{id}")
    public Mono<ResponseEntity<ProductAggregate>> getProductAggregate(@PathVariable Integer id) {
        return aggregatorService.aggregate(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}

package net.shyshkin.study.webfluxpatterns.sec09.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec09/calculator")
public class CalculatorController {

    //CPU Intensive Task
    //We want to allow only 5 request per 20 seconds
    @GetMapping("{input}")
    public Mono<ResponseEntity<Integer>> doubleInput(@PathVariable Integer input) {
        return Mono.fromSupplier(() -> input * 2)
                .map(ResponseEntity::ok);
    }

}

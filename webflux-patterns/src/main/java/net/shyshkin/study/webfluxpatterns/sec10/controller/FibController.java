package net.shyshkin.study.webfluxpatterns.sec10.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("sec10")
public class FibController {

    //CPU Intensive Task
    @GetMapping("fib/{input}")
    public Mono<ResponseEntity<Long>> fibonacci(@PathVariable Long input) {
        return Mono.fromSupplier(() -> findFib(input))
                .map(ResponseEntity::ok);
    }

    private long findFib(long input) {
        if (input < 2) return input;
        return findFib(input - 1) + findFib(input - 2);
    }

}

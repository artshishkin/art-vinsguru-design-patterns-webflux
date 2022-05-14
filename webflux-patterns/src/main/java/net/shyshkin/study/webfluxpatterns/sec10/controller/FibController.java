package net.shyshkin.study.webfluxpatterns.sec10.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@RequestMapping("sec10")
public class FibController {

    //I have 8 CPUs but I like to limit 6 CPUs just for fibonacci operation
    private final Scheduler scheduler = Schedulers.newParallel("fib", 6);

    //CPU Intensive Task
    @GetMapping("fib/{input}")
    public Mono<ResponseEntity<Long>> fibonacci(@PathVariable Long input) {
        return Mono.fromSupplier(() -> findFib(input))
                .subscribeOn(scheduler)
                .map(ResponseEntity::ok);
    }

    private long findFib(long input) {
        if (input < 2) return input;
        return findFib(input - 1) + findFib(input - 2);
    }

}

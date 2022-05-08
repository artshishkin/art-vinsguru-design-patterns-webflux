package net.shyshkin.study.webfluxpatterns.sec04.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrchestrationRequestContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderCancellationService {

    private final List<Orchestrator> orchestrators;

    private Sinks.Many<OrchestrationRequestContext> sink;
    private Flux<OrchestrationRequestContext> flux;

    @PostConstruct
    private void init() {
        sink = Sinks.many().multicast().onBackpressureBuffer();
        flux = sink.asFlux().publishOn(Schedulers.boundedElastic());
        orchestrators.forEach(orc -> this.flux.subscribe(orc.cancel()));
    }

    public void cancelOrder(OrchestrationRequestContext ctx) {
        sink.tryEmitNext(ctx);
    }


}

package net.shyshkin.study.webfluxpatterns.sec04.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec04.client.ShippingClient;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrchestrationRequestContext;
import net.shyshkin.study.webfluxpatterns.sec04.dto.Status;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ShippingOrchestrator extends Orchestrator {

    private final ShippingClient client;

    @Override
    public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
        return client.schedule(ctx.getShippingRequest())
                .doOnNext(ctx::setShippingResponse)
                .thenReturn(ctx)
                .handle(this.statusHandler());
    }

    @Override
    public Predicate<OrchestrationRequestContext> isSuccess() {
        return ctx -> Objects.nonNull(ctx.getShippingResponse()) && Status.SUCCESS.equals(ctx.getShippingResponse().getStatus());
    }

    @Override
    public Consumer<OrchestrationRequestContext> cancel() {
        return ctx -> Mono.just(ctx)
                .filter(isSuccess())
                .map(OrchestrationRequestContext::getShippingRequest)
                .flatMap(client::cancel)
                .subscribe();
    }
}

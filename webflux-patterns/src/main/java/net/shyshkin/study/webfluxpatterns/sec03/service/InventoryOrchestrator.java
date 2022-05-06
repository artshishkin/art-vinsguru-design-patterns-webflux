package net.shyshkin.study.webfluxpatterns.sec03.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.webfluxpatterns.sec03.client.InventoryClient;
import net.shyshkin.study.webfluxpatterns.sec03.dto.OrchestrationRequestContext;
import net.shyshkin.study.webfluxpatterns.sec03.dto.Status;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class InventoryOrchestrator extends Orchestrator {

    private final InventoryClient client;

    @Override
    public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
        return client.deduct(ctx.getInventoryRequest())
                .doOnNext(ctx::setInventoryResponse)
                .thenReturn(ctx);
    }

    @Override
    public Predicate<OrchestrationRequestContext> isSuccess() {
        return ctx -> Status.SUCCESS.equals(ctx.getInventoryResponse().getStatus());
    }

    @Override
    public Consumer<OrchestrationRequestContext> cancel() {
        return ctx -> Mono.just(ctx)
                .filter(isSuccess())
                .map(OrchestrationRequestContext::getInventoryRequest)
                .flatMap(client::restore)
                .subscribe();
    }
}

package net.shyshkin.study.webfluxpatterns.sec08.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreakerConfigCustomizer reviewServiceCBCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of(
                        "review-service",
                        builder -> builder
                                .minimumNumberOfCalls(2)
                                .waitDurationInOpenState(Duration.ofSeconds(10))
                );
    }
}

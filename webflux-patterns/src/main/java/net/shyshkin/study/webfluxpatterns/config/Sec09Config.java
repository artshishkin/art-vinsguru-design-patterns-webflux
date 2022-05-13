package net.shyshkin.study.webfluxpatterns.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({"net.shyshkin.study.webfluxpatterns.sec09"})
@Profile("sec09")
public class Sec09Config {
}

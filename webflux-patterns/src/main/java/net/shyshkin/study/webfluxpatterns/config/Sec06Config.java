package net.shyshkin.study.webfluxpatterns.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan({"net.shyshkin.study.webfluxpatterns.sec06"})
@Profile("sec06")
public class Sec06Config {
}

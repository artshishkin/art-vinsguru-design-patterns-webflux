package net.shyshkin.study.webfluxpatterns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = "net.shyshkin.study.webfluxpatterns.sec02")
@SpringBootApplication(scanBasePackages = "net.shyshkin.study.webfluxpatterns.config")
public class WebfluxPatternsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxPatternsApplication.class, args);
    }

}

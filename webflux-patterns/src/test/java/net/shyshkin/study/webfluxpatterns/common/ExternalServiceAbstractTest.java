package net.shyshkin.study.webfluxpatterns.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
        "logging.level.net.shyshkin=debug",
        "app.external.service.baseUrl=http://${SERVICES_HOST}:${SERVICES_PORT}"
})
@ContextConfiguration(initializers = ExternalServiceAbstractTest.Initializer.class)
public abstract class ExternalServiceAbstractTest {

    @Container
    protected static GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/vinsguru-external-services")
            .withExposedPorts(7070)
            .waitingFor(Wait.forLogMessage(".*Started ExternalServicesApplication.*\\n", 1));

    protected static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            String host = externalServices.getHost();
            Integer port = externalServices.getMappedPort(7070);

            System.setProperty("SERVICES_HOST", host);
            System.setProperty("SERVICES_PORT", String.valueOf(port));
        }
    }

}
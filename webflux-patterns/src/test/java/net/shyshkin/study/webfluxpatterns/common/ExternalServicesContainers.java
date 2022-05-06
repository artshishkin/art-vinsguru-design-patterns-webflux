package net.shyshkin.study.webfluxpatterns.common;

import lombok.Getter;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Getter
public class ExternalServicesContainers extends GenericContainer<ExternalServicesContainers> {

    private static ExternalServicesContainers instance;

    private static boolean containerStarted = false;

    private final GenericContainer<?> externalServices = new GenericContainer<>("artarkatesoft/vinsguru-external-services")
            .withExposedPorts(7070)
            .waitingFor(Wait.forLogMessage(".*Started ExternalServicesApplication.*\\n", 1));

    @Override
    public void start() {
        if (!containerStarted) {
            externalServices.start();
        }
        containerStarted = true;
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }

    public static ExternalServicesContainers getInstance() {
        if (instance == null)
            instance = new ExternalServicesContainers();
        return instance;
    }

}

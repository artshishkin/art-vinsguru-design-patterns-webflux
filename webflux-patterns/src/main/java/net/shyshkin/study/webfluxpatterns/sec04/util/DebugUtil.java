package net.shyshkin.study.webfluxpatterns.sec04.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.shyshkin.study.webfluxpatterns.sec04.dto.OrchestrationRequestContext;
import org.slf4j.Logger;

public class DebugUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public static void print(Logger log, OrchestrationRequestContext ctx) {
        try {
            String ctxJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ctx);
            log.debug("\n{}", ctxJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}

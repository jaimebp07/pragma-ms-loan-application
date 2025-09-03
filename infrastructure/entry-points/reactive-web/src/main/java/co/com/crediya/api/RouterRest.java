package co.com.crediya.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class RouterRest {
    @Bean
    RouterFunction<ServerResponse> routerFunction(HandlerV1 handlerV1, HandlerV2 handlerV2) {
        return RouterFunctions
            .route()
            .path("/api/v1", builder -> builder
                .POST("/solicitud", handlerV1::applyLoan))
            .path("/api/v2", builder -> builder
                .POST("/usecase/otherpath", handlerV2::listenPOSTUseCase))
            .build();
        }
}

package co.com.crediya.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.mockito.Mockito.*;

class RouterRestTest {

    private WebTestClient webTestClient;
    private HandlerV1 handlerV1;
    private HandlerV2 handlerV2;

    @BeforeEach
    void setUp() {
        handlerV1 = mock(HandlerV1.class);
        handlerV2 = mock(HandlerV2.class);

        RouterRest routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handlerV1, handlerV2);

        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    void testRouteV1ApplyLoan() {
        when(handlerV1.applyLoan(any())).thenReturn(ServerResponse.ok().bodyValue("OK"));

        webTestClient.post()
                .uri("/api/v1/solicitud")
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("OK");

        verify(handlerV1, times(1)).applyLoan(any());
    }

    @Test
    void testRouteV2OtherPath() {
        when(handlerV2.listenPOSTUseCase(any())).thenReturn(ServerResponse.ok().bodyValue("V2 OK"));

        webTestClient.post()
                .uri("/api/v2/usecase/otherpath")
                .bodyValue("{}")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("V2 OK");

        verify(handlerV2, times(1)).listenPOSTUseCase(any());
    }
}

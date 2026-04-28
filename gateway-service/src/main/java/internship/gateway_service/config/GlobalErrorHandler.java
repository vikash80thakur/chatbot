package internship.gateway_service.config;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
// here i'm giving higher priority ( lower number = higher priority )
@Order(-2)
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        // If no specific error matches, then this below will be used
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Something went wrong";

        String error = ex.getMessage();

        // 🔐 Auth errors
        if ("AUTH_HEADER_MISSING".equals(error)) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Authorization header missing or invalid";
        }

        else if ("JWT_INVALID".equals(error)) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Invalid or expired token";
        }

        // 🧾 Response JSON
        String response = String.format(
                "{\"status\": %d, \"message\": \"%s\", \"timestamp\": \"%s\"}",
                status.value(),
                message,
                LocalDateTime.now()
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(response.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

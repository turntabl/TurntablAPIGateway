package io.tuntabl;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.Optional;

@Component
public class JWTValidationFilter extends AbstractGatewayFilterFactory<JWTValidationFilter.Config> {
    public JWTValidationFilter() {
        super(Config.class);
    }

    private boolean isAuthorizationValid(String authorizationHeader) {
        /* Logic for checking the value */
        Optional<RSAPublicKey> publicKey = TokenValidation.getParsedPublicKey();
        return publicKey.filter(rsaPublicKey -> TokenValidation.isTokenValidated(authorizationHeader, rsaPublicKey)).isPresent();
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus httpStatus, String err)  {
        exchange.getResponse().setStatusCode(httpStatus);
        byte[] bytes = err.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("Authorization")) {
                return this.onError(exchange, HttpStatus.FORBIDDEN, "No Authorization Header");
            }

            String authorizationHeader = Objects.requireNonNull(request.getHeaders().get("Authorization")).get(0);

            if (!this.isAuthorizationValid(authorizationHeader)) {
                return this.onError(exchange,  HttpStatus.UNAUTHORIZED, "Invalid Authorization Token");
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().
                    header("secret").
                    build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }


    public static class Config {
        // Put the configuration properties
    }
}
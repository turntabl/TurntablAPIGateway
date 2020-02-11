package io.tuntabl;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPublicKey;
import java.util.Optional;

@Component
public class MyFilter extends AbstractGatewayFilterFactory<MyFilter.Config> {
    public MyFilter() {
        super(Config.class);
    }

    private boolean isAuthorizationValid(String authorizationHeader) {
        /* Logic for checking the value */
        Optional<RSAPublicKey> publicKey = TokenValidation.getParsedPublicKey();
        return publicKey.filter(rsaPublicKey -> TokenValidation.isTokenValidated(authorizationHeader, rsaPublicKey)).isPresent();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err)  {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        System.err.println("Error:....xxxx.... " + err);
        return response.setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey("Authorization")) {
                System.out.println("\"No Authorization header");
                return this.onError(exchange, "No Authorization header");
            }

            String authorizationHeader = request.getHeaders().get("Authorization").get(0);

            if (!this.isAuthorizationValid(authorizationHeader)) {
                return this.onError(exchange, "Invalid Authorization header");
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().
                    header("Bearer").
                    build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        };
    }


    public static class Config {
        // Put the configuration properties
    }
}
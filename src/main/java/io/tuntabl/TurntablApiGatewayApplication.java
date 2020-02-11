package io.tuntabl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TurntablApiGatewayApplication {
    JWTValidationFilter JWTValidationFilter = new JWTValidationFilter();

	public static void main(String[] args) {
		SpringApplication.run(TurntablApiGatewayApplication.class, args);
	}

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("hello", r ->
                        r.path("/hello/**")
                       .filters(f -> f.rewritePath("/hello/(?<segment>.*)", "/${segment}")
                               .filter(JWTValidationFilter.apply(JWTValidationFilter.newConfig())))
                        .uri("http://hello-service:5000"))
                .route("todo",
                        r -> r.path("/todo/**")
                        .filters(f -> f.rewritePath("/todo/(?<segment>.*)", "/${segment}")
                                .filter(JWTValidationFilter.apply(JWTValidationFilter.newConfig())))
                        .uri("http://todo-service:8080"))
                .build();
    }

}

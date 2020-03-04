package io.tuntabl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TurntablApiGatewayApplication {
    JWTValidationFilter JWTValidationFilter = new JWTValidationFilter();
    AddRequestHeaderGatewayFilterFactory addHeader = new AddRequestHeaderGatewayFilterFactory();

	public static void main(String[] args) {
		SpringApplication.run(TurntablApiGatewayApplication.class, args);
	}

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()   
                .route("holiday",
                    r -> r.path("/holiday/**")
                        .filters(f -> f.rewritePath("/holiday/(?<segment>.*)", "/${segment}") 
                        .filter(JWTValidationFilter.apply(JWTValidationFilter.newConfig())))
                        .uri("http://holiday:7070"))
                .route("permission",
                    r -> r.path("/permission/**")
                        .filters(f -> f.rewritePath("/permission/(?<segment>.*)", "/${segment}") )
                        .uri("http://permission:5000")) 
                .route("empire",
                    r -> r.path("/empire/**")
                        .filters(f -> f.rewritePath("/empire/(?<segment>.*)", "/${segment}") 
                        .filter(JWTValidationFilter.apply(JWTValidationFilter.newConfig())))
                        .uri("http://empire:8050"))
                .route("jaeger",
                    r -> r.path("/jaeger/**")
                        .filters(f -> f.rewritePath("/jaeger/(?<segment>.*)", "/${segment}") )
                        .uri("http://jaeger:16686"))
                .build();
    }

}
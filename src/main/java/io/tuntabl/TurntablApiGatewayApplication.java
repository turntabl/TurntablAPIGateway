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
                .route("gis",
                    r -> r.path("/gis/**")
                        .filters(f -> f.rewritePath("/gis/(?<segment>.*)", "/${segment}") )
                        .uri("http://gis:5004"))
                .route("holiday",
                    r -> r.path("/holiday/**")
                        .filters(f -> f.rewritePath("/holiday/(?<segment>.*)", "/${segment}") )
                        .uri("http://holiday:7070"))
                .route("chess",
                    r -> r.path("/chess/**")
                        .filters(f -> f.rewritePath("/chess/(?<segment>.*)", "/${segment}") )
                        .uri("http://chess:8080"))
                .route("permission",
                    r -> r.path("/permission/**")
                        .filters(f -> f.rewritePath("/permission/(?<segment>.*)", "/${segment}") )
                        .uri("http://permission:5000")) 
                .build();
    }

}
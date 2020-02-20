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
                .route("permission",
                        r -> r.path("/permission/**")
                            .filters(f -> f.rewritePath("/permission/(?<segment>.*)", "/${segment}")
                                    .filter(JWTValidationFilter.apply(JWTValidationFilter.newConfig())))
                            .uri("https://permission.services.turntabl.io"))
                .route("gis",
                        r -> r.path("/gis/**")
                            .filters(f -> f.rewritePath("/gis/(?<segment>.*)", "/${segment}") )
                            .uri("http://gis:5004"))
                .route("tt",
                        r -> r.path("/tt/**")
                            .filters(f -> f.rewritePath("/tt/(?<segment>.*)", "/${segment}") )
                            .uri("https://turntabl.io/"))
                .build();
    }

}

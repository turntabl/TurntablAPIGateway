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
                .route("hello",
                        r -> r.path("/hello/**")
                            .filters(f -> f.rewritePath("/hello/(?<segment>.*)", "/${segment}")
                            .filter(addHeader.apply(addHeader.newConfig().setName("serviceName").setValue("hello"))))
                            .uri("http://turnt-publi-1ios56ej76ufj-1502995462.us-east-2.elb.amazonaws.com/"))
                .route("holiday",
                        r -> r.path("/holiday/**")
                                .filters(f -> f.rewritePath("/holiday/(?<segment>.*)", "/${segment}")
                                        .filter(addHeader.apply(addHeader.newConfig().setName("serviceName").setValue("holiday"))))
                                .uri("http://turnt-publi-1ios56ej76ufj-1502995462.us-east-2.elb.amazonaws.com/"))
                .route("gis",
                        r -> r.path("/gis/**")
                                .filters(f -> f.rewritePath("/gis/(?<segment>.*)", "/${segment}")
                                        .filter(addHeader.apply(addHeader.newConfig().setName("serviceName").setValue("gis"))))
                                .uri("http://turnt-publi-1ios56ej76ufj-1502995462.us-east-2.elb.amazonaws.com/"))
                .route("chess",
                        r -> r.path("/chess/**")
                                .filters(f -> f.rewritePath("/chess/(?<segment>.*)", "/${segment}")
                                        .filter(addHeader.apply(addHeader.newConfig().setName("serviceName").setValue("chess"))))
                                .uri("http://turnt-publi-1ios56ej76ufj-1502995462.us-east-2.elb.amazonaws.com/"))
                .route("permission",
                        r -> r.path("/permission/**")
                                .filters(f -> f.rewritePath("/permission/(?<segment>.*)", "/${segment}")
                                        .filter(addHeader.apply(addHeader.newConfig().setName("serviceName").setValue("permission"))))
                                .uri("http://turnt-publi-1ios56ej76ufj-1502995462.us-east-2.elb.amazonaws.com/"))
                .build();
    }

}
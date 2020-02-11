package io.tuntabl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TurntablApiGatewayApplication {
    MyFilter myFilter = new MyFilter();

	public static void main(String[] args) {
		SpringApplication.run(TurntablApiGatewayApplication.class, args);
	}

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("hello", r ->
                        r.host("hello.**")
                       .filters(f -> f.filter(myFilter.apply(myFilter.newConfig())))
                        .uri("http://hello-service:5000"))
                .route("todo",
                        r -> r.host("todo.**")
                        .filters(f -> f.rewritePath("/todo/(?<segment>.*)", "/${segment}"))
                        .uri("http://todo-service:8080"))
                .build();
    }

}

package io.tuntabl;


import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DedupeResponseHeaderGatewayFilterFactory   extends AbstractGatewayFilterFactory<DedupeResponseHeaderGatewayFilterFactory.Config>{

    private static final String STRATEGY_KEY = "strategy";

    public DedupeResponseHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(NAME_KEY, STRATEGY_KEY);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> chain.filter(exchange).then(Mono.fromRunnable(() -> {
            dedupe(exchange.getResponse().getHeaders(), config);
        }));
    }

    public enum Strategy {
        /*
        Default: Retain the first value only.
         */
        RETAIN_FIRST,

        /*
        Retain the last value only.
         */
        RETAIN_LAST,

        /*
        Retain all unique values in the order of their first encounter.
         */
        RETAIN_UNIQUE
    }

    void dedupe(HttpHeaders headers, Config config) {
        String names = config.getName();
        Strategy strategy = config.getStrategy();
        if (headers == null || names == null || strategy == null) {
            return;
        }
        for (String name : names.split(" ")) {
            dedupe(headers, name.trim(), strategy);
        }
    }

    private void dedupe(HttpHeaders headers, String name, Strategy strategy) {
        List<String> values = headers.get(name);
        if (values == null || values.size() <= 1) {
            return;
        }
        switch (strategy) {
            case RETAIN_FIRST:
                headers.set(name, values.get(0));
                break;
            case RETAIN_LAST:
                headers.set(name, values.get(values.size() - 1));
                break;
            case RETAIN_UNIQUE:
                headers.put(name, values.stream().distinct().collect(Collectors.toList()));
                break;
            default:
                break;
        }
    }

    public static class Config extends AbstractGatewayFilterFactory.NameConfig {
        private Strategy strategy = Strategy.RETAIN_FIRST;

        public Strategy getStrategy() {
            return strategy;
        }

        public Config setStrategy(Strategy strategy) {
            this.strategy = strategy;
            return this;
        }
    }
}
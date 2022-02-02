package ru.tehworks.scriptgo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import ru.tehworks.scriptgo.handler.GreetingHandler;

/**
 * @author Marat Sadretdinov
 */
@Configuration(proxyBeanMethods = false)
@PropertySource("file:setting.properties")
public class GreetingRouter {
    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {

        RequestPredicate route = RequestPredicates
                .GET("/hello")
                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN));

        return RouterFunctions
                .route(route, greetingHandler::hello)
                .andRoute(
                        RequestPredicates.GET("/index"),
                        greetingHandler::index
                );
    }

}

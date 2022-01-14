package ru.tehworks.scriptgo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.server.*;
import ru.tehworks.scriptgo.DAO.TehDAO;
import ru.tehworks.scriptgo.handler.GreetingHandler;

import java.io.*;

/**
 * @author Marat Sadretdinov
 */
@Configuration(proxyBeanMethods = false)
@PropertySource("file:setting.properties")
@EnableScheduling
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

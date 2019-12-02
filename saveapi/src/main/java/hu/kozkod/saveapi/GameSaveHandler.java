package hu.kozkod.saveapi;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class GameSaveHandler {
    private final GameSaveRepository repository;

    public GameSaveHandler(GameSaveRepository repository) {
        this.repository = repository;
    }

    @Bean
    RouterFunction<ServerResponse> saveApiRoutes() {
        return route(POST("/").and(accept(APPLICATION_JSON)), this::save)
                .andRoute(GET("/"), this::load);
    }

    private Mono<ServerResponse> save(ServerRequest req) {
        return req.bodyToMono(String.class)
                .map(GameSave::new)
                .flatMap(repository::save)
                .flatMap(gameSave ->
                        ServerResponse
                                .created(req.uriBuilder()
                                        .pathSegment(gameSave.getId().toString())
                                        .build())
                                .bodyValue(gameSave));
    }

    private Mono<ServerResponse> load(ServerRequest req) {
        return repository.findAll()
                .next()
                .map(GameSave::getData)
                .flatMap(gameSaveData -> ServerResponse.ok().bodyValue(gameSaveData))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}

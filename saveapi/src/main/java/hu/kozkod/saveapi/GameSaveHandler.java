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
                .andRoute(GET("/"), this::listAll)
                .andRoute(GET("/{level}"), this::load);
    }

    private Mono<ServerResponse> save(ServerRequest req) {
        return req.bodyToMono(GameSave.class)
                .flatMap(repository::save)
                .flatMap(gameSave ->
                        ServerResponse
                                .created(req.uriBuilder()
                                        .pathSegment(gameSave.getLevel())
                                        .build())
                                .bodyValue(gameSave));
    }

    private Mono<ServerResponse> listAll(ServerRequest req) {
        return repository.findAll()
                .collectList()
                .flatMap(saves -> ServerResponse.ok().body(saves, GameSave.class));
    }

    private Mono<ServerResponse> load(ServerRequest req) {
        return repository.findById(req.pathVariable("level"))
                .map(GameSave::getData)
                .flatMap(gameSaveData -> ServerResponse.ok().bodyValue(gameSaveData))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}

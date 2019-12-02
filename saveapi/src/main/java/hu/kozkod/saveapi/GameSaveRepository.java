package hu.kozkod.saveapi;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameSaveRepository extends ReactiveMongoRepository<GameSave, UUID> {
}

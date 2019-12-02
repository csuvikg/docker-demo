package hu.kozkod.saveapi;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameSaveRepository extends ReactiveMongoRepository<GameSave, String> {
}

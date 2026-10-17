package ng.ourChemo.data.repositories;

import ng.ourChemo.data.models.Batch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BatchRepository extends MongoRepository<Batch, String> {
    List<Batch> findByDrugId(String drugId);
}

package ng.ourChemo.data.repositories;

import ng.ourChemo.data.models.DispensedDrugs;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DispensedDrugsRepository extends MongoRepository<DispensedDrugs, String> {
}

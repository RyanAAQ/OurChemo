package ng.ourChemo.data.repositories;

import ng.ourChemo.data.models.Drug;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DrugRepository extends MongoRepository<Drug, String> {

    Drug findByName(String name);

    Drug findByNameAndBrand(String name, String brand);

    List<Drug> findDrugsByName(String word);

    List<Drug> findByGenericName(String word);

    List<Drug> findByBrand(String word);
}

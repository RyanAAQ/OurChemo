package ng.ourChemo.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "dispensed_drugs")
@Data
public class DispensedDrugs {
    @Id
    private String id;
    private List<DispensedDrug> items;
    private LocalDateTime saleDateTime;
    private String dispensedBy;

}

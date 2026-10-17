package ng.ourChemo.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Document(collection = "drugs")
public class Drug {
    @Id
    private String id;
    private String name;
    private String genericName;
    private String strength;
    private String dosageForm;
    private String brand;
    private int unitPrice;
    private int quantity;
    private LocalDate expiryDate;
    private List<Batch> batches;
}

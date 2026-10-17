package ng.ourChemo.data.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "batches")
@Data
public class Batch {
    @Id
    private String id;
    private String drugId;
    private int costPrice;
    private int purchaseQuantity;
    private int quantityLeft;
    private LocalDate purchaseDate;
}

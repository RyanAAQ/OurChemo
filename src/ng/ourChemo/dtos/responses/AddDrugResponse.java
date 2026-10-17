package ng.ourChemo.dtos.responses;

import lombok.Data;
import ng.ourChemo.data.models.Batch;
import org.springframework.data.annotation.Id;

import java.util.List;

@Data
public class AddDrugResponse {
    @Id
    private String id;
    private String name;
    private String genericName;
    private String brand;
    private int unitPrice;
    private int totalQuantity;
    private int totalDrugs;
    private List<Batch> drugBatch;
    private String message;

}

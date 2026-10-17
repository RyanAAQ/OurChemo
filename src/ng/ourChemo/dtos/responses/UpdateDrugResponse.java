package ng.ourChemo.dtos.responses;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class UpdateDrugResponse {
    @Id
    private String id;
    private String name;
    private String brand;
    private int price;
    private String message;

}

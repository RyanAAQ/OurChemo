package ng.ourChemo.dtos.responses;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class DeleteDrugResponse {
    @Id
    private String id;
    private String message;

}

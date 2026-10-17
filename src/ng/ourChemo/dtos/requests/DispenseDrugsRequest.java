package ng.ourChemo.dtos.requests;

import lombok.Data;
import ng.ourChemo.data.models.DispensedDrug;

import java.util.List;

@Data
public class DispenseDrugsRequest {
    private String username;
    private List<DispensedDrug> items;

}

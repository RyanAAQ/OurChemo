package ng.ourChemo.dtos.requests;

import lombok.Data;

@Data
public class UpdateDrugRequest {
    private String name;
    private String brand;
    private int price;
}

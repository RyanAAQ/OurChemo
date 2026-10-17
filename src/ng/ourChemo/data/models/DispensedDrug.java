package ng.ourChemo.data.models;

import lombok.Data;

@Data
public class DispensedDrug {
    private String batchId;
    private String drugId;
    private String drugName;
    private int quantity;
    private int totalPrice;

}

package ng.ourChemo.dtos.requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddDrugRequest {
    private String name;
    private String genericName;
    private String strength;
    private String dosageForm;
    private String brand;
    private int unitPrice;
    private int costPrice;
    private int purchaseQuantity;
    private LocalDate expiryDate;
}

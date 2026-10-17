package ng.ourChemo.dtos.responses;

import lombok.Data;

@Data
public class DispenseDrugsResponse {
    private int savedCount;
    private int totalAmount;
    private String message;
}

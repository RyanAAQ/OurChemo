package ng.ourChemo.dtos.responses;

import lombok.Data;
import ng.ourChemo.data.models.Drug;

import java.util.List;

@Data
public class SearchDrugResponse {
    private List<Drug> drugs;
}

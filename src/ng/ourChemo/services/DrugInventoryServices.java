package ng.ourChemo.services;

import ng.ourChemo.data.models.DispensedDrugs;
import ng.ourChemo.data.models.Drug;
import ng.ourChemo.dtos.requests.AddDrugRequest;
import ng.ourChemo.dtos.requests.DeleteDrugRequest;
import ng.ourChemo.dtos.requests.DispenseDrugsRequest;
import ng.ourChemo.dtos.requests.SearchDrugRequest;
import ng.ourChemo.dtos.requests.UpdateDrugRequest;
import ng.ourChemo.dtos.responses.AddDrugResponse;
import ng.ourChemo.dtos.responses.DeleteDrugResponse;
import ng.ourChemo.dtos.responses.DispenseDrugsResponse;
import ng.ourChemo.dtos.responses.SearchDrugResponse;
import ng.ourChemo.dtos.responses.UpdateDrugResponse;

import java.util.List;

public interface DrugInventoryServices {
    AddDrugResponse addDrug(AddDrugRequest request);
    UpdateDrugResponse updateDrug(UpdateDrugRequest request);
    DeleteDrugResponse deleteDrug(DeleteDrugRequest request);
    DispenseDrugsResponse dispenseDrugs(DispenseDrugsRequest request);
    SearchDrugResponse searchDrug(SearchDrugRequest request);
    List<Drug> getAllDrugs();
    List<DispensedDrugs> getDispenseHistory();
}

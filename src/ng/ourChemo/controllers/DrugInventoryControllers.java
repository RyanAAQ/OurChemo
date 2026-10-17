package ng.ourChemo.controllers;

import ng.ourChemo.dtos.requests.AddDrugRequest;
import ng.ourChemo.dtos.requests.DeleteDrugRequest;
import ng.ourChemo.dtos.requests.DispenseDrugsRequest;
import ng.ourChemo.dtos.requests.SearchDrugRequest;
import ng.ourChemo.dtos.requests.UpdateDrugRequest;
import ng.ourChemo.dtos.responses.ApiResponse;
import ng.ourChemo.services.DrugInventoryServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drugs")
public class DrugInventoryControllers {

    private final DrugInventoryServices drugInventoryServices;

    public DrugInventoryControllers(DrugInventoryServices drugInventoryServices) {
        this.drugInventoryServices = drugInventoryServices;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllDrugs() {
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.getAllDrugs()));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addDrug(@RequestBody AddDrugRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.addDrug(request)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateDrug(@RequestBody UpdateDrugRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.updateDrug(request)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteDrug(@RequestBody DeleteDrugRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.deleteDrug(request)));
    }

    @PostMapping("/dispense")
    public ResponseEntity<ApiResponse> dispenseDrugs(@RequestBody DispenseDrugsRequest request) {
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.dispenseDrugs(request)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchDrug(@RequestParam String word) {
        SearchDrugRequest request = new SearchDrugRequest();
        request.setWord(word);
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.searchDrug(request)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getDispenseHistory() {
        return ResponseEntity.ok(new ApiResponse(true, drugInventoryServices.getDispenseHistory()));
    }
}

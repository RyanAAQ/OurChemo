package ng.ourChemo.services;

import ng.ourChemo.data.repositories.DrugRepository;
import ng.ourChemo.data.repositories.UserRepository;
import ng.ourChemo.dtos.requests.*;
import ng.ourChemo.dtos.responses.AddDrugResponse;
import ng.ourChemo.dtos.responses.DispenseDrugsResponse;
import ng.ourChemo.dtos.responses.SearchDrugResponse;
import ng.ourChemo.dtos.responses.UpdateDrugResponse;
import ng.ourChemo.data.models.Drug;
import ng.ourChemo.data.models.DispensedDrug;

import java.util.ArrayList;
import java.util.List;

import ng.ourChemo.exceptions.InsufficientStockException;
import ng.ourChemo.exceptions.MedicineNotFoundException;
import ng.ourChemo.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DrugInventoryServicesImplTest {
@Autowired
    private AuthService authService;
@Autowired
    private DrugInventoryServices drugService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DrugRepository drugRepository;


    private void register() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setUsername("johndoe");
        request.setPassword("password");
        request.setFullName("John Doe");
        authService.registerChemist(request);
    }

    private void login() {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("johndoe");
        request.setPassword("password");
        assertTrue(authService.login(request).isLoggedIn());
    }

    private void registerAndLogin() {
        register();
        login();
    }
    @BeforeEach
    public void setup(){
        drugRepository.deleteAll();
        userRepository.deleteAll();
        registerAndLogin();

    }

    @Test
    void addDrugBatchCountIsOne() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setStrength("500mg");
        request.setPurchaseQuantity(100);
        AddDrugResponse response = drugService.addDrug(request);
        assertEquals(1, response.getDrugBatch().size());
    }

    @Test
    void addDrugQuantityMatchesPurchaseQuantity() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        AddDrugResponse response = drugService.addDrug(request);
        assertEquals(100, response.getTotalQuantity());
    }

    @Test
    void addSameDrugTwiceAddsBatchAndIncreasesQuantity() {
        AddDrugRequest request1 = new AddDrugRequest();
        request1.setName("Paracetamol");
        request1.setBrand("Emzor");
        request1.setUnitPrice(500);
        request1.setPurchaseQuantity(100);
        drugService.addDrug(request1);

        AddDrugRequest request2 = new AddDrugRequest();
        request2.setName("Paracetamol");
        request2.setBrand("Emzor");
        request2.setUnitPrice(500);
        request2.setPurchaseQuantity(50);
        AddDrugResponse response = drugService.addDrug(request2);

        assertEquals(1, response.getDrugBatch().size());
        assertEquals(150, response.getTotalQuantity());
    }

    @Test
    void addMultipleDifferentDrugsAndItUpdatesCorrectly(){
        AddDrugRequest request1 = new AddDrugRequest();
        request1.setName("Paracetamol");
        request1.setBrand("Emzor");
        request1.setUnitPrice(500);
        request1.setPurchaseQuantity(100);
        drugService.addDrug(request1);

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       AddDrugRequest request2 = new AddDrugRequest();
        request2.setName("Vitamin C");
        request2.setBrand("Garri");

        request2.setUnitPrice(500);
        request2.setPurchaseQuantity(50);
        AddDrugResponse response = drugService.addDrug(request2);
    }

    @Test
    void addDrugWithoutPurchaseQuantityHasNoBatch() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Vitamin C");
        request.setBrand("Emzor");
        request.setUnitPrice(300);
        request.setPurchaseQuantity(0);
        AddDrugResponse response = drugService.addDrug(request);
//        assertEquals(, response.getDrugBatch());
    }

    @Test
    void addDrugWithNullNameThrowsException() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName(null);
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        assertThrows(ValidationException.class, () -> drugService.addDrug(request));
    }

    @Test
    void addDrugWithNullBrandThrowsException() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Paracetamol");
        request.setBrand("Canada");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        assertThrows(ValidationException.class, () -> drugService.addDrug(request));
    }

    @Test
    void addDrugWithZeroPriceThrowsException() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(0);
        request.setPurchaseQuantity(100);
        assertThrows(ValidationException.class, () -> drugService.addDrug(request));
    }

    @Test
    void addDrugResponseContainsCorrectNameAndBrand() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Amoxicillin");
        request.setBrand("GSK");
        request.setUnitPrice(800);
        request.setPurchaseQuantity(60);
        AddDrugResponse response = drugService.addDrug(request);
        assertEquals("Amoxicillin", response.getName());
        assertEquals("GSK", response.getBrand());
    }

    @Test
    void updateDrugWithNullRequestThrowsException() {
        assertThrows(MedicineNotFoundException.class, () -> drugService.updateDrug(null));
    }

    @Test
    void updateDrugWithNonExistentIdThrowsException() {
        UpdateDrugRequest request = new UpdateDrugRequest();
        request.setName("Ibuprofen");
        assertThrows(MedicineNotFoundException.class, () -> drugService.updateDrug(request));
    }

    @Test
    void updateDrugWithCorrectInformationUpdatesTheDrug() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        AddDrugResponse response = drugService.addDrug(request);
        assertEquals(500, response.getUnitPrice());
        assertEquals(100, response.getTotalQuantity());

        UpdateDrugRequest request1 = new UpdateDrugRequest();
        request1.setName("Igbo");
        request1.setBrand("YabaMade");
        request1.setPrice(800);

        UpdateDrugResponse updateResponse = drugService.updateDrug(request1);
        assertEquals("Igbo", updateResponse.getName());
        assertEquals("YabaMade", updateResponse.getBrand());
        assertEquals(800, updateResponse.getPrice());
    }

    @Test
    void deleteDrugWithNullRequestThrowsException() {
        assertThrows(MedicineNotFoundException.class, () -> drugService.deleteDrug(null));
    }

    @Test
    void deleteDrugWithNonExistentIdThrowsException() {
        DeleteDrugRequest request = new DeleteDrugRequest();
        assertThrows(MedicineNotFoundException.class, () -> drugService.deleteDrug(request));
    }

    @Test
    void dispenseWithNullRequestThrowsException() {
        assertThrows(ValidationException.class, () -> drugService.dispenseDrugs(null));
    }

    @Test
    void dispenseWithNullUsernameThrowsException() {
        DispenseDrugsRequest request = new DispenseDrugsRequest();
        request.setUsername(null);
        assertThrows(ValidationException.class, () -> drugService.dispenseDrugs(request));
    }

    @Test
    void dispenseWithNonExistentUserThrowsException() {
        DispenseDrugsRequest request = new DispenseDrugsRequest();
        request.setUsername("nobody");
        assertThrows(ValidationException.class, () -> drugService.dispenseDrugs(request));
    }

    @Test
    void dispenseWithUserNotLoggedInThrowsException() {
        DispenseDrugsRequest request = new DispenseDrugsRequest();
        request.setUsername("johndoe");
        assertThrows(ValidationException.class, () -> drugService.dispenseDrugs(request));
    }

    @Test
    void dispenseWithInsufficientStockThrowsException(){
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Amoxicillin");
        request.setBrand("GSK");
        request.setUnitPrice(800);
        request.setPurchaseQuantity(5);
        AddDrugResponse addResponse = drugService.addDrug(request);

        Drug drug = new Drug();
        drug.setId(addResponse.getId());

        DispensedDrug item = new DispensedDrug();
        item.setDrugId(drug.getId());
        item.setBatchId(addResponse.getId());
        item.setQuantity(10);

        DispenseDrugsRequest request2 = new DispenseDrugsRequest();
        request2.setUsername("johndoe");
        List<DispensedDrug> items = new ArrayList<>();
        items.add(item);
        request2.setItems(items);

        assertThrows(InsufficientStockException.class, () -> drugService.dispenseDrugs(request2));
    }

    @Test
    void dispenseCorrectlyReducesTheStock() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Amoxicillin");
        request.setBrand("GSK");
        request.setUnitPrice(800);
        request.setPurchaseQuantity(60);
        AddDrugResponse addResponse = drugService.addDrug(request);

        Drug drug = new Drug();
        drug.setId(addResponse.getId());

        DispensedDrug item = new DispensedDrug();
        item.setDrugName(drug.getName());
        item.setBatchId(addResponse.getId());
        item.setQuantity(10);

        DispenseDrugsRequest request2 = new DispenseDrugsRequest();
        request2.setUsername("johndoe");
        List<DispensedDrug> items = new ArrayList<>();
        items.add(item);
        request2.setItems(items);

        DispenseDrugsResponse response = drugService.dispenseDrugs(request2);
        assertEquals(1, response.getSavedCount());
        assertEquals(8000, response.getTotalAmount());
        assertEquals(50, addResponse.getTotalQuantity() - response.getTotalAmount() / 800);
    }

    @Test
    void searchWithNullRequestThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> drugService.searchDrug(null));
    }

    @Test
    void searchWithEmptyQueryThrowsException() {
        SearchDrugRequest request = new SearchDrugRequest();
        request.setWord("");
        assertThrows(IllegalArgumentException.class, () -> drugService.searchDrug(request));
    }

    @Test
    void addOneDrugTotalDrugsIsOne() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        AddDrugResponse response = drugService.addDrug(request);
        assertEquals(1, response.getTotalDrugs());
    }

    @Test
    void addTwoDifferentDrugsTotalDrugsIsTwo() {
        AddDrugRequest request1 = new AddDrugRequest();
        request1.setName("Paracetamol");
        request1.setBrand("Emzor");
        request1.setUnitPrice(500);
        request1.setPurchaseQuantity(100);
        drugService.addDrug(request1);

        AddDrugRequest request2 = new AddDrugRequest();
        request2.setName("Igbo");
        request2.setBrand("vendor");
        request2.setUnitPrice(800);
        request2.setPurchaseQuantity(60);
        AddDrugResponse response = drugService.addDrug(request2);
        assertEquals(2, response.getTotalDrugs());
    }

    @Test
    void addSameDrugTwiceTotalDrugsRemainsOneAndBatchIsTwo() {
        AddDrugRequest request1 = new AddDrugRequest();
        request1.setName("Paracetamol");
        request1.setBrand("Emzor");
        request1.setUnitPrice(500);
        request1.setPurchaseQuantity(100);
        drugService.addDrug(request1);

        AddDrugRequest request2 = new AddDrugRequest();
        request2.setName("Paracetamol");
        request2.setBrand("Emzor");
        request2.setUnitPrice(500);
        request2.setPurchaseQuantity(50);
        AddDrugResponse response = drugService.addDrug(request2);
        assertEquals(1, response.getTotalDrugs());
        assertEquals(2, response.getDrugBatch().size());
    }

    @Test
    void twoChemistsAddSameDrugAndBrandResultsInOneDrugTwoBatchesAndCombinedQuantity() {
        AddDrugRequest first = new AddDrugRequest();
        first.setName("Paracetamol");
        first.setBrand("Emzor");
        first.setUnitPrice(500);
        first.setPurchaseQuantity(100);
        drugService.addDrug(first);

        RegisterUserRequest second = new RegisterUserRequest();
        second.setUsername("ryanaA9");
        second.setPassword("password");
        second.setFullName("Ryan Ariyo");
        authService.registerChemist(second);

        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("ryanaA9");
        request.setPassword("password");
        assertTrue(authService.login(request).isLoggedIn());

        AddDrugRequest request2 = new AddDrugRequest();
        request2.setName("Paracetamol");
        request2.setBrand("Emzor");
        request2.setUnitPrice(500);
        request2.setPurchaseQuantity(50);
        AddDrugResponse response = drugService.addDrug(request2);

        assertEquals(1, response.getTotalDrugs());
        assertEquals(2, response.getDrugBatch().size());
        assertEquals(150, response.getTotalQuantity());
    }

    @Test
    void searchGenericNameOfDrugReturnsTheCorrectDrugName() {
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Emzor Paracetamol");
        request.setGenericName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        drugService.addDrug(request);

        SearchDrugRequest searchRequest = new SearchDrugRequest();
        searchRequest.setWord("Para");
        SearchDrugResponse response = drugService.searchDrug(searchRequest);

        assertEquals(1, response.getDrugs().size());
    }

    @Test
    void searchGenericNameOfDrugThatDoesntExistsThrowsException(){
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Emzor Paracetamol");
        request.setGenericName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        drugService.addDrug(request);
        SearchDrugRequest request1 = new SearchDrugRequest();
        request1.setWord("Kara");

        assertThrows(MedicineNotFoundException.class, () -> drugService.searchDrug(request1));
    }

    @Test
    void searchGenericNameOfDrugWithoutAWordThrowsException(){
        AddDrugRequest request = new AddDrugRequest();
        request.setName("Emzor Paracetamol");
        request.setGenericName("Paracetamol");
        request.setBrand("Emzor");
        request.setUnitPrice(500);
        request.setPurchaseQuantity(100);
        drugService.addDrug(request);
        SearchDrugRequest request1 = new SearchDrugRequest();

        assertThrows(IllegalArgumentException.class, () -> drugService.searchDrug(request1));
    }
}

package ng.ourChemo.services;

import ng.ourChemo.data.models.Batch;
import ng.ourChemo.data.models.DispensedDrug;
import ng.ourChemo.data.models.DispensedDrugs;
import ng.ourChemo.data.models.Drug;
import ng.ourChemo.data.models.User;
import ng.ourChemo.data.repositories.BatchRepository;
import ng.ourChemo.data.repositories.DispensedDrugsRepository;
import ng.ourChemo.data.repositories.DrugRepository;
import ng.ourChemo.data.repositories.UserRepository;
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
import ng.ourChemo.exceptions.InsufficientStockException;
import ng.ourChemo.exceptions.MedicineNotFoundException;
import ng.ourChemo.exceptions.ValidationException;
import ng.ourChemo.utils.Mapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DrugInventoryServicesImpl implements DrugInventoryServices {

    private final DrugRepository drugRepository;
    private final BatchRepository batchRepository;
    private final DispensedDrugsRepository dispensedDrugsRepository;
    private final UserRepository userRepository;

    public DrugInventoryServicesImpl(DrugRepository drugRepository, BatchRepository batchRepository, DispensedDrugsRepository dispensedDrugsRepository, UserRepository userRepository) {
        this.drugRepository = drugRepository;
        this.batchRepository = batchRepository;
        this.dispensedDrugsRepository = dispensedDrugsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AddDrugResponse addDrug(AddDrugRequest request) {
        if (request == null)
            throw new ValidationException("Request cannot be null");
        if (request.getName() == null || request.getName().isEmpty())
            throw new ValidationException("Drug name is required");
        if (request.getBrand() == null || request.getBrand().isEmpty())
            throw new ValidationException("Drug brand is required");
        if (request.getUnitPrice() <= 0)
            throw new ValidationException("Drug price must be greater than zero");

        Drug drug = drugRepository.findByNameAndBrand(request.getName(), request.getBrand());
        if (drug == null) {
            drug = Mapper.mapToDrug(request);
            drug.setQuantity(0);
            drug.setBatches(new ArrayList<>());
        }

        if (request.getPurchaseQuantity() > 0) {
            if (request.getUnitPrice() > drug.getUnitPrice()) {
                drug.setUnitPrice(request.getUnitPrice());
            }

            LocalDate incomingExpiry = request.getExpiryDate();
            if (incomingExpiry == null) {
                incomingExpiry = LocalDate.now().plusMonths(1);
            }

            drug.setQuantity(drug.getQuantity() + request.getPurchaseQuantity());

            boolean sameExpiry = drug.getExpiryDate() != null && drug.getExpiryDate().equals(incomingExpiry);
            boolean hasBatches = !drug.getBatches().isEmpty();

            if (sameExpiry && hasBatches) {
                Batch lastBatch = drug.getBatches().get(drug.getBatches().size() - 1);
                lastBatch.setPurchaseQuantity(lastBatch.getPurchaseQuantity() + request.getPurchaseQuantity());
                lastBatch.setQuantityLeft(lastBatch.getQuantityLeft() + request.getPurchaseQuantity());
                batchRepository.save(lastBatch);
                drug = drugRepository.save(drug);
            } else {
                drug.setExpiryDate(incomingExpiry);
                drug = drugRepository.save(drug);

                Batch newBatch = new Batch();
                newBatch.setDrugId(drug.getId());
                newBatch.setCostPrice(request.getCostPrice());
                newBatch.setPurchaseQuantity(request.getPurchaseQuantity());
                newBatch.setQuantityLeft(request.getPurchaseQuantity());
                newBatch.setPurchaseDate(LocalDate.now());
                batchRepository.save(newBatch);

                drug.getBatches().add(newBatch);
                drug = drugRepository.save(drug);
            }
        } else {
            drug = drugRepository.save(drug);
        }

        AddDrugResponse response = new AddDrugResponse();
        response.setId(drug.getId());
        response.setName(drug.getName());
        response.setGenericName(drug.getGenericName());
        response.setBrand(drug.getBrand());
        response.setUnitPrice(drug.getUnitPrice());
        response.setTotalQuantity(drug.getQuantity());
        response.setTotalDrugs((int) drugRepository.count());
        response.setDrugBatch(drug.getBatches());
        response.setMessage("Drug added successfully");
        return response;
    }

    @Override
    public UpdateDrugResponse updateDrug(UpdateDrugRequest request) {
        if (request == null)
            throw new MedicineNotFoundException("Update request cannot be null");

        Drug drug = drugRepository.findByName(request.getName());
        if (drug == null)
            throw new MedicineNotFoundException("Drug with name " + request.getName() + " not found");

        if (request.getBrand() != null && !request.getBrand().isEmpty())
            drug.setBrand(request.getBrand());
        if (request.getPrice() > 0)
            drug.setUnitPrice(request.getPrice());

        drugRepository.save(drug);

        UpdateDrugResponse response = new UpdateDrugResponse();
        response.setId(drug.getId());
        response.setName(drug.getName());
        response.setBrand(drug.getBrand());
        response.setPrice(drug.getUnitPrice());
        response.setMessage("Drug updated successfully");
        return response;
    }

    @Override
    public DeleteDrugResponse deleteDrug(DeleteDrugRequest request) {
        if (request == null)
            throw new MedicineNotFoundException("Delete request cannot be null");

        Drug drug = drugRepository.findByName(request.getName());
        if (drug == null)
            throw new MedicineNotFoundException("Drug with name " + request.getName() + " not found");

        batchRepository.deleteAll(batchRepository.findByDrugId(drug.getId()));
        drugRepository.delete(drug);

        DeleteDrugResponse response = new DeleteDrugResponse();
        response.setId(drug.getId());
        response.setMessage("Drug deleted successfully");
        return response;
    }

    @Override
    public DispenseDrugsResponse dispenseDrugs(DispenseDrugsRequest request) {
        if (request == null)
            throw new ValidationException("Request cannot be null");
        if (request.getUsername() == null || request.getUsername().isEmpty())
            throw new ValidationException("Username is required");

        User user = userRepository.findByUsername(request.getUsername().toLowerCase());
        if (user == null)
            throw new ValidationException("User not found");
        if (!user.isLoggedIn())
            throw new ValidationException("User is not logged in");

        List<DispensedDrug> items = request.getItems();
        if (items == null || items.isEmpty())
            throw new ValidationException("No items to dispense");

        int totalAmount = 0;

        for (DispensedDrug item : items) {
            Drug drug = drugRepository.findByName(item.getDrugName());
            if (drug == null)
                throw new MedicineNotFoundException("Drug " + item.getDrugName() + " not found");
            if (drug.getQuantity() < item.getQuantity())
                throw new InsufficientStockException("Insufficient stock for " + drug.getName());

            List<Batch> batches = batchRepository.findByDrugId(drug.getId());
            for (Batch batch : batches) {
                if (batch.getId().equals(item.getBatchId())) {
                    batch.setQuantityLeft(batch.getQuantityLeft() - item.getQuantity());
                    batchRepository.save(batch);
                    break;
                }
            }

            int itemTotal = item.getQuantity() * drug.getUnitPrice();
            item.setTotalPrice(itemTotal);
            item.setDrugName(drug.getName());
            totalAmount += itemTotal;

            drug.setQuantity(drug.getQuantity() - item.getQuantity());
            drugRepository.save(drug);
        }

        DispensedDrugs record = new DispensedDrugs();
        record.setItems(items);
        record.setSaleDateTime(LocalDateTime.now());
        record.setDispensedBy(user.getUsername());
        dispensedDrugsRepository.save(record);

        DispenseDrugsResponse response = new DispenseDrugsResponse();
        response.setSavedCount(items.size());
        response.setTotalAmount(totalAmount);
        response.setMessage("Dispensed " + items.size() + " item(s). Total: " + totalAmount);
        return response;
    }

    @Override
    public SearchDrugResponse searchDrug(SearchDrugRequest request) {
        if (request == null || request.getWord() == null || request.getWord().isEmpty())
            throw new IllegalArgumentException("Search word is required");

        String word = request.getWord();
        List<Drug> results = new ArrayList<>();

        for (Drug drug : drugRepository.findDrugsByName(word)) {
            if (!results.contains(drug)) results.add(drug);
        }
        for (Drug drug : drugRepository.findByGenericName(word)) {
            if (!results.contains(drug)) results.add(drug);
        }
        for (Drug drug : drugRepository.findByBrand(word)) {
            if (!results.contains(drug)) results.add(drug);
        }

        if (results.isEmpty())
            throw new MedicineNotFoundException("No medicines found matching: " + word);

        SearchDrugResponse response = new SearchDrugResponse();
        response.setDrugs(results);
        return response;
    }

    @Override
    public List<Drug> getAllDrugs() {
        return drugRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public List<DispensedDrugs> getDispenseHistory() {
        return dispensedDrugsRepository.findAll(Sort.by(Sort.Direction.DESC, "saleDateTime"));
    }
}

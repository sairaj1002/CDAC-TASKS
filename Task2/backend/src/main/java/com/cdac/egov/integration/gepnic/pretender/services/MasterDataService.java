package com.cdac.egov.integration.gepnic.pretender.services;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterEntity;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterTenderType;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterNumberOfCovers;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterTenderCategory;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterWorkItemProductCategory;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterNumberOfBidOpeners;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterFormOfContract;
import com.cdac.egov.integration.gepnic.pretender.entities.MasterTendererClass;
import com.cdac.egov.integration.gepnic.pretender.forms.MasterOptionResponse;
import com.cdac.egov.integration.gepnic.pretender.forms.MasterOptionRequest;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterTenderTypeRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterNumberOfCoversRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterTenderCategoryRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterWorkItemProductCategoryRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterNumberOfBidOpenersRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterFormOfContractRepository;
import com.cdac.egov.integration.gepnic.pretender.repositories.MasterTendererClassRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class MasterDataService {
    private final MasterTenderTypeRepository tenderTypes;
    private final MasterNumberOfCoversRepository covers;
    private final MasterTenderCategoryRepository categories;
    private final MasterWorkItemProductCategoryRepository productCategories;
    private final MasterNumberOfBidOpenersRepository bidOpeners;
    private final MasterFormOfContractRepository contracts;
    private final MasterTendererClassRepository tendererClasses;

    public MasterDataService(MasterTenderTypeRepository tenderTypes, MasterNumberOfCoversRepository covers,
        MasterTenderCategoryRepository categories, MasterWorkItemProductCategoryRepository productCategories,
        MasterNumberOfBidOpenersRepository bidOpeners, MasterFormOfContractRepository contracts,
        MasterTendererClassRepository tendererClasses) {
        this.tenderTypes = tenderTypes; this.covers = covers; this.categories = categories;
        this.productCategories = productCategories; this.bidOpeners = bidOpeners; this.contracts = contracts;
        this.tendererClasses = tendererClasses;
    }

    @Transactional(readOnly = true)
    public List<MasterOptionResponse> list(String key) {
        return repository(key).findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
            .map(entity -> response((MasterEntity) entity)).toList();
    }

    public MasterOptionResponse create(String key, MasterOptionRequest request) {
        MasterEntity option = newEntity(key);
        apply(key, option, request);
        return response(save(repository(key), option));
    }

    public MasterOptionResponse update(String key, long id, MasterOptionRequest request) {
        JpaRepository<?, Long> repository = repository(key);
        MasterEntity option = find(repository, id);
        apply(key, option, request);
        return response(save(repository, option));
    }

    public void delete(String key, long id) {
        JpaRepository<?, Long> repository = repository(key);
        find(repository, id);
        if (key.equals("tender-category") && productCategories.existsByTenderCategory_Id(id)) {
            throw new IllegalArgumentException("Tender Category is used by product categories and cannot be deleted");
        }
        repository.deleteById(id);
    }

    private MasterEntity newEntity(String key) {
        return switch (key) {
            case "tender-type" -> new MasterTenderType();
            case "number-of-covers" -> new MasterNumberOfCovers();
            case "tender-category" -> new MasterTenderCategory();
            case "work-item-product-category" -> new MasterWorkItemProductCategory();
            case "number-of-bid-openers" -> new MasterNumberOfBidOpeners();
            case "form-of-contract" -> new MasterFormOfContract();
            case "tenderer-class" -> new MasterTendererClass();
            default -> throw new EntityNotFoundException("Unknown master list: " + key);
        };
    }

    private JpaRepository<?, Long> repository(String key) {
        return switch (key) {
            case "tender-type" -> tenderTypes;
            case "number-of-covers" -> covers;
            case "tender-category" -> categories;
            case "work-item-product-category" -> productCategories;
            case "number-of-bid-openers" -> bidOpeners;
            case "form-of-contract" -> contracts;
            case "tenderer-class" -> tendererClasses;
            default -> throw new EntityNotFoundException("Unknown master list: " + key);
        };
    }

    private void apply(String key, MasterEntity entity, MasterOptionRequest request) {
        String name = request.name();
        if (name == null || name.isBlank()) {
            name = switch (key) {
                case "tender-type", "number-of-bid-openers", "tenderer-class" -> firstValue(request.description(), request.code());
                case "tender-category" -> firstValue(request.code(), request.description());
                default -> request.code();
            };
        }
        validateValue(name, "Name", 255, true);
        validateValue(request.code(), "Code", 255, false);
        validateValue(request.description(), "Description", 2000, false);
        entity.setName(name);
        entity.setCode(request.code());
        entity.setDescription(request.description());

        // Keep the old name-only API payload useful for masters that now also
        // carry a source code/description.
        if ((key.equals("tender-type") || key.equals("tender-category")) && entity.getCode() == null) {
            entity.setCode(name);
        }
        if (key.equals("tender-type") && entity.getDescription() == null) entity.setDescription(name);
        if (key.equals("number-of-bid-openers")) {
            if (!(entity instanceof com.cdac.egov.integration.gepnic.pretender.entities.MasterNumberOfBidOpeners bidOpeners))
                throw new IllegalStateException("Unexpected bid-opener master entity");
            bidOpeners.setBovalue(request.bovalue());
        }
        if (key.equals("work-item-product-category")) {
            if (!(entity instanceof MasterWorkItemProductCategory productCategory))
                throw new IllegalStateException("Unexpected product-category master entity");
            productCategory.setTenderCategory(request.tenderCategoryId() == null ? null : categories.findById(request.tenderCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Tender Category master value was not found")));
        }
    }

    private String firstValue(String preferred, String fallback) {
        return preferred == null || preferred.isBlank() ? fallback : preferred;
    }

    private void validateValue(String value, String label, int maxLength, boolean required) {
        if (required && (value == null || value.isBlank())) throw new IllegalArgumentException(label + " is required");
        if (value != null && value.length() > maxLength) throw new IllegalArgumentException(label + " cannot exceed " + maxLength + " characters");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MasterEntity find(JpaRepository repository, long id) {
        Object found = repository.findById(id).orElse(null);
        if (found == null) throw new EntityNotFoundException("Master value " + id + " was not found");
        return (MasterEntity) found;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private MasterEntity save(JpaRepository repository, MasterEntity entity) {
        return (MasterEntity) repository.save(entity);
    }

    private MasterOptionResponse response(MasterEntity entity) {
        Long tenderCategoryId = null;
        String tenderCategoryName = null;
        if (entity instanceof MasterWorkItemProductCategory productCategory && productCategory.getTenderCategory() != null) {
            tenderCategoryId = productCategory.getTenderCategory().getId();
            tenderCategoryName = productCategory.getTenderCategory().getName();
        }
        Integer bovalue = entity instanceof com.cdac.egov.integration.gepnic.pretender.entities.MasterNumberOfBidOpeners bidOpeners
            ? bidOpeners.getBovalue() : null;
        return new MasterOptionResponse(entity.getId(), entity.getName(), entity.getCode(), entity.getDescription(),
            bovalue, tenderCategoryId, tenderCategoryName, entity.getSourceRowKey());
    }
}

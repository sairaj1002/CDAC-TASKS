package in.gov.wamis.master.service;

import in.gov.wamis.common.exception.BusinessValidationException;
import in.gov.wamis.master.entity.*;
import in.gov.wamis.master.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

/**
 * Resolves the display values sent by the frontend (e.g. "Open Tender") to the
 * wamis_master rows they refer to, so pre-tenders store master ids as foreign keys.
 * Blank values resolve to null; unknown values are rejected.
 */
@Service
@Transactional(readOnly = true)
public class MasterDataService {

    private final MasterTenderTypeRepository tenderTypeRepository;
    private final MasterFormOfContractRepository formOfContractRepository;
    private final MasterTenderCategoryRepository tenderCategoryRepository;
    private final MasterProductCategoryRepository productCategoryRepository;
    private final MasterBidOpenersTypeRepository bidOpenersTypeRepository;
    private final MasterTendererClassRepository tendererClassRepository;

    public MasterDataService(MasterTenderTypeRepository tenderTypeRepository,
                             MasterFormOfContractRepository formOfContractRepository,
                             MasterTenderCategoryRepository tenderCategoryRepository,
                             MasterProductCategoryRepository productCategoryRepository,
                             MasterBidOpenersTypeRepository bidOpenersTypeRepository,
                             MasterTendererClassRepository tendererClassRepository) {
        this.tenderTypeRepository = tenderTypeRepository;
        this.formOfContractRepository = formOfContractRepository;
        this.tenderCategoryRepository = tenderCategoryRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.bidOpenersTypeRepository = bidOpenersTypeRepository;
        this.tendererClassRepository = tendererClassRepository;
    }

    public MasterTenderType tenderType(String value) {
        return resolve(value, "tender type", tenderTypeRepository::findByTenderType);
    }

    public MasterFormOfContract formOfContract(String value) {
        return resolve(value, "form of contract", formOfContractRepository::findByFormOfContract);
    }

    public MasterTenderCategory tenderCategory(String value) {
        return resolve(value, "tender category", tenderCategoryRepository::findByCategory);
    }

    /** A product category is only valid within the tender category it belongs to. */
    public MasterProductCategory productCategory(String value, MasterTenderCategory tenderCategory) {
        if (value != null && !value.isBlank() && tenderCategory == null) {
            throw new BusinessValidationException("Product category requires a tender category");
        }
        return resolve(value, "product category for tender category '"
                        + (tenderCategory != null ? tenderCategory.getCategory() : "") + "'",
                name -> productCategoryRepository.findByNameAndTenderCategory_Id(name, tenderCategory.getId()));
    }

    public MasterBidOpenersType bidOpeners(String value) {
        return resolve(value, "bid openers", bidOpenersTypeRepository::findByBoType);
    }

    public MasterTendererClass tendererClass(String value) {
        return resolve(value, "tenderer class", tendererClassRepository::findByTenderClass);
    }

    private static <T> T resolve(String value, String label, Function<String, Optional<T>> finder) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        return finder.apply(trimmed)
                .orElseThrow(() -> new BusinessValidationException("Unknown " + label + ": " + trimmed));
    }
}

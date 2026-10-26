package com.cdac.scope.service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cdac.scope.constant.DropdownConstant;
import com.cdac.scope.entities.DropdownOptionEntity;
import com.cdac.scope.forms.CreateDropdownOptionRequest;
import com.cdac.scope.forms.DropdownOptionDto;
import com.cdac.scope.forms.UpdateDropdownOptionRequest;
import com.cdac.scope.repository.BridgeRepository;
import com.cdac.scope.repository.DropdownOptionRepository;
import com.cdac.scope.repository.RoadRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class DropdownOptionService {

    private final DropdownOptionRepository repository;
    private final RoadRepository roadRepository;
    private final BridgeRepository bridgeRepository;
    private final Map<String, String> labelCache = new ConcurrentHashMap<>();

    public DropdownOptionService(
            DropdownOptionRepository repository,
            RoadRepository roadRepository,
            BridgeRepository bridgeRepository) {
        this.repository = repository;
        this.roadRepository = roadRepository;
        this.bridgeRepository = bridgeRepository;
    }

    /**
     * Canonicalize category names to prevent fragmentation.
     */
    public static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "";
        }
        String clean = category.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if ("PROPOSED_ROAD_TYPE".equals(clean) || "PROPOSED_ROAD_TYPES".equals(clean) || "ROAD_TYPES".equals(clean)) {
            return DropdownConstant.Categories.ROAD_TYPE;
        }
        if ("NATURE_OF_WORKS".equals(clean)) {
            return DropdownConstant.Categories.NATURE_OF_WORK;
        }
        if ("WORK_PLAN_TYPES".equals(clean)) {
            return DropdownConstant.Categories.WORK_PLAN_TYPE;
        }
        if ("CD_WORK_TYPES".equals(clean)) {
            return DropdownConstant.Categories.CD_WORK_TYPE;
        }
        if ("REPAIR_MAINTENANCE_TYPES".equals(clean)) {
            return DropdownConstant.Categories.REPAIR_MAINTENANCE_TYPE;
        }
        if ("BRIDGE_ACTIVITIES".equals(clean) || "ACTIVITIES".equals(clean) || "ACTIVITY".equals(clean)) {
            return DropdownConstant.Categories.BRIDGE_ACTIVITY;
        }
        return clean;
    }

    // ------------------------------------------------------------------------
    // Read operations
    // ------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<DropdownOptionDto> findActiveByCategory(String category) {
        String canonical = normalizeCategory(category);
        return repository.findByCategoryAndActiveTrueOrderByDisplayOrderAscIdAsc(canonical).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDto> findAllByCategory(String category) {
        String canonical = normalizeCategory(category);
        return repository.findByCategoryOrderByDisplayOrderAscIdAsc(canonical).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DropdownOptionDto> findAll(String categoryFilter) {
        if (categoryFilter != null && !categoryFilter.isBlank()) {
            return findAllByCategory(categoryFilter);
        }
        return repository.findAllByOrderByCategoryAscDisplayOrderAscIdAsc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return repository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public DropdownOptionDto findById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Dropdown option with ID " + id + " not found."));
    }

    // ------------------------------------------------------------------------
    // Write / Admin operations
    // ------------------------------------------------------------------------

    public DropdownOptionDto createOption(CreateDropdownOptionRequest request) {
        String canonicalCategory = normalizeCategory(request.category());
        String code = request.code().trim().toUpperCase(Locale.ROOT).replace(' ', '_');

        if (repository.existsByCategoryAndCode(canonicalCategory, code)) {
            throw new IllegalArgumentException(
                    "Dropdown option with category '" + canonicalCategory + "' and code '" + code + "' already exists.");
        }

        DropdownOptionEntity entity = new DropdownOptionEntity();
        entity.setCategory(canonicalCategory);
        entity.setCode(code);
        entity.setLabel(request.label().trim());
        entity.setDisplayOrder(request.displayOrder() != null ? request.displayOrder() : 1);
        entity.setActive(request.active() == null || request.active());
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        DropdownOptionEntity saved = repository.save(entity);
        clearCache();
        return toDto(saved);
    }

    public DropdownOptionDto updateOption(Long id, UpdateDropdownOptionRequest request) {
        DropdownOptionEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dropdown option with ID " + id + " not found."));

        entity.setLabel(request.label().trim());
        entity.setDisplayOrder(request.displayOrder());
        entity.setActive(request.active());
        entity.setUpdatedAt(Instant.now());

        DropdownOptionEntity updated = repository.save(entity);
        clearCache();
        return toDto(updated);
    }

    public DropdownOptionDto toggleActive(Long id, boolean active) {
        DropdownOptionEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dropdown option with ID " + id + " not found."));

        entity.setActive(active);
        entity.setUpdatedAt(Instant.now());
        DropdownOptionEntity updated = repository.save(entity);
        clearCache();
        return toDto(updated);
    }

    public void deleteOption(Long id) {
        DropdownOptionEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dropdown option with ID " + id + " not found."));

        String code = entity.getCode();

        // Check if referenced by existing Road or Bridge components
        boolean inUseInRoad = roadRepository.isOptionCodeInUse(code);
        boolean inUseInBridge = bridgeRepository.isOptionCodeInUse(code);

        if (inUseInRoad || inUseInBridge) {
            throw new IllegalStateException(
                    "Cannot delete option '" + entity.getLabel() + "' (" + code + ") because it is referenced by existing "
                    + (inUseInRoad && inUseInBridge ? "Road and Bridge" : inUseInRoad ? "Road" : "Bridge")
                    + " records. Please deactivate it instead.");
        }

        repository.delete(entity);
        clearCache();
    }

    // ------------------------------------------------------------------------
    // Validation & Label helpers
    // ------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public void validateActiveCode(String category, String code) {
        if (code == null || code.isBlank()) {
            return;
        }
        String canonical = normalizeCategory(category);
        Optional<DropdownOptionEntity> option = repository.findByCategoryAndCode(canonical, code.trim());

        if (option.isEmpty()) {
            List<DropdownOptionEntity> all = repository.findByCategoryAndActiveTrueOrderByDisplayOrderAscIdAsc(canonical);
            for (DropdownOptionEntity opt : all) {
                if (opt.getLabel().equalsIgnoreCase(code.trim()) || opt.getCode().equalsIgnoreCase(code.trim())) {
                    return;
                }
            }
            throw new IllegalArgumentException(
                    "Invalid or inactive option code '" + code + "' for category '" + category + "'.");
        }

        if (!Boolean.TRUE.equals(option.get().getActive())) {
            throw new IllegalArgumentException(
                    "Option '" + code + "' in category '" + category + "' is deactivated and cannot be selected.");
        }
    }

    @Transactional(readOnly = true)
    public String getLabel(String category, String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String canonical = normalizeCategory(category);
        String cacheKey = canonical + ":" + code.trim();
        String cached = labelCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Optional<DropdownOptionEntity> option = repository.findByCategoryAndCode(canonical, code.trim());
        if (option.isPresent()) {
            String label = option.get().getLabel();
            labelCache.put(cacheKey, label);
            return label;
        }

        List<DropdownOptionEntity> all = repository.findByCategoryOrderByDisplayOrderAscIdAsc(canonical);
        for (DropdownOptionEntity opt : all) {
            if (opt.getLabel().equalsIgnoreCase(code.trim()) || opt.getCode().equalsIgnoreCase(code.trim())) {
                labelCache.put(cacheKey, opt.getLabel());
                return opt.getLabel();
            }
        }

        return code;
    }

    public void clearCache() {
        labelCache.clear();
    }

    private DropdownOptionDto toDto(DropdownOptionEntity entity) {
        return new DropdownOptionDto(
                entity.getId(),
                entity.getCategory(),
                entity.getCode(),
                entity.getLabel(),
                entity.getActive(),
                entity.getDisplayOrder());
    }
}

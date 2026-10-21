package com.company.enterprise.features.wms.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import com.company.enterprise.features.wms.dto.response.WorkResponse;
import com.company.enterprise.features.wms.entity.WorkEntity;

class WorkMapperTest {

    private final WorkMapper mapper = new WorkMapper(strictModelMapper());

    @Test
    void mapsWorkEntityFieldsToResponse() {
        WorkEntity entity = new WorkEntity();
        entity.setWorkRegNo(12L);
        entity.setWorkDetails("Repair the main entrance");
        entity.setActualStartDate(LocalDate.of(2026, 10, 1));
        entity.setContractorName("Example Contractor");
        entity.setStipulatedStartDate(LocalDate.of(2026, 10, 1));
        entity.setStipulatedEndDate(LocalDate.of(2026, 11, 1));
        entity.setCreatedAt(LocalDateTime.of(2026, 9, 20, 10, 15));
        entity.setUpdatedAt(LocalDateTime.of(2026, 9, 21, 11, 30));

        WorkResponse response = mapper.toResponse(entity);

        assertEquals(entity.getWorkRegNo(), response.getWorkRegNo());
        assertEquals(entity.getWorkDetails(), response.getWorkDetails());
        assertEquals(entity.getActualStartDate(), response.getActualStartDate());
        assertEquals(entity.getContractorName(), response.getContractorName());
        assertEquals(entity.getCreatedAt(), response.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), response.getUpdatedAt());
    }

    private static ModelMapper strictModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }
}

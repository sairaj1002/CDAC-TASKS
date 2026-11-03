package com.cdac.egov.wms.work.subwork.unit.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cdac.egov.wms.work.subwork.unit.constants.UnitConstants;
import com.cdac.egov.wms.work.subwork.unit.entity.Unit;
import com.cdac.egov.wms.work.subwork.unit.repository.UnitRepository;
import com.cdac.egov.wms.common.constants.CommonConstants;

@RestController
@RequestMapping(UnitConstants.BASE_PATH)
@CrossOrigin(origins = CommonConstants.LOCAL_FRONTEND_ORIGIN)
public class UnitController {

    private final UnitRepository repository;

    public UnitController(UnitRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Unit> getUnits() {
        return repository.findByActiveTrueOrderByShortNameAsc();
    }
}

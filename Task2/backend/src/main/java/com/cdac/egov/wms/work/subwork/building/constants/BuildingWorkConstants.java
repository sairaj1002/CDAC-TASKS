package com.cdac.egov.wms.work.subwork.building.constants;

/** Constants used by the Building component API and validation. */
public final class BuildingWorkConstants {
    private BuildingWorkConstants() {}

    public static final class Api {
        private Api() {}
        public static final String BASE_PATH = "/api/buildings";
        public static final String PATH_ID = "/{id}";
        public static final String PATH_AUDIT = "/{id}/audit";
        public static final String PATH_OPTIONS = "/options";
        public static final String PARAM_CATEGORY = "category";
        public static final String PAGE_DEFAULT = "0";
        public static final String SIZE_DEFAULT = "5";
    }

    public static final class Validation {
        private Validation() {}
        public static final int DESCRIPTION_MAX_LENGTH = 1000;
        public static final String AREA_MIN = "0.0";
        public static final long COUNT_MIN = 0L;
    }

    public static final class Categories {
        private Categories() {}
        public static final String MAINTENANCE_TYPE = "MAINTENANCE_TYPE";
        public static final String BUILDING_ACTIVITY = "BUILDING_ACTIVITY";
    }
}

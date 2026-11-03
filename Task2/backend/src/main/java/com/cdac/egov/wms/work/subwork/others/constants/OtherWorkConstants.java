package com.cdac.egov.wms.work.subwork.others.constants;

/** Constants used by the Others component API and validation. */
public final class OtherWorkConstants {
    private OtherWorkConstants() {}

    public static final class Api {
        private Api() {}
        public static final String BASE_PATH = "/api/others";
        public static final String PATH_ID = "/{id}";
        public static final String PATH_AUDIT = "/{id}/audit";
        public static final String PAGE_DEFAULT = "0";
        public static final String SIZE_DEFAULT = "5";
    }

    public static final class Validation {
        private Validation() {}
        public static final int DESCRIPTION_MAX_LENGTH = 1000;
        public static final String QUANTITY_MIN = "0.0";
    }

    public static final class Messages {
        private Messages() {}
        public static final String DESCRIPTION_REQUIRED = "Description is required.";
        public static final String UNIT_REQUIRED = "Unit is required.";
        public static final String QUANTITY_REQUIRED = "Quantity is required.";
    }
}

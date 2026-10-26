package com.cdac.scope.constant;

/**
 * Centralized constant definitions for Road components.
 * Holds message constants, validation patterns, categories, plan types, API paths, and configuration values.
 */
public final class RoadConstant {

    private RoadConstant() {
        // Prevent instantiation
    }

    // =========================================================================
    // VALIDATION PATTERNS & FIELD LIMITS
    // =========================================================================
    public static final class Validation {
        private Validation() {}

        public static final String NOT_BLANK_REGEX = "\\S+";
        public static final String DECIMAL_MIN_ZERO = "0.0";
        public static final boolean DECIMAL_MIN_INCLUSIVE = false;

        public static final int DIGITS_INTEGER = 10;
        public static final int DIGITS_FRACTION = 3;

        public static final int MAX_LENGTH_50 = 50;

        // Validation messages
        public static final String NATURE_OF_WORK_REQUIRED = "Nature of work is required.";
        public static final String NATURE_OF_WORK_MAX = "Nature of work must not exceed 50 characters.";

        public static final String PROPOSED_ROAD_TYPE_REQUIRED = "Type of proposed road is required.";
        public static final String PROPOSED_ROAD_TYPE_MAX = "Type of proposed road must not exceed 50 characters.";

        public static final String LENGTH_REQUIRED = "Road length is required.";
        public static final String LENGTH_MIN = "Road length must be greater than zero.";

        public static final String WIDTH_MIN = "Road width must be greater than zero.";

        public static final String WORK_PLAN_TYPE_REQUIRED = "Work plan type is required.";
        public static final String WORK_PLAN_TYPE_MAX = "Work plan type must not exceed 50 characters.";

        public static final String CD_WORK_TYPE_REQUIRED = "Type of CD works is required.";
        public static final String CD_WORK_TYPE_MAX = "Type of CD works must not exceed 50 characters.";

        public static final String REPAIR_MAINTENANCE_TYPE_MAX = "Repair/maintenance type must not exceed 50 characters.";

        public static final String OFFICE_ID_POSITIVE = "Office ID must be positive.";
        public static final String WORK_ID_POSITIVE = "Work ID must be positive.";
        public static final String TENANT_ID_POSITIVE = "Tenant ID must be positive.";

        public static final String REPAIR_MAINTENANCE_TYPE_CONDITIONAL =
                "Repair/Maintenance Type is required for Non-Plan work and must be empty for Plan work.";

        public static final String ENTITY_REPAIR_MAINTENANCE_VALIDATION =
                "Repair/Maintenance Type must be selected for Non-Plan work and must be empty for Plan work.";
    }

    // =========================================================================
    // API & SERVICE MESSAGES
    // =========================================================================
    public static final class Messages {
        private Messages() {}

        public static final String LOADED = "Road components loaded.";
        public static final String CREATED = "Road component created.";
        public static final String UPDATED = "Road component updated.";
        public static final String DELETED = "Road component deleted.";
        public static final String AUDIT_HISTORY_LOADED = "Road audit history loaded.";

        public static final String NOT_FOUND_TEMPLATE = "Road component %d was not found.";
        public static final String ENVERS_SNAPSHOT_MISSING = "Envers returned an audit revision without its entity snapshot.";

        public static String notFound(long id) {
            return String.format(NOT_FOUND_TEMPLATE, id);
        }
    }

    // =========================================================================
    // API ENDPOINTS & ROUTES
    // =========================================================================
    public static final class Api {
        private Api() {}

        public static final String BASE_PATH = "/api/road-components";
        public static final String PATH_ID = "/{id}";
        public static final String PATH_AUDIT = "/{id}/audit";
    }

    // =========================================================================
    // DROPDOWN CATEGORIES
    // =========================================================================
    public static final class Category {
        private Category() {}

        public static final String NATURE_OF_WORK = "NATURE_OF_WORK";
        public static final String ROAD_TYPE = "ROAD_TYPE";
        public static final String WORK_PLAN_TYPE = "WORK_PLAN_TYPE";
        public static final String CD_WORK_TYPE = "CD_WORK_TYPE";
        public static final String REPAIR_MAINTENANCE_TYPE = "REPAIR_MAINTENANCE_TYPE";
    }

    // =========================================================================
    // WORK PLAN TYPE CODES & LABELS
    // =========================================================================
    public static final class WorkPlan {
        private WorkPlan() {}

        public static final String PLAN_CODE = "PLAN";
        public static final String PLAN_LABEL = "Plan";

        public static final String NON_PLAN_CODE = "NON_PLAN";
        public static final String NON_PLAN_LABEL = "Non-Plan";

        public static boolean isNonPlan(String workPlanType) {
            if (workPlanType == null) {
                return false;
            }
            String trimmed = workPlanType.trim();
            return NON_PLAN_CODE.equalsIgnoreCase(trimmed) || NON_PLAN_LABEL.equalsIgnoreCase(trimmed);
        }
    }

    // =========================================================================
    // AUDIT REVISION TYPES
    // =========================================================================
    public static final class Audit {
        private Audit() {}

        public static final String CREATED = "CREATED";
        public static final String UPDATED = "UPDATED";
        public static final String DELETED = "DELETED";
    }

    // Direct aliases for high-frequency constants
    public static final String BASE_API_PATH = Api.BASE_PATH;
    public static final String CATEGORY_NATURE_OF_WORK = Category.NATURE_OF_WORK;
    public static final String CATEGORY_ROAD_TYPE = Category.ROAD_TYPE;
    public static final String CATEGORY_WORK_PLAN_TYPE = Category.WORK_PLAN_TYPE;
    public static final String CATEGORY_CD_WORK_TYPE = Category.CD_WORK_TYPE;
    public static final String CATEGORY_REPAIR_MAINTENANCE_TYPE = Category.REPAIR_MAINTENANCE_TYPE;
    public static final String NON_PLAN_CODE = WorkPlan.NON_PLAN_CODE;
    public static final String NON_PLAN_LABEL = WorkPlan.NON_PLAN_LABEL;
    public static final String MSG_LOADED = Messages.LOADED;
    public static final String MSG_CREATED = Messages.CREATED;
    public static final String MSG_UPDATED = Messages.UPDATED;
    public static final String MSG_DELETED = Messages.DELETED;
    public static final String MSG_AUDIT_HISTORY_LOADED = Messages.AUDIT_HISTORY_LOADED;
    public static final String MSG_ENVERS_SNAPSHOT_MISSING = Messages.ENVERS_SNAPSHOT_MISSING;
}

package com.cdac.scope.constant;

/**
 * Centralized constant definitions for Bridge components and shared features.
 * Holds message constants, validation patterns, activity codes, API paths, and configuration values.
 */
public final class BridgeConstant {

    private BridgeConstant() {
        // Prevent instantiation
    }

    // =========================================================================
    // VALIDATION PATTERNS & FIELD LIMITS
    // =========================================================================
    public static final class Validation {
        private Validation() {}

        public static final String NOT_BLANK_REGEX = "\\S+";
        public static final String NUMERIC_ONLY_REGEX = "^\\d+$";
        public static final String DECIMAL_MIN_ZERO = "0.0";

        public static final int DIGITS_INTEGER = 10;
        public static final int DIGITS_FRACTION = 3;

        public static final int RIVER_NALLAH_BARRIER_MAX_LENGTH = 150;
        public static final int APPROVAL_ROAD_MAX_LENGTH = 150;
        public static final int BRIDGE_ACTIVITY_MAX_LENGTH = 50;
        public static final int OTHER_ACTIVITY_DESC_MAX_LENGTH = 500;
        public static final int NUMBER_OF_SPANS_MIN = 0;

        // Validation messages
        public static final String RIVER_NALLAH_BARRIER_REQUIRED = "River/Nallah/Barrier is required.";
        public static final String APPROVAL_ROAD_REQUIRED = "Approval road is required.";
        public static final String BRIDGE_ACTIVITY_REQUIRED = "Bridge activity is required.";
        public static final String OTHER_ACTIVITY_DESC_REQUIRED = "Other activity description is required for Other Activity.";
    }

    // =========================================================================
    // API & SERVICE MESSAGES
    // =========================================================================
    public static final class Messages {
        private Messages() {}

        public static final String LOADED = "Bridge components loaded.";
        public static final String CREATED = "Bridge component created.";
        public static final String UPDATED = "Bridge component updated.";
        public static final String DELETED = "Bridge component deleted.";
        public static final String AUDIT_HISTORY_LOADED = "Bridge audit history loaded.";

        public static final String NOT_FOUND_TEMPLATE = "Bridge component %d was not found.";
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

        public static final String BASE_PATH = "/api/bridge-components";
        public static final String PATH_ID = "/{id}";
        public static final String PATH_AUDIT = "/{id}/audit";
    }

    // =========================================================================
    // DROPDOWN CATEGORY & ACTIVITY CODES
    // =========================================================================
    public static final class Activity {
        private Activity() {}

        public static final String CATEGORY_BRIDGE_ACTIVITY = "BRIDGE_ACTIVITY";

        public static final String REPAIR_TO_BRIDGE_CODE = "REPAIR_TO_BRIDGE";
        public static final String REPAIR_TO_BRIDGE_LABEL = "Repair-to-Bridge";

        public static final String NEW_CONSTRUCTION_CODE = "NEW_CONSTRUCTION";
        public static final String NEW_CONSTRUCTION_LABEL = "New Construction";

        public static final String OTHER_ACTIVITY_CODE = "OTHER_ACTIVITY";
        public static final String OTHER_ACTIVITY_LABEL = "Other Activity";

        public static boolean isOtherActivity(String activity) {
            if (activity == null) {
                return false;
            }
            String trimmed = activity.trim();
            return OTHER_ACTIVITY_CODE.equalsIgnoreCase(trimmed) || OTHER_ACTIVITY_LABEL.equalsIgnoreCase(trimmed);
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
    public static final String CATEGORY_BRIDGE_ACTIVITY = Activity.CATEGORY_BRIDGE_ACTIVITY;
    public static final String OTHER_ACTIVITY_CODE = Activity.OTHER_ACTIVITY_CODE;
    public static final String OTHER_ACTIVITY_LABEL = Activity.OTHER_ACTIVITY_LABEL;
    public static final String MSG_LOADED = Messages.LOADED;
    public static final String MSG_CREATED = Messages.CREATED;
    public static final String MSG_UPDATED = Messages.UPDATED;
    public static final String MSG_DELETED = Messages.DELETED;
    public static final String MSG_AUDIT_HISTORY_LOADED = Messages.AUDIT_HISTORY_LOADED;
    public static final String MSG_ENVERS_SNAPSHOT_MISSING = Messages.ENVERS_SNAPSHOT_MISSING;
    public static final String MSG_BRIDGE_ACTIVITY_REQUIRED = Validation.BRIDGE_ACTIVITY_REQUIRED;
    public static final String MSG_OTHER_ACTIVITY_DESC_REQUIRED = Validation.OTHER_ACTIVITY_DESC_REQUIRED;
}

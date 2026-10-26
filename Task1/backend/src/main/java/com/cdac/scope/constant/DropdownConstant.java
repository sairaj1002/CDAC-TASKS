package com.cdac.scope.constant;

/**
 * Centralized constant definitions for Dropdown management and common API handling.
 */
public final class DropdownConstant {

    private DropdownConstant() {
        // Prevent instantiation
    }

    public static final class Api {
        private Api() {}

        public static final String BASE_PATH = "/api/dropdowns";
        public static final String PATH_CATEGORY = "/{category}";
        public static final String PATH_CATEGORIES = "/categories";
        public static final String PATH_ITEM_ID = "/item/{id}";
        public static final String PATH_ID = "/{id}";
        public static final String PATH_STATUS = "/{id}/status";
    }

    public static final class Messages {
        private Messages() {}

        public static final String OPTIONS_LOADED = "Dropdown options loaded.";
        public static final String ALL_OPTIONS_LOADED = "All dropdown options loaded.";
        public static final String CATEGORIES_LOADED = "Dropdown categories loaded.";
        public static final String OPTION_LOADED = "Dropdown option loaded.";
        public static final String CREATED = "Dropdown option created successfully.";
        public static final String UPDATED = "Dropdown option updated successfully.";
        public static final String ACTIVATED = "Dropdown option activated.";
        public static final String DEACTIVATED = "Dropdown option deactivated.";
        public static final String DELETED = "Dropdown option deleted successfully.";

        public static final String NOT_FOUND_TEMPLATE = "Dropdown option with ID %d not found.";
        public static final String ALREADY_EXISTS_TEMPLATE = "Dropdown option with category '%s' and code '%s' already exists.";
        public static final String INVALID_CODE_TEMPLATE = "Invalid or inactive option code '%s' for category '%s'.";
        public static final String DEACTIVATED_CODE_TEMPLATE = "Option '%s' in category '%s' is deactivated and cannot be selected.";

        public static final String VALIDATION_FAILED = "Request validation failed.";
        public static final String BODY_INVALID = "Request body is invalid or contains an unsupported value.";
        public static final String UNEXPECTED_ERROR = "An unexpected error occurred.";
    }

    public static final class Categories {
        private Categories() {}

        public static final String ROAD_TYPE = "ROAD_TYPE";
        public static final String NATURE_OF_WORK = "NATURE_OF_WORK";
        public static final String WORK_PLAN_TYPE = "WORK_PLAN_TYPE";
        public static final String CD_WORK_TYPE = "CD_WORK_TYPE";
        public static final String REPAIR_MAINTENANCE_TYPE = "REPAIR_MAINTENANCE_TYPE";
        public static final String BRIDGE_ACTIVITY = "BRIDGE_ACTIVITY";
    }
}

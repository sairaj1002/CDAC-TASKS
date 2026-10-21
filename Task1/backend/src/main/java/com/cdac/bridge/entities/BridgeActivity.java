package com.cdac.bridge.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BridgeActivity {
	REPAIR_TO_BRIDGE("Repair-to-Bridge"),
	NEW_CONSTRUCTION("New Construction"),
	OTHER_ACTIVITY("Other Activity");

	private final String value;

	BridgeActivity(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	@JsonCreator
	public static BridgeActivity fromValue(String value) {
		for (BridgeActivity activity : values()) {
			if (activity.value.equals(value)) {
				return activity;
			}
		}
		throw new IllegalArgumentException("Unsupported bridge activity: " + value);
	}
}

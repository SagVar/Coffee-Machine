package com.company.assignment.model;

import lombok.Getter;
import lombok.NonNull;

@Getter
public enum Availability {
	NOT_AVAILABLE("not available"),
	NOT_SUFFICIENT("not sufficient"),
	SUFFICIENT("sufficient");

	@NonNull
	private final String desc;
	
	Availability(String desc) {
		this.desc = desc;
	}
}
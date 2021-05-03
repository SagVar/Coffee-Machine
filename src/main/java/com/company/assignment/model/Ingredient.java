package com.company.assignment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Ingredient {

	@NonNull
	private final String name;
}

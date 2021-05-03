package com.company.assignment.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class Beverage {
	
	@NonNull
	private final String name;
	
	@NonNull
	private final Recipe recipe;
	
	/**
	 * @param name
	 * @param quantityMap
	 * method to create an beverage instance with given name and recipe
	 * @return beverage object
	 */
	public static Beverage toBeverageBean(final String name, final Map<String, Integer> quantityMap) {
		final Recipe recipe = Recipe.toRecipeBean(quantityMap);
		return new Beverage(name, recipe);
	}	
}
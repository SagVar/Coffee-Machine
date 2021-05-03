package com.company.assignment;

import com.company.assignment.model.Availability;
import com.company.assignment.model.Ingredient;
import java.util.HashMap;
import java.util.Map;

public class Inventory {

	private final Map<Ingredient, Integer> inventoryMap = new HashMap<>();
	
	/**
	 * method to add a new ingredient
	 * @param ingredient
	 * @param quantity
	 */
	public void addNewIngredient(final Ingredient ingredient, final Integer quantity) {
		inventoryMap.put(ingredient, quantity);
	}
	
	/**
	 * method to increase an ingredient's quantity
	 * @param ingredient
	 * @param quantity
	 */
	public void refillIngredient(final Ingredient ingredient, final Integer quantity) {
		Integer oldVal = 0;
		if(inventoryMap.containsKey(ingredient)) {
			oldVal = inventoryMap.get(ingredient);
		}
		inventoryMap.put(ingredient, oldVal + quantity);
	}
	
	/**
	 * method to check if ingredient is sufficiently available and if yes, then decrease given quantity
	 * @param ingredient
	 * @param quantity
	 * @return true/false
	 */
	public boolean fetch(final Ingredient ingredient, final Integer quantity) {
		if(Availability.SUFFICIENT.equals(isAvailable(ingredient, quantity))) {
			final Integer quantityAvailable = inventoryMap.get(ingredient);
			inventoryMap.put(ingredient, quantityAvailable - quantity);
			return true;
		}
		
		return false;
	}
	
	/**
	 * method to check an ingredient's availability for given quantity
	 * @param ingredient
	 * @param quantity
	 * @return NOT_AVAILABLE/NOT_SUFFICIENT/SUFFICIENT
	 */
	public Availability isAvailable(final Ingredient ingredient, final Integer quantity) {
		if(!inventoryMap.containsKey(ingredient)) {
			return Availability.NOT_AVAILABLE;
		}
		
		final Integer quantityAvailable = inventoryMap.get(ingredient);
		if(quantityAvailable >= quantity) {
			return Availability.SUFFICIENT;
		}
		if(quantityAvailable > 0) {
			return Availability.NOT_SUFFICIENT;
		}
		return Availability.NOT_AVAILABLE;
	}
	
	/**
	 * method to purge all ingredients from inventory
	 */
	public void clean() {
		inventoryMap.clear();
	}
}
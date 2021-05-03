package com.company.assignment;

import com.company.assignment.model.Availability;
import com.company.assignment.model.Beverage;
import com.company.assignment.model.Ingredient;
import com.company.assignment.model.Recipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.company.assignment.config.Machine;
import com.company.assignment.config.MachineConfig;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class CoffeeMachine {
	
	private final int outlets;
	private final List<Ingredient> ingredients;
	private final List<Beverage> beverages;
	@Getter(AccessLevel.NONE)
	private final Inventory inventory;
	@Getter(AccessLevel.NONE)
	private final Map<Ingredient, Integer> thresholdMap;

	/**
	 * method to create coffee machine instance with given configuration
	 * @param config - configuration data to initialize coffee machine
	 * @return CoffeeMachine
	 */
	public static CoffeeMachine toCoffeeMachineBean(final MachineConfig config) {
		final Machine machine = config.getMachine();
		int outlets = machine.getOutlets().getCount();

		final Inventory inventory = new Inventory();
		final List<Ingredient> ingredients = new ArrayList<>();
		final Map<String, Integer> totalItemsQuantity = machine.getTotalItemsQuantity();

		totalItemsQuantity.keySet().forEach(key -> {
			inventory.addNewIngredient(new Ingredient(key), totalItemsQuantity.get(key));
			ingredients.add(new Ingredient(key));
		});

		final List<Beverage> bList = new ArrayList<>();
		final Map<Ingredient, Integer> thresholdMap = new HashMap<>();
		final Map<String, Map<String, Integer>> beverageMap = machine.getBeverages();

		//threshold value for an low running ingredient has been kept such that each drink can be processed once
		for(final String key: beverageMap.keySet()) {
			final Beverage beverage = Beverage.toBeverageBean(key, beverageMap.get(key));
			bList.add(beverage);
			final Recipe recipe = beverage.getRecipe();
			for(final Ingredient ingredient: recipe.getIngredients()) {
				Integer oldVal = 0;
				if(thresholdMap.containsKey(ingredient)) {
					oldVal = thresholdMap.get(ingredient);
				}
				thresholdMap.put(ingredient, oldVal + recipe.getQuantity(ingredient));
			}
		}
		return new CoffeeMachine(outlets, ingredients, bList, inventory, thresholdMap);
	}

	/**
	 * method to check if any ingredient of beverage is out of stock
	 * @param beverage
	 * @return true/false
	 */
	public boolean isBeverageOutOfStock(final Beverage beverage) {
		final Recipe recipe = beverage.getRecipe();
		final Optional<IngredientAvailability> insufficientIngredient = getInsufficientIngredientForRecipe(recipe);
		return insufficientIngredient.isPresent();
	}
	
	/**
	 * method to make beverage
	 * 1. check if all ingredients of beverage are present in required quantity
	 * 2. fetch all ingredients from inventory
	 * 2. prepare beverage - a simulation to represent real life processing time 
	 * @param beverageName
	 * @return status message
	 */
	public String makeBeverage(final String beverageName) {
		final Optional<Beverage> beverageOpt = getBeverageFromName(beverageName);
		if(beverageOpt.isEmpty()) {
			return beverageName + " is not available";
		}
		final Beverage beverage = beverageOpt.get();
		synchronized (inventory) {
			final Recipe recipe = beverage.getRecipe();
			final Optional<IngredientAvailability> insufficientIngredient = getInsufficientIngredientForRecipe(recipe);
			if (insufficientIngredient.isPresent()) {
				final IngredientAvailability ingredientAvailability = insufficientIngredient.get();
				return String.format("%s can not be prepared because %s is %s", beverage.getName(),
						ingredientAvailability.getIngredient().getName(), ingredientAvailability.getAvailability().getDesc());
			}
			fetchIngredientsForRecipe(recipe);
		}
		prepareBeverage();
		return String.format("Your %s is prepared!", beverage.getName());
	}

	/**
	 * @return list of ingredients which are running based on threshold
	 */
	public List<Ingredient> getLowRunningIngredients()  {
		return ingredients.stream()
				.filter(i -> !Availability.SUFFICIENT.equals(inventory.isAvailable(i, thresholdMap.get(i))))
				.collect(Collectors.toList());
	}
	
	/**
	 * method to refill ingredient in inventory
	 * @param ingredient - ingredient to be refilled
	 * @param quantity
	 */
	public void refillIngredient(final Ingredient ingredient, final Integer quantity) {
		processRefill();
		synchronized (inventory) {
			inventory.refillIngredient(ingredient, quantity);
		}
	}
	
	/**
	 * method to remove all ingredients from inventory
	 */
	public void purgeIngredients() {
		synchronized (inventory) {
			inventory.clean();
		}
	}
	
	/**
	 * method to add a new ingredient to coffee machine inventory
	 * @param ingredient
	 * @param quantity
	 */
	public void addNewIngredient(Ingredient ingredient, Integer quantity) {
		synchronized (inventory) {
			inventory.addNewIngredient(ingredient, quantity);;
		}
	}

	private Optional<Beverage> getBeverageFromName(final String beverageName) {
		return beverages.stream()
				.filter(b -> b.getName().equals(beverageName)).findFirst();
	}

	private Optional<IngredientAvailability> getInsufficientIngredientForRecipe(final Recipe recipe) {
		final Set<Ingredient> ingredients = recipe.getIngredients();
		for(final Ingredient ingredient: ingredients) {
			final Integer requiredQuantity = recipe.getQuantity(ingredient);
			final Availability availability = inventory.isAvailable(ingredient, requiredQuantity);
			if(!Availability.SUFFICIENT.equals(availability)) {
				return Optional.of(new IngredientAvailability(ingredient, availability));
			}
		}
		return Optional.empty();
	}

	private void fetchIngredientsForRecipe(final Recipe recipe) {
		for(final Ingredient ingredient: recipe.getIngredients()) {
			inventory.fetch(ingredient, recipe.getQuantity(ingredient));
		}
	}

	private void prepareBeverage() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void processRefill() {
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Getter
	@AllArgsConstructor
	private static class IngredientAvailability {

		@NonNull
		private final Ingredient ingredient;

		@NonNull
		private final Availability availability;
	}
}

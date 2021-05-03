package com.company.assignment;

import com.company.assignment.model.Beverage;
import com.company.assignment.model.Ingredient;
import com.company.assignment.util.AsyncUtil;
import com.company.assignment.util.AsyncUtil.Task;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CoffeeMachineUI {
	
	private final CoffeeMachine coffeeMachine;
	private final List<Ingredient> ingredients;
	private final List<Beverage> beverages;
	
	public CoffeeMachineUI(final CoffeeMachine coffeeMachine) {
		this.coffeeMachine = coffeeMachine;
		
		this.ingredients = coffeeMachine.getIngredients().stream()
				.sorted(Comparator.comparing(Ingredient::getName))
	            .collect(Collectors.toList());
		
		this.beverages = coffeeMachine.getBeverages().stream()
				.sorted(Comparator.comparing(Beverage::getName))
	            .collect(Collectors.toList());
	}
	
	/**
	 * method to show available beverages at any given point
	 */
	public void showMenu() {
		System.out.println("Available beverages : ");
		for (Beverage beverage: beverages) {
            int i = 1;
            if (!coffeeMachine.isBeverageOutOfStock(beverage)) {
                System.out.printf("%s. %s%n", i, beverage.getName());
                i++;
            }
        }
        System.out.println();
	}
	
	/**
	 * method to show ingredients list (might not be available though) 
	 */
	public void showIngredients() {
		System.out.println("Ingredients : ");
		for(int i=0; i<ingredients.size(); i++) {
            System.out.printf("%s. %s%n", i+1, ingredients.get(i).getName());
		}
		System.out.println();
	}
	
	/**
	 * @param order - beverage name
	 * method to process beverage making in parallel as no of outlets
	 */
	public void process(String order) {		
		AsyncUtil.execute(new Task() {
			@Override
			public void run() {
				System.out.println("Dispensing " + order);
				String msg = coffeeMachine.makeBeverage(order);
				System.out.println(msg);
			}
		});
	}

	/**
	 * method to show ingredients which are running low beyond a threshold limit
	 */
	public void showIndicator() {
		List<Ingredient> outOfStockIngredients = coffeeMachine.getLowRunningIngredients();
		System.out.println("Following ingredients are running low :");
		for(int i=0; i<outOfStockIngredients.size(); i++) {
            System.out.printf("%s. %s%n", i+1, outOfStockIngredients.get(i).getName());
		}
		System.out.println();
	}
	
	/**
	 * @param ingredientString - ingredient to be refilled
	 * @param quantity
	 * method to refill the ingredient by given quantity 
	 */
	public void refill(String ingredientString, Integer quantity) {
		Optional<Ingredient> ingredientOpt = ingredients.stream()
				.filter(i -> i.getName().equals(ingredientString))
				.findFirst();
		if (ingredientOpt.isEmpty()) {
			System.out.println(ingredientString + " is not supported");
		}
		coffeeMachine.refillIngredient(ingredientOpt.get(), quantity);
	}
}

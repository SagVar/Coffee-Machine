package com.company.assignment.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class Recipe {

    @NonNull
    private final Map<Ingredient, Integer> ingredientsQuantityMap;

    public static Recipe toRecipeBean(final Map<String, Integer> quantityMap) {
        final Map<Ingredient, Integer> recipeMap = new LinkedHashMap<>();
        quantityMap.keySet().forEach(key -> recipeMap.put(new Ingredient(key), quantityMap.get(key)));
        return new Recipe(recipeMap);
    }

    public Set<Ingredient> getIngredients() {
        return this.ingredientsQuantityMap.keySet();
    }

    public Integer getQuantity(final Ingredient ingredient) {
        return ingredientsQuantityMap.get(ingredient);
    }
}

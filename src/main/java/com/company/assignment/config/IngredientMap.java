package com.company.assignment.config;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class IngredientMap {
	
	private final Map<String, Integer> map = new HashMap<String, Integer>();
	
	public IngredientMap() {
		super();
	}
}

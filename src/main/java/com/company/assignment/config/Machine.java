package com.company.assignment.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Machine {
	
	private final Outlet outlets;
	@JsonProperty("total_items_quantity")
	private final Map<String, Integer> totalItemsQuantity = new HashMap<String, Integer>();
	private final Map<String, Map<String, Integer>> beverages = new HashMap<String, Map<String, Integer>>();
	
	public Machine() {
		super();
		this.outlets = new Outlet();
	}
}

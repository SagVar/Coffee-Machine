package com.company.assignment.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Outlet {
	
	@JsonProperty("count_n")
	private Integer count;
}

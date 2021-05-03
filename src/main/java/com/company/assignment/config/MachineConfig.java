package com.company.assignment.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineConfig {
	
	private final Machine machine;
	
	public MachineConfig() {
		super();
		this.machine = null;
	}
}

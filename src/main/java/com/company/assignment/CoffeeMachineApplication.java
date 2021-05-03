package com.company.assignment;

import com.company.assignment.util.AsyncUtil;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.company.assignment.config.MachineConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class CoffeeMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeMachineApplication.class, args);
		init();
	}
	
	
	/**
	 * to initialize coffee machine with config.json 
	 */
	public static void init() {
		final ObjectMapper mapper = new ObjectMapper();
		final InputStream ipStream = CoffeeMachineApplication.class.getResourceAsStream("/config.json");
		final MachineConfig machineConfig;
		try {
			machineConfig = mapper.readValue(ipStream, MachineConfig.class);
		} catch (IOException e) {
			System.out.println("Some error occurred while doing machine setup");
			return;
		}
		
		CoffeeMachine coffeeMachine = CoffeeMachine.toCoffeeMachineBean(machineConfig);
		CoffeeMachineUI coffeeMachineUI = new CoffeeMachineUI(coffeeMachine);
		AsyncUtil.init(coffeeMachine.getOutlets());
		coffeeMachineUI.showIngredients();
		coffeeMachineUI.showMenu();
		//sample processing
		coffeeMachineUI.process("hot_tea");
		coffeeMachineUI.process("hot_coffee");
		coffeeMachineUI.process("black_tea");
		coffeeMachineUI.process("green_tea");
		
	}

}

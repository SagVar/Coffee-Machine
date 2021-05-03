package com.company.assignment;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.context.SpringBootTest;

import com.company.assignment.config.MachineConfig;
import com.company.assignment.model.Beverage;
import com.company.assignment.model.Ingredient;
import com.company.assignment.util.AsyncUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class CoffeeMachineTest {
	
	private CoffeeMachine coffeeMachine;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final List<String> BEVERAGES = List.of("hot_tea", "hot_coffee", "black_tea", "green_tea");
	
	@BeforeAll
	void init() throws IOException {
		MachineConfig machineConfig = readFile("/testconfig.json", new TypeReference<MachineConfig>(){});		
		coffeeMachine = CoffeeMachine.toCoffeeMachineBean(machineConfig);
		AsyncUtil.init(coffeeMachine.getOutlets());
	}

	@Test
	void testMakeBeverageAllPrepared() throws IOException {
		setIngredients("inventoryconfig1");
		
		final List<List<String>> expectedRespList = getExpectedResponseList("response1");
		final List<String> responseList = makeBeverage(BEVERAGES);
		
		verify(expectedRespList, responseList);
	}
	
	@Test
	void testMakeBeveragePartialPrepared() throws IOException {
		setIngredients("inventoryconfig2");
		
		final List<List<String>> expectedRespList = getExpectedResponseList("response2");
		final List<String> responseList = makeBeverage(BEVERAGES);
		
		verify(expectedRespList, responseList);
	}
	
	@Test
	void testMakeBeverageNonePrepared() throws IOException {
		setIngredients("inventoryconfig3");
		
		final List<List<String>> expectedRespList = getExpectedResponseList("response3");
		final List<String> responseList = makeBeverage(BEVERAGES);
		
		verify(expectedRespList, responseList);
	}
	
	@Test
	void testLowRunningIngredients() throws IOException {
		//inventory is set up for threshold limit
		setIngredients("inventoryconfig4");
		
		final String beverageName = "green_tea";
		makeBeverage(List.of(beverageName));
		final List<Ingredient> expectedIngredients = getBeverageIngredients(beverageName);
		final List<Ingredient> ingredients = coffeeMachine.getLowRunningIngredients();
		

		assertTrue(CollectionUtils.isEqualCollection(ingredients, expectedIngredients));
	}
	
	@Test
	void testRefillIngredient() throws IOException {
		setIngredients("inventoryconfig5");
		
		String beverageName = "green_tea";
		final List<String> expectedResp1 = List.of("green_tea can not be prepared because green_mixture is not available");
		final List<String> response1 = makeBeverage(List.of(beverageName));
		assertTrue(CollectionUtils.isEqualCollection(response1, expectedResp1));
		
		coffeeMachine.refillIngredient(new Ingredient("green_mixture"), 100);
		final List<String> expectedResp2 = List.of("Your green_tea is prepared!");
		final List<String> response2 = makeBeverage(List.of(beverageName));
		assertTrue(CollectionUtils.isEqualCollection(response2, expectedResp2));
	}
	
	private void setIngredients(final String fileName) throws IOException {
		coffeeMachine.purgeIngredients();
		final String directory = String.format("/inventoryconfig/%s.json", fileName);
		final Map<String, Integer> totalItemsQuantity = readFile(directory, new TypeReference<Map<String, Integer>>(){});
		totalItemsQuantity.forEach((k,v) -> coffeeMachine.addNewIngredient(new Ingredient(k), v));
	}
	
	private List<String> makeBeverage(final List<String> beverages) {
		final List<Future<String>> futureList = new ArrayList<Future<String>>();
		beverages.forEach(b -> futureList.add(makeBeverageCallToCoffeeMachine(b)));
		
		final List<String> responseList = new ArrayList<String>();
		futureList.stream().forEach(f -> {
			try {
				responseList.add(f.get());
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
		});
		
		return responseList;
	}
	
	private List<List<String>> getExpectedResponseList(final String fileName) throws IOException {
		final String directory = String.format("/response/%s.json", fileName);
		final List<List<String>> expectedResp = readFile(directory, new TypeReference<List<List<String>>>(){});		
		return expectedResp;
	}
	
	private void verify(final List<List<String>> expectedRespList, final List<String> responseList)  {
		final boolean match = expectedRespList.stream().anyMatch(l -> CollectionUtils.isEqualCollection(responseList, l));
		assertTrue(match, String.format("Expected that actual response %s should match one of %s", responseList, expectedRespList));
	}
	
	private Future<String> makeBeverageCallToCoffeeMachine(final String beverageName) {
		return AsyncUtil.submit(new AsyncUtil.AsyncWork<String>() {
			@Override
			public String call() throws Exception {
				String s = coffeeMachine.makeBeverage(beverageName);
				return s;
			}
		});
	}
	
	private <T> T readFile(final String directory, final TypeReference<T> typeReference) throws IOException {
		final InputStream ipStream = CoffeeMachineTest.class.getResourceAsStream(directory);
		return objectMapper.readValue(ipStream, typeReference);
	}
	
	private List<Ingredient> getBeverageIngredients(final String beverageName) {
		final Optional<Beverage> beverageOpt= coffeeMachine.getBeverages().stream()
				.filter(b -> b.getName().equals(beverageName)).findFirst();
		if(beverageOpt.isPresent()) {
			return new ArrayList<Ingredient>(beverageOpt.get().getRecipe().getIngredients());
		}
		return Collections.emptyList();
	}

}
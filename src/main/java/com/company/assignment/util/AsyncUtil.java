package com.company.assignment.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncUtil {
	
	private static ExecutorService executorService;
		
	public static void init(int noOfOutlets) {
		executorService = Executors.newFixedThreadPool(noOfOutlets);
	}
		
	public static void execute (Task task) {
		executorService.execute(task);
	}
	
	public static <R> Future<R> submit(AsyncWork<R> task) {
		return executorService.submit(task);
	}
	
	public static abstract class AsyncWork<R> implements Callable<R>{
	}
	
	public static abstract class Task implements Runnable{
	}
}

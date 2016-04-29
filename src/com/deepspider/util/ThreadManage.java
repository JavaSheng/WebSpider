package com.deepspider.util;

import java.util.concurrent.*;
public class ThreadManage {
	private ExecutorService executorService;
	
	public void createThreadPool(){
		executorService = Executors.newFixedThreadPool(100);
	}
	
	public void addTask(Runnable task){
		executorService.execute(task);
	}
	
	public void shutDown(){
		executorService.shutdown();
	}
	
}

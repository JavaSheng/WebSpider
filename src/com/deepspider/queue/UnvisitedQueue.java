package com.deepspider.queue;

import java.util.LinkedList;

public class UnvisitedQueue {
	private LinkedList<String> unvisitedQueue = new LinkedList<String>();
	
	//URL入队列
	public  void enQueue(String url){
		unvisitedQueue.addLast(url);
	}
	
	//URL出队列
	public  String deQueue(){
		return unvisitedQueue.removeFirst();
	}
	
	
	//判断队列是否为空
	public  boolean isUnvisitedQueueEmpty(){
		return unvisitedQueue.isEmpty();
	}
	
	//获取未访问URL数量
	public int getUnvisitedUrlNum(){
		return unvisitedQueue.size();
	}
	
}

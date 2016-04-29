package com.deepspider.queue;

import java.util.HashSet;
import java.util.Set;

public class VisitedQueue {
	private Set<String> visitedQueue = new HashSet<>();
	
	//将已爬url添加到set中
	public void addVisitedUrl(String url){
		visitedQueue.add(url);
	}
	
	//移除已访问url
	public void removeVisitedUrl(String url){
		visitedQueue.remove(url);
	}
	
	//是否包含在已访问URL中
	public boolean containUrl(String url){
		return visitedQueue.contains(url);
	}
	
	//获取已访问URL数量
	public int getVisitedUrlNum(){
		return visitedQueue.size();
	}
}

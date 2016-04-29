package com.deepspider.main;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import com.deepspider.pojo.HandledQueue;
import com.deepspider.queue.BloomFilter;
import com.deepspider.queue.UnvisitedQueue;
import com.deepspider.service.CheckUpdate;
import com.deepspider.service.ConfigListen;
import com.deepspider.service.HandleNode;
import com.deepspider.util.SqlConfig;
import com.deepspider.util.ThreadManage;
import com.ibatis.sqlmap.client.SqlMapClient;


public class SpiderStart {
	
	public static void main(String[] args){
		/**
		 * 线程池初始化
		 * @author JavaSheng
		 */
		ThreadManage threadManage = new ThreadManage();
		threadManage.createThreadPool();
		
		/**
		 * 获取数据库实例
		 * @author JavaSheng
		 */
		SqlMapClient sqlMap = SqlConfig.getSqlMapInstance();
		
		/**
		 * 网页内容定时更新线程初始化
		 * @author JavaSheng
		 */
		ResourceBundle rb = ResourceBundle.getBundle("path");
		long checkTime = Long.parseLong(rb.getString("checkTime"));//更新间隔时间
		CheckUpdate checkUpdate = new CheckUpdate(checkTime);
		threadManage.addTask(checkUpdate);
		
		/**
		 * 待爬，已爬队列初始化(以后改为redis)
		 * @author JavaSheng
		 */
		UnvisitedQueue unvisitedQueue = new UnvisitedQueue();
		BloomFilter bloomFilterQueue = new BloomFilter();
		
		/**
		 * 从数据库中获取已爬URL,并添加到已爬队列中
		 * @author JavaSheng
		 */
		try {
			
			@SuppressWarnings("unchecked")
			List<HandledQueue> handledQueue = sqlMap.queryForList("getUrlFilter");
			if(!handledQueue.isEmpty()){
				for(HandledQueue queue : handledQueue){
					bloomFilterQueue.addUrl(queue.getUrl());
				}
			}
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("Get VisitedUrl failed");
		}
		
		/**
		 * 配置监听线程初始化
		 * @author JavaSheng
		 */
		
		ConfigListen configListen = new ConfigListen(threadManage,unvisitedQueue);
		threadManage.addTask(configListen);
		
		/**
		 * 从数据库中获取目标url,并添加到待爬队列中
		 * @author JavaSheng
		 */
		try {
			@SuppressWarnings("unchecked")
			List<String> targetUrl = sqlMap.queryForList("getTargetUrl");
			if(!targetUrl.isEmpty()){
				for(String url : targetUrl){				
					//判断是否为已处理url
					if(!(bloomFilterQueue.containsUrl(url))){
						unvisitedQueue.enQueue(url);
					}
				

				}
				
				/**
				 * 交由爬取线程处理
				 * @author JavaSheng
				 */
				HandleNode handleNode = new HandleNode(threadManage,unvisitedQueue,bloomFilterQueue);
				threadManage.addTask(handleNode);
			}
								


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Get target url fail");
		}				
	}
}

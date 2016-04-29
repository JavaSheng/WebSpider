package com.deepspider.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.deepspider.pojo.WebNode;
import com.deepspider.util.SqlConfig;
import com.ibatis.sqlmap.client.SqlMapClient;

public class WebNodeDao{
	
	private SqlMapClient sqlMap = SqlConfig.getSqlMapInstance();
	private List<WebNode> insertQueue;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private int insertSize = 0;
		
	public WebNodeDao(List<WebNode> insertQueue){
		this.insertQueue = insertQueue;
	}

	/**
	 * 定时从消息队列中取出数据插入到数据库中
	 * @author JavaSheng
	 */
	Runnable runnable = new Runnable(){
		public void run(){
			synchronized(insertQueue){
				try{
					sqlMap.startTransaction();
					sqlMap.startBatch();
					
					Map<String,Object> map = new HashMap<String,Object>();
						for(WebNode insertNode : insertQueue){							
							map.put("url",insertNode.getRequestInfo().getUrl());
							map.put("ip",insertNode.getRequestInfo().getIp());
							map.put("host",insertNode.getRequestInfo().getHost());
							map.put("html",insertNode.getResponseInfo().getHtml());
							String content = insertNode.getResponseInfo().getContent();

							map.put("content",content);
							map.put("protocol",insertNode.getResponseInfo().getProtocol());
							map.put("server",insertNode.getResponseInfo().getServer());
							map.put("frame",insertNode.getResponseInfo().getFrame());
							map.put("recenttime",0);
							map.put("lasttime",insertNode.getLastTime());
							map.put("istargetnode",insertNode.isTargetNode());
							map.put("targeturl",insertNode.getTargetUrl());
							sqlMap.insert("insertSpiderData", map);
						}
						
			
						sqlMap.executeBatch();
						sqlMap.commitTransaction();
						sqlMap.endTransaction();
						int deleteSize = insertQueue.size();
						insertSize += deleteSize;
						insertQueue.clear();//清空队列
						System.out.println("already handled : " + insertSize);
				
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("insert fail");
				}
			}
			
		}
	};

	public void insertWebNode(){
		ResourceBundle rb = ResourceBundle.getBundle("path");
		int insertTime = Integer.parseInt(rb.getString("insertTime"));
		service.scheduleAtFixedRate(runnable, insertTime, insertTime, TimeUnit.SECONDS);
	}
}

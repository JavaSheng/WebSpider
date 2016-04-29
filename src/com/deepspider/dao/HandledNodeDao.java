package com.deepspider.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.deepspider.pojo.HandledQueue;
import com.deepspider.util.SqlConfig;
import com.ibatis.sqlmap.client.SqlMapClient;

public class HandledNodeDao {
	private SqlMapClient sqlMap = SqlConfig.getSqlMapInstance();
	private List<HandledQueue> handledQueue;
	private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	
	public HandledNodeDao(List<HandledQueue> handledQueue){
		this.handledQueue = handledQueue;
	}
	
	/**
	 * 定时从消息队列中取出数据插入到数据库中
	 * @author JavaSheng
	 */
	Runnable runnable = new Runnable(){
		public void run(){
			synchronized(handledQueue){
				try{
					sqlMap.startTransaction();
					sqlMap.startBatch();

						for(HandledQueue handledNode : handledQueue){							
							sqlMap.insert("insertHandledUrl", handledNode);
						}
						
			
						sqlMap.executeBatch();
						sqlMap.commitTransaction();
						sqlMap.endTransaction();
						handledQueue.clear();//清空队列			
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("insert fail");
				}
			}
			
		}
	};
	
	public void insertHandledNode(){
		ResourceBundle rb = ResourceBundle.getBundle("path");
		int insertTime = Integer.parseInt(rb.getString("insertTime"));
		service.scheduleAtFixedRate(runnable, insertTime, insertTime, TimeUnit.SECONDS);
	}
}

package com.deepspider.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.deepspider.rule.FilterTab;
import com.deepspider.rule.RegexSummary;
import com.deepspider.util.CalcMD5;
import com.deepspider.util.DownWeb;
import com.deepspider.util.SqlConfig;

public class CheckUpdate implements Runnable{
	
	private long checkTime;
	
	public CheckUpdate(long checkTime){
		this.checkTime = checkTime;
	}
	
	/**
	 * 
	 * 判断网页内容md5值是否变化判断网页内容是否更新
	 * @author JavaSheng
	 */
	public void run(){
		SqlMapClient sqlMap = SqlConfig.getSqlMapInstance();
		
		while(true){
			try {
				Thread.sleep(checkTime);
				System.out.println("begin update");
				@SuppressWarnings("unchecked")
				List<Map<String,String>> list = sqlMap.queryForList("getCheckUpdate");
				for(Map<String,String> checkUpdate : list){				
					String url = checkUpdate.get("url");
					String htmlMD5 = CalcMD5.getMD5((DownWeb.getWeb(url).get("html")));
					long lastUpdateTime = Long.parseLong(String.valueOf(checkUpdate.get("time")));
					if(!htmlMD5.equals(checkUpdate.get("htmlMD5"))){
						Map<String,Object> map = new HashMap<String,Object>();
						String content = DownWeb.getWeb(url).get("html");
						map.put("html",RegexSummary.HtmlSummary(content));
						map.put("content", FilterTab.HtmlContent(content));						
						long time = System.currentTimeMillis();
						map.put("recentTime",time);
						map.put("lastTime",lastUpdateTime);
						map.put("url",url);
						sqlMap.update("updateHtml", map);
						
						//更新已处理URL时间
						Map<String,Object> mapTime = new HashMap<String,Object>();
						mapTime.put("time",time);
						mapTime.put("url",url);
						sqlMap.update("updateFilterTime",mapTime);
					}else{
						//System.out.println("Do not need to update");
					}
					
				}
				System.out.println("end update");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}
	public long getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(long checkTime) {
		this.checkTime = checkTime;
	}
	
	

}

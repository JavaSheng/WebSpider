package com.deepspider.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载网页内容
 * @author JavaSheng
 *
 */
public class DownWeb {
	public static Map<String, String> getWeb(String url){
		BufferedReader reader = null;	 
		String result = "";
		String line = "";
		String charset = "utf-8";
		HttpURLConnection connection = null;
		URL u = null;
		Map<String, String> resultMap = new HashMap<String,String>();
		try{		
			u = new URL(url);
			connection = (HttpURLConnection)u.openConnection();
			//设置超时时间
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			while(connection.getResponseCode() == 302 || connection.getResponseCode() == 301){
				u = new URL(connection.getHeaderField("Location"));
				connection = (HttpURLConnection)u.openConnection();
			}
			connection.connect();
			
			//获取协议类型
			Map<String,List<String>> map = connection.getHeaderFields();
			for(Map.Entry<String, List<String>> entry : map.entrySet()){
				if(entry.getKey() == null){
					String http = entry.getValue().get(0).split(" ")[0];
					resultMap.put("protocol", http);
					break;
				}
			}
			String contentType = connection.getHeaderField("Content-Type");
			String server = connection.getHeaderField("Server");
				resultMap.put("server", server);
			int typeIndex = contentType.indexOf("charset");
			if(typeIndex != -1){
				String s = contentType.substring(typeIndex);
				if(s.toLowerCase().indexOf("gb") != -1){
					charset = "gbk";
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));
					while((line = reader.readLine()) != null)
						result += line;
					result = new String(result.getBytes("utf-8"),"utf-8");
				}else if(charset.toLowerCase().indexOf("utf")!=-1){
					charset = "utf-8";
					reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));
					while((line = reader.readLine()) != null)
						result += line;
				}
				
			}else{		
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));
				int lineCount = 0;
				while((line = reader.readLine()) != null){
					lineCount++;
					int charsetIndex = line.indexOf("charset");
					if(charsetIndex != -1){
						String tmp = line.substring(charsetIndex);
						if(tmp.toLowerCase().indexOf("gb") != -1){
							charset = "gbk";
							break;
						}else if(tmp.toLowerCase().indexOf("utf") != -1){
							charset = "utf-8";
							break;
						}else if(lineCount > 15){
							break;
						}
					}
				}
				
				u = new URL(url);
				connection = (HttpURLConnection)u.openConnection();
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));
				while((line = reader.readLine()) != null)
					result += line;
				if(charset.equals("gbk"))
					result = new String(result.getBytes("utf-8"),"utf-8");
			}
			
			resultMap.put("html", result);
		}catch(Exception e){
			//System.out.println("request error: " + url);
		}finally{
			try{
				if(connection != null){
					connection.disconnect();
				}

				if(reader != null){
					reader.close();
				}			
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return resultMap;
	}	
}

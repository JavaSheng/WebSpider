package com.deepspider.rule;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.deepspider.util.ConvertPercent;
import com.deepspider.pojo.WebNode;


public class RegexUrl {
	public static String WEB_PATTERN =  "<a.*?href[\\s]*?=[\\s]*?\"[\\s]*?([^\"]+?)[\\s]*?\"";//过滤url
	
	public static LinkedList<String> DomianUrl(String targethtml, WebNode node){
		
		String url = node.getRequestInfo().getUrl();//目标url
		
		//获取目标url二级或三级域名
		int beginIndex = url.indexOf(".");//二级三级域名起始索引
		int lastIndex = url.indexOf("/",8);//二级三级域名结束索引
		String domain = "";//二级或三级域名
		//判断有无目录
		if(lastIndex != -1){
			domain = url.substring(beginIndex + 1,lastIndex);
		}else
			domain = url.substring(beginIndex + 1);
	//	System.out.println("domain:" + domain);
		LinkedList<String> urlQueue = new LinkedList<String>();
		
		Pattern pattern = Pattern.compile(WEB_PATTERN,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(targethtml);
		
		/*
		int findCount = 0;//总共找到的url
		int totalCount = 0;//满足条件的url
		int ipCount = 0;//为ip地址的url
		*/
		while(matcher.find()){
			//totalCount++;
			String allUrl = matcher.group(1).trim();//匹配到的url
			/*
			 * 三种情况：相对路径     绝对路径   根路径
			 */
			try {
				if(allUrl.indexOf("%u") != -1)
					continue;
				allUrl = ConvertPercent.convertPercent(allUrl);//过滤普通含义%
				allUrl = URLDecoder.decode(allUrl,"utf8").replaceAll("&#x2F;", "/").replaceAll("&amp;", "&");//转义html转义字符
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				System.out.println("decode fail : " + allUrl);
				continue;
			}
			
			if(allUrl.toLowerCase().startsWith("http://")){//绝对路径
				Pattern pattern_ip = Pattern.compile("http://\\d+\\.\\d+\\.\\d+?\\.\\d");
				Matcher matcher_ip = pattern_ip.matcher(allUrl);
				if(matcher_ip.find()){//判断是否是IP地址
					//ipCount++;
					String ipAddress = allUrl.substring(7);
					int ipIndex = ipAddress.indexOf("/");
					if(ipIndex != -1){
						ipAddress = ipAddress.substring(0, ipIndex);
					}
					
					InetAddress address = null;
					try {
						address = InetAddress.getByName(ipAddress);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						//System.out.println("No Host : " + ipAddress);
						continue;
					}
					if((address.getHostName().indexOf(domain)) != -1){
						urlQueue.addLast(allUrl);

						//findCount++;
					}
				}
				else{//不是IP地址
					if(allUrl.indexOf(domain) != -1){//绝对路径
						urlQueue.addLast(allUrl);
						//findCount++;
					}
				}
			}else if(allUrl.toLowerCase().startsWith("https://")){//绝对路径
				Pattern pattern_ip = Pattern.compile("https://\\d+\\.\\d+\\.\\d+?\\.\\d");
				Matcher matcher_ip = pattern_ip.matcher(allUrl);
				if(matcher_ip.find()){//判断是否是IP地址
					//ipCount++;
					String ipAddress = allUrl.substring(8);
					int ipIndex = ipAddress.indexOf("/");
					if(ipIndex != -1){
						ipAddress = ipAddress.substring(0, ipIndex);
					}
					
					InetAddress address = null;
					try {
						address = InetAddress.getByName(ipAddress);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						//System.out.println("No Host : " + ipAddress);
						continue;
					}
					if((address.getHostName().indexOf(domain)) != -1){
						urlQueue.addLast(allUrl);

						//findCount++;
					}
				}
				else{//不是IP地址
					if(allUrl.indexOf(domain) != -1){//绝对路径
						urlQueue.addLast(allUrl);
						//findCount++;
					}
				}
			}else if(allUrl.startsWith("//")){//绝对路径
				Pattern pattern_ip = Pattern.compile("//\\d+\\.\\d+\\.\\d+?\\.\\d");
				Matcher matcher_ip = pattern_ip.matcher(allUrl);
				if(matcher_ip.find()){//判断是否是IP地址
					//ipCount++;
					String ipAddress = allUrl.substring(2);
					int ipIndex = ipAddress.indexOf("/");
					if(ipIndex != -1){
						ipAddress = ipAddress.substring(0, ipIndex);
					}
					
					InetAddress address = null;
					try {
						address = InetAddress.getByName(ipAddress);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						//System.out.println("No Host : " + ipAddress);
						continue;
					}
					if((address.getHostName().indexOf(domain)) != -1){
						allUrl = "http:" + allUrl;
						urlQueue.addLast(allUrl);
						
						//findCount++;
					}
				}
				else{//不是IP地址
					if(allUrl.indexOf(domain) != -1){//绝对路径
						allUrl = "http:" + allUrl;
						urlQueue.addLast(allUrl);
						//findCount++;
					}
				}
			}else if(allUrl.startsWith("/")){//根路径
				if(url.startsWith("http://")){
					int hostIndex = url.indexOf("/", 7);
					if(hostIndex != -1){
						String rootUrl  = url.substring(0,hostIndex);
						allUrl = rootUrl + allUrl;		
						urlQueue.addLast(allUrl);
						//findCount++;
					}else{
						allUrl = url + allUrl;
						urlQueue.addLast(allUrl);
						//findCount++;
					}
					
				}else if(url.startsWith("https://")){
					int hostIndex = url.indexOf("/", 8);
					if(hostIndex != -1){
						String rootUrl = url.substring(0,hostIndex);
						allUrl = rootUrl + allUrl;		
						urlQueue.addLast(allUrl);
						//findCount++;
					}else{
						allUrl = url + allUrl;
						urlQueue.addLast(allUrl);
						//findCount++;
					}
				}					

			}else if(!(allUrl.toLowerCase().startsWith("javascript"))){//相对路径
				//System.out.println(url);
				if(url.endsWith("/"))
					allUrl = url + allUrl;
				else
					allUrl = url + "/" + allUrl;					
				
				urlQueue.addLast(allUrl);
				//findCount++;
			}
			
		}	
		/*
		System.out.println("totalcount :" + totalCount);
		System.out.println("findcount :" + findCount);
		System.out.println("ipCount :" + ipCount);
		*/
		return urlQueue;
	}
}

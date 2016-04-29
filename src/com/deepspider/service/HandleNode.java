package com.deepspider.service;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.deepspider.dao.HandledNodeDao;
import com.deepspider.dao.WebNodeDao;
import com.deepspider.pojo.HandledQueue;
import com.deepspider.pojo.RequestInfo;
import com.deepspider.pojo.ResponseInfo;
import com.deepspider.pojo.WebNode;
import com.deepspider.queue.BloomFilter;
import com.deepspider.queue.UnvisitedQueue;
import com.deepspider.rule.FilterTab;
import com.deepspider.rule.RegexPlugin;
import com.deepspider.rule.RegexSummary;
import com.deepspider.rule.RegexUrl;
import com.deepspider.util.CalcMD5;
import com.deepspider.util.DownWeb;
import com.deepspider.util.ThreadManage;

/**
 * 爬虫处理线程
 * @author JavaSheng
 */
public class HandleNode implements Runnable{
	private ThreadManage threadManage;
	private UnvisitedQueue unvisitedQueue;
	private BloomFilter bloomFilterQueue;
	private List<WebNode> insertQueue = Collections.synchronizedList(new LinkedList<WebNode>());//插入数据库的缓存队列
	private List<HandledQueue> handledQueue = Collections.synchronizedList(new LinkedList<HandledQueue>());//插入数据库缓存队列
	private long time;
	
	public HandleNode(ThreadManage threadManage, UnvisitedQueue unvisitedQueue, BloomFilter bloomFilterQueue){
		this.threadManage = threadManage;
		this.unvisitedQueue = unvisitedQueue;
		this.bloomFilterQueue = bloomFilterQueue;
	}
	
	
	public void run(){
		/**
		 * 建立数据缓存队列
		 * @author JavaSheng
		 */
		WebNodeDao webNodeDao = new WebNodeDao(insertQueue);
		webNodeDao.insertWebNode();
		HandledNodeDao handledNodeDao = new HandledNodeDao(handledQueue);
		handledNodeDao.insertHandledNode();

		while(true){
			
			/**
			 * 判断待爬队列中是否还有未爬的URL
			 * @author JavaSheng
			 */
			synchronized(unvisitedQueue){
				if(unvisitedQueue.isUnvisitedQueueEmpty()){
					try {							
						unvisitedQueue.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				}else{
					String targetUrl = unvisitedQueue.deQueue();
					Spide spide = new Spide(targetUrl);
					threadManage.addTask(spide);
				}				
			}
			
		}	
	}
	
	/**
	 * 爬取线程
	 * @author JavaSheng
	 */
	private class Spide implements Runnable{
		
		private String targetUrl;
		public Spide(String targetUrl){
			this.targetUrl = targetUrl;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			WebNode targetNode = new WebNode();//网页所有信息
			RequestInfo targetRequestInfo = new RequestInfo();//请求信息
			ResponseInfo targetResponseInfo = new ResponseInfo();//响应信
			HandledQueue handledNode = new HandledQueue();//已处理URL
			

					
			/**
			 * 处理请求信息
			 * @author JavaSheng
			 */
			
			try{
				targetUrl = URLDecoder.decode(targetUrl, "utf8");
			}catch (UnsupportedEncodingException e) {
				System.out.println("No such encoding");		
			}
			Map<String,String> mapTarget = DownWeb.getWeb(targetUrl);
			String targethtml = mapTarget.get("html");
			
			if((targethtml!=null) && (targethtml.length() != 0)){//判断是否成功读取网页内容
				
				/**
				 * 处理请求信息
				 * @author JavaSheng
				 */
				targetRequestInfo.setUrl(targetUrl);
				try {
					targetRequestInfo.setHost((new URL(targetUrl)).getHost());
					targetRequestInfo.setIp(InetAddress.getByName(targetRequestInfo.getHost()).getHostAddress());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					//System.out.println("No host " + targetUrl);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					//System.out.println("No IP " + targetUrl);
				}			
				targetNode.setRequestInfo(targetRequestInfo);	
				targetNode.setTargetNode(true);	
				targetNode.setTargetUrl(targetUrl);
				
				/**
				 * 处理响应信息
				 * @author JavaSheng
				 */
				targetResponseInfo.setHtml(RegexSummary.HtmlSummary(targethtml));
				targetResponseInfo.setContent(FilterTab.HtmlContent(targethtml));
				targetResponseInfo.setProtocol(mapTarget.get("protocol"));
				targetResponseInfo.setServer(mapTarget.get("server"));
				String frame = RegexPlugin.FrameWork(targethtml);
				if(frame.length() == 0)
					frame = "无";
				targetResponseInfo.setFrame(frame);
				targetNode.setResponseInfo(targetResponseInfo);	
				time = System.currentTimeMillis();//记录爬取时间
				targetNode.setLastTime(time);
				
				/**
				 * 获取urlMD5,网页内容MD5添加到相应队列中
				 * @author JavaSheng
				 */
				String targetUrlMD5 = CalcMD5.getMD5(targetUrl);
				String targetHtmlMD5 = CalcMD5.getMD5(targethtml);				
				handledNode.setUrl(targetUrl);
				handledNode.setUrlMD5(targetUrlMD5);
				handledNode.setHtmlMD5(targetHtmlMD5);
				handledNode.setTime(time);
				
				insertQueue.add(targetNode);
				handledQueue.add(handledNode);
				bloomFilterQueue.addUrl(targetUrl);
										
				/**
				 * 获当前域名下的url，插入到待爬队列中
				 * @author JavaSheng
				 */
				LinkedList<String> urlQueue = RegexUrl.DomianUrl(targethtml,targetNode);
				if(!urlQueue.isEmpty()){				
					synchronized(unvisitedQueue){
						for(int i = 0; i < urlQueue.size(); i++){
							String url = urlQueue.get(i);
							if(!bloomFilterQueue.containsUrl(url)){								
								unvisitedQueue.enQueue(url);								
							}
						}
						
						unvisitedQueue.notify();
					}
					
					
				}else{
					//System.out.println("No suitable node");
				}		
					//sqlMap.update("updateStatus",targetUrl);
			}else{//未读取到网页内容			
				bloomFilterQueue.addUrl(targetUrl);
			}						
		}
		
	}
	 
	
}

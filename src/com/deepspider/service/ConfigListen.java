package com.deepspider.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import com.deepspider.queue.UnvisitedQueue;
import com.deepspider.util.ThreadManage;

public class ConfigListen implements Runnable{
	
	private int port ;
	private ThreadManage threadManage;
	private UnvisitedQueue unvisitedQueue;
	
	public ConfigListen(ThreadManage threadManage,UnvisitedQueue unvisitedQueue){
		this.threadManage = threadManage;
		this.unvisitedQueue = unvisitedQueue;
	}
	
	/**
	 * ͨ监听前台下发的配置
	 * @author JavaSheng
	 */
	public void run(){
		ServerSocket serverSocket = null;
		Socket socket = null;
		ResourceBundle rb = ResourceBundle.getBundle("path");//获取监听端口
		port = Integer.parseInt(rb.getString("port"));
		try {
			serverSocket = new ServerSocket(port);
			while(true){
				socket = serverSocket.accept();
				HandleListen handleListen = new HandleListen(socket);
				threadManage.addTask(handleListen);				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听处理线程
	 * @author JavaSheng
	 */
	 class HandleListen implements Runnable{
		private Socket socket;
		public HandleListen(Socket socket){
			this.socket = socket;
		}
		public void run(){
			BufferedReader reader = null;
			PrintWriter pw = null;
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String url = reader.readLine();
				synchronized(unvisitedQueue){
					unvisitedQueue.enQueue(url);
					unvisitedQueue.notify();//通知爬虫处理线程
				}		
				pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
				pw.println("Url Receive");
				pw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					if(reader != null){
						reader.close();
					} 
					
					if(pw != null){
						pw.close();
					}
					
					if(socket != null){
						socket.close();
					}
				}catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				}
			}
			
		}
	}
}

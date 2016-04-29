package com.deepspider.pojo;

import java.io.Serializable;

public class HandledQueue implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String url;
	private String urlMD5;
	private String htmlMD5;
	private long time;//插入时间
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlMD5() {
		return urlMD5;
	}
	public void setUrlMD5(String urlMD5) {
		this.urlMD5 = urlMD5;
	}
	public String getHtmlMD5() {
		return htmlMD5;
	}
	public void setHtmlMD5(String htmlMD5) {
		this.htmlMD5 = htmlMD5;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	
}

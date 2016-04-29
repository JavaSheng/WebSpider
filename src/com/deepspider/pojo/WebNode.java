package com.deepspider.pojo;



public class WebNode {
	
	private RequestInfo requestInfo;//请求信息
	private ResponseInfo responseInfo;//响应信息
	private boolean isTargetNode;//是否为配置节点
	private String targetUrl;//根节点url
	private long recentTime;//最近更新时间
	private long lastTime;//上次更新时间
	
	public RequestInfo getRequestInfo() {
		return requestInfo;
	}
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
	public ResponseInfo getResponseInfo() {
		return responseInfo;
	}
	public void setResponseInfo(ResponseInfo responseInfo) {
		this.responseInfo = responseInfo;
	}
	public boolean isTargetNode() {
		return isTargetNode;
	}
	public void setTargetNode(boolean isTargetNode) {
		this.isTargetNode = isTargetNode;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public long getRecentTime() {
		return recentTime;
	}
	public void setRecentTime(long recentTime) {
		this.recentTime = recentTime;
	}
	public long getLastTime() {
		return lastTime;
	}
	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
	
}

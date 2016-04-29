package com.deepspider.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPlugin {
	
	private static final String STRUTS = "<%@[\\s.]*taglib.*%>";//struts2库
	private static final String JS = "<script.*src.*/?>";//js库
	private static final String FRAMEWORK= "<link.*href.*/?>";//框架
	
	public static String FrameWork(String html){
		Pattern p_struts = Pattern.compile(STRUTS);
		Pattern p_js = Pattern.compile(JS);
		Pattern p_framework = Pattern.compile(FRAMEWORK);
		
		String struts = "";
		String js="";
		String framework = "";
		
		Matcher matcher = p_struts.matcher(html);
		while(matcher.find()){
			if(matcher.group().indexOf("struts") != -1){
				struts += "Struts ";
				break;
			}
		}
		
		matcher = p_js.matcher(html);
		while(matcher.find()){
			if(matcher.group().toLowerCase().indexOf("jquery") != -1){
				js += "JQuery ";
				break;
			}
		}
		
		matcher = p_framework.matcher(FRAMEWORK);
		while(matcher.find()){
			if(matcher.group().indexOf("bootstrap") != -1){
				framework += "BootStrap ";
			}
		}
		
		return struts + js + framework;
		
	}
	
	
}

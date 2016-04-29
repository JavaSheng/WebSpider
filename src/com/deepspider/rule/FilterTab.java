package com.deepspider.rule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterTab {
	public static final String FILTER_SCRIPT = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";//过滤script标签
	public static final String FILTER_STYLE = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";//过滤style标签
	public static final String FILTER_HTML = "<[^>]+>";//过滤html标签
	
	public static String HtmlContent(String srcHtml){
		String filterHtml = srcHtml;
		
		Pattern p_script = Pattern.compile(FILTER_SCRIPT, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(filterHtml);
		filterHtml = m_script.replaceAll("");
		
		Pattern p_style = Pattern.compile(FILTER_STYLE, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(filterHtml);
		filterHtml = m_style.replaceAll("");
		
		Pattern p_html = Pattern.compile(FILTER_HTML, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(filterHtml);
		filterHtml = m_html.replaceAll("");
		
		filterHtml.replaceAll("&nbsp;", " ");
		return filterHtml;
		
	}
}

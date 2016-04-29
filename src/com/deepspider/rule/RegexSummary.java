package com.deepspider.rule;

public class RegexSummary {
	private static final String DES_BEGIN = "name=\"description\"";//过滤<meta>标签中的desrciption属性
	
	//过滤<title>属性
	private static final String TITLE_BEGIN = "<title>";
	private static final String TITLE_END = "</title>";
	public static String HtmlSummary(String html){
		String result = "";
		
		int desIndex = html.indexOf(DES_BEGIN);
		if(desIndex != -1){
			String tmp = html.substring(desIndex);
			int contentIndex = tmp.indexOf("content=");
			if(contentIndex != -1){
				tmp = tmp.substring(contentIndex);
				tmp = tmp.substring(8);
				if(contentIndex != -1)
					result = tmp.substring(0,tmp.indexOf("\""));
				if(result.length()==0){
					int beginIndex = html.indexOf(TITLE_BEGIN);
					int endIndex = html.indexOf(TITLE_END);
					if((beginIndex != -1) && (endIndex != -1))
						result = html.substring(beginIndex + 7, endIndex);
				}
			}
			
		}else{
			int beginIndex = html.indexOf(TITLE_BEGIN);
			int endIndex = html.indexOf(TITLE_END);
			if((beginIndex != -1) && (endIndex != -1))
				result = html.substring(beginIndex + 7, endIndex);
		}
		
		return result;
	}
}

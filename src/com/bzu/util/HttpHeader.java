package com.bzu.util;

/**
 * 常用请求头的key
 *
 * @author xsc
 *
 */
public class HttpHeader {
	/*HTTP Request Header Key*/
	public static final String Accept="Accept";
	public static final String Content_Type="Content-Type";
	public static final String X_Requested_With="X-Requested-With";
	public static final String Referer="Referer";
	public static final String Accept_Language="Accept-Language";
	public static final String Accept_Encoding="Accept-Encoding";
	public static final String User_Agent="User-Agent";
	public static final String Host="Host";
	public static final String Content_Length="Content-Length";
	public static final String DNT="DNT";
	public static final String Connection="Connection";
	public static final String Cache_Control="Cache-Control";
	public static final String Cookie="Cookie";
	public static final String Origin = "Origin";
	public static final String Upgrade_Insecure_Requests = "Upgrade-Insecure-Requests";

	/**
	 * 返回一个默认的User-Agent
	 * @return
	 */
	public static String getUsrAgt(){
		return "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3732.400 QQBrowser/10.5.3819.400";
	}

	/**
	 * 返回一个有user-agent的默认header map
	 * @return
	 */
	public static Map<String,String> getHeaders(){
		Map<String,String> map = new HashMap<>();
		map.put(User_Agent, getUsrAgt());
		return map;
	}
}

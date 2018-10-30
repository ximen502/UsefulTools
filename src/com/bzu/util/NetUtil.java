package com.bzu.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @brief Network state检测类. 判断手机是否已经连接网络，使用HTTP请求数据。
 *        字符串连接的时候最好使用StringBuilder(单线程情况)，提高效率。 需要添加一个修改：直接返回Stream具备通用性。11/1
 *        2015/04/13:增加和重写了一些get,post方法，增加了HTTP header作为方法的参数。
 * @author xsc
 * @date 2013/1/25 16:14:00
 * @version 1.0
 */
public class NetUtil {
	// 存储用户的cookie,方便其他程序使用
	public static String cookie = "";
	// 存储URL文件的长度
	public static long contentLength = 0;
	
	// HTTP Header常量
	private static final String CONTENTTYPE = "Content-Type";
	private static final String USERAGENT = "User-Agent";
	private static final String HOST = "Host";
	private static final String COOKIE = "Cookie";

	/**
	 * 判断网络是否连通
	 */
//	public static boolean isNetworkConnected(Context context) {
//		/*
//		 * The primary responsibilities of this class(ConnectivityManager) are
//		 * to: 1.Monitor network connections (Wi-Fi, GPRS, UMTS, etc.) 2.Send
//		 * broadcast intents when network connectivity changes 3.Attempt to
//		 * "fail over" to another network when connectivity to a network is lost
//		 * 4.Provide an API that allows applications to query the coarse-grained
//		 * or fine-grained state of the available networks 千万不要忘了在manifest里面加个权限
//		 * ，粗心的朋友一定要记住： <uses-permission
//		 * android:name="android.permission.ACCESS_NETWORK_STATE"/>
//		 */
//		ConnectivityManager conman = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		NetworkInfo netInfo = conman.getActiveNetworkInfo();
//
//		if (netInfo != null && netInfo.isConnected()) { // 有网络连接
//			return true;
//		} else { // 没有网络连接
//			return false;
//		}
//	}

	/**
	 * 判断一个url是否可以连接到
	 */
	public static boolean isUrlReachable(String strurl) {
		int timeout = 5;
		try {
			URL url = new URL(strurl);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			// urlConnection.setRequestProperty(field, newValue);
			urlConnection.setRequestProperty("Accept", "application/javascript, */*;q=0.8");
			urlConnection.setRequestProperty("Accept-Language", "zh-Hans-CN,zh-Hans;q=0.8,en-US;q=0.5,en;q=0.3");
			urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.3; WOW64; Trident/7.0; .NET4.0E; .NET4.0C; .NET CLR 3.5.30729; .NET CLR 2.0.50727; .NET CLR 3.0.30729)");
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setConnectTimeout(1000 * timeout);// 超时时间为5秒
			urlConnection.connect();
			System.out.println("ResponseCode:"+urlConnection.getResponseCode());
			if (urlConnection.getResponseCode() == 200) {
				System.out.println("content-length:"+urlConnection.getContentLength());
				return true;
			} else {
				return false;
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			System.out.println("connect time out...");
			return false;
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			System.out.println("connect exception..." + e);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 判断一个IP地址是否可以到达ICMP,TCP
	 * @param hostIp
	 * @return
	 */
	public static boolean isReachable(String hostIp) {
		// TODO Auto-generated method stub
		try {
			InetAddress address = InetAddress.getByName(hostIp);
			
			boolean reachable = address.isReachable(10000);
			System.out.println(reachable);
			return reachable;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 根据url去获取一个图片
	 */
//	public static Bitmap fetchBitmap(String url) {
//		Bitmap bm = null;
//
//		try {
//			URL qrurl = new URL(url);
//			URLConnection conn = qrurl.openConnection();
//			conn.connect();
//			InputStream is = conn.getInputStream();
//			BufferedInputStream bis = new BufferedInputStream(is);
//			bm = BitmapFactory.decodeStream(bis);
//			bis.close();
//			is.close();
//			return bm;
//		} catch (SocketTimeoutException e) {
//			e.printStackTrace();
//			System.out.println("connect time out...");
//			return null;
//		} catch (ConnectException e) {// Network is unreachable
//			e.printStackTrace();
//			System.out.println("connect exception..." + e);
//			return null;
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			return null;
//		}
//	}

	/**
	 * 增加一个返回Stream的方法，具有通用性
	 * 
	 * @return 字节流
	 */
	public static InputStream getStream(String url, String cookie) {
		URL resultUrl = null;
		URLConnection conn = null;
		InputStream is = null;
		try {
			resultUrl = new URL(url);
			conn = resultUrl.openConnection();
			conn.setRequestProperty("Cookie", cookie);
			conn.connect();
			is = conn.getInputStream();
			// 取得响应头中的content-length
			if (conn.getContentLength() != -1)
				contentLength = conn.getContentLength();
			if (is != null)
				return is;
			else
				return null;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			System.out.println("connect time out...");
			return null;
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			System.out.println("connect exception..." + e);
			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("spec could not be parsed as a URL...." + e);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			// 在此块中不要断开connection连接，也不要关闭InputStream流，否则接收不到数据提示BuffedInputStream
			// is closed
			// if(conn != null)
			// ((HttpURLConnection)conn).disconnect();
			// if(is != null){
			// try {
			// is.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
			// }
		}
	}

	/**
	 * 根据指定的url请求数据，使用java 提供的http get请求方式
	 * 
	 * @param url
	 *            包含数据的url
	 * @return 返回一个字符串
	 */
	public static String getRequest(String url) {
		if (url == null || url.length() <= 0) {
			return "url can not be null!";
		}
		StringBuilder result = new StringBuilder();
		URL weburl = null;
		HttpURLConnection urlcon = null;
		InputStreamReader isr = null;
		try {
			weburl = new URL(url);
			urlcon = (HttpURLConnection) weburl.openConnection();
			urlcon.setConnectTimeout(25000);// 超时时间为25秒
			isr = new InputStreamReader(urlcon.getInputStream());
			BufferedReader bufReader = new BufferedReader(isr);
			String tmp = null;
			if (urlcon.getResponseCode() == 200) {
				// 新增一句取得cookie的代码，蹩脚。加上一个null的验证，如果用户登录失败则Set-Cookie返回null
				if (urlcon.getHeaderField("Set-Cookie") != null) {
					cookie = urlcon.getHeaderField("Set-Cookie").split(";")[0];
					// Const.COOKIE =
					// urlcon.getHeaderField("Set-Cookie").split(";")[0];
				}
				while ((tmp = bufReader.readLine()) != null) {
					result.append(tmp);
				}
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			System.out.println("connect time out...");
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			System.out.println("connect exception..." + e);
		}catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println(e.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlcon != null)
				urlcon.disconnect();
			try {
				if (isr != null)// 防止Null Pointer Exception空指针
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!result.equals("")) {
			return result.toString();
		} else {
			return "";
		}
	}

	/**
	 * 发送注册请求 根据指定的url请求数据，使用java 提供的http get请求方式
	 * 
	 * @param url
	 *            包含数据的url
	 * @return 返回一个字符串
	 */
	public static String getRequest(String url, String cookie) {
		if (url == null || url.length() <= 0) {
			return "url can not be null!";
		}
		StringBuilder result = new StringBuilder();
		URL weburl = null;
		HttpURLConnection urlcon = null;
		InputStreamReader isr = null;
		try {
			weburl = new URL(url);
			urlcon = (HttpURLConnection) weburl.openConnection();
			urlcon.setConnectTimeout(25000);// 超时时间为25秒
			urlcon.setRequestProperty("Cookie", cookie);
			isr = new InputStreamReader(urlcon.getInputStream());
			BufferedReader bufReader = new BufferedReader(isr);
			String tmp = null;
			while ((tmp = bufReader.readLine()) != null) {
				result.append(tmp);
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			System.out.println("connect time out...");
			return "SocketTimeoutException";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			// System.out.println("connect exception..."+e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlcon != null)
				urlcon.disconnect();
			try {
				if (isr != null)// 防止Null Pointer Exception空指针
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!result.equals("")) {
			return result.toString();
		} else {
			return "";
		}
	}

	/**
	 * 发送注册请求 根据指定的url请求数据，使用java 提供的http get请求方式
	 * 
	 * @param url
	 *            包含数据的url
	 * @return 返回一个字符串
	 */
	/*public static String getReqJ(String url, Map headers) {
		final String CONTENTTYPE = "Content-Type";
		final String USERAGENT = "User-Agent";
		final String HOST = "Host";
		final String COOKIE = "Cookie";
		if (url == null || url.length() <= 0) {
			return "url can not be null!";
		}
		StringBuilder result = new StringBuilder();
		URL weburl = null;
		HttpURLConnection urlcon = null;
		InputStreamReader isr = null;
		try {
			weburl = new URL(url);
			urlcon = (HttpURLConnection) weburl.openConnection();
			urlcon.setConnectTimeout(25000);// 超时时间为25秒
			urlcon.setRequestProperty(CONTENTTYPE, headers.get(CONTENTTYPE).toString());
			urlcon.setRequestProperty(USERAGENT, headers.get(USERAGENT).toString());
			urlcon.setRequestProperty(HOST, headers.get(HOST).toString());
			urlcon.setRequestProperty(COOKIE, headers.get(COOKIE).toString());
			// urlcon.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");
			// urlcon.addRequestProperty("User-Agent",
			// "Mozilla/5.0 (Windows NT 6.3; WOW64) Trident/7.0; rv:11.0");
			// urlcon.addRequestProperty("Host", "192.168.0.104:9090");
			// urlcon.addRequestProperty("Cookie",
			// "JSESSIONID=1kmid7132cm6p1neugg08tuk7l");
			isr = new InputStreamReader(urlcon.getInputStream());
			BufferedReader bufReader = new BufferedReader(isr);
			String tmp = null;
			while ((tmp = bufReader.readLine()) != null) {
				result.append(tmp);
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			//L.w("connect time out...");
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			//L.w("connect exception..." + e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlcon != null)
				urlcon.disconnect();
			try {
				if (isr != null)// 防止Null Pointer Exception空指针
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!result.equals("")) {
			return result.toString();
		} else {
			return "";
		}
	}*/
	
	/**
	 * 发送注册请求 根据指定的url请求数据，使用java 提供的http get请求方式
	 * 
	 * @param url
	 *            包含数据的url
	 * @return 返回一个字符串
	 */
	public static String getReqJ(String url, Map headers) {
		if (url == null || url.length() <= 0) {
			return "url can not be null!";
		}
		StringBuilder result = new StringBuilder();
		URL weburl = null;
		HttpURLConnection urlcon = null;
		InputStreamReader isr = null;
		try {
			weburl = new URL(url);
			urlcon = (HttpURLConnection) weburl.openConnection();
			urlcon.setConnectTimeout(25000);// 超时时间为25秒
			
			//set request property
			Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
			Entry<String, String> entry;
			String key;
			String value;
			while (iter.hasNext()) {

			    entry = iter.next();

			    key = entry.getKey();

			    value = entry.getValue();
			    //System.out.println(key+":"+value);
			    urlcon.setRequestProperty(key, value);
			}
			//System.out.println(headers);
			
			
			isr = new InputStreamReader(urlcon.getInputStream());
			BufferedReader bufReader = new BufferedReader(isr);
			String tmp = null;
			while ((tmp = bufReader.readLine()) != null) {
				result.append(tmp);
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			//L.w("connect time out...");
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			//L.w("connect exception..." + e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlcon != null)
				urlcon.disconnect();
			try {
				if (isr != null)// 防止Null Pointer Exception空指针
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!result.equals("")) {
			return result.toString();
		} else {
			return "";
		}
	}
	
	/**
	 * 根据指定的url请求数据，使用java 提供的http get请求方式
	 * 
	 * @param url
	 *            包含数据的url
	 * @param cookie
	 *            用户的cookie
	 * @return 返回一个字符串
	 */
	public static String getReqJ(String url, String cookie) {
		if (url == null || url.length() <= 0) {
			return "url can not be null!";
		}
		StringBuilder result = new StringBuilder();
		URL weburl = null;
		HttpURLConnection urlcon = null;
		InputStreamReader isr = null;
		try {
			weburl = new URL(url);
			urlcon = (HttpURLConnection) weburl.openConnection();
			urlcon.addRequestProperty("Cookie", cookie);
			urlcon.setConnectTimeout(25000);// 超时时间为25秒
			isr = new InputStreamReader(urlcon.getInputStream());
			BufferedReader bufReader = new BufferedReader(isr);
			String tmp = null;
			while ((tmp = bufReader.readLine()) != null) {
				result.append(tmp);
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			System.out.println("connect time out...");
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			System.out.println("connect exception..." + e);
			return "-2";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (urlcon != null)
				urlcon.disconnect();
			try {
				if (isr != null)// 防止Null Pointer Exception空指针
					isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!result.equals("")) {
			return result.toString();
		} else {
			return "";
		}
	}

	/**
	 * 根据指定的url请求数据，使用apache 提供的http get请求方式
	 * 
	 * @param url
	 *            包含数据的url
	 * @return 返回一个字符串
	 */
//	public static String getReqA(String url) {
//		String resultData = "";
//		// HttpGet连接对象
//		HttpGet httpRequest = new HttpGet(url);
//		// 去的HttpClient对象
//		HttpClient httpClient = new DefaultHttpClient();
//		try {
//			HttpResponse httpResponse = httpClient.execute(httpRequest);
//			// 请求成功
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//				// 去的返回的字符串
//				resultData = EntityUtils.toString(httpResponse.getEntity());
//			}
//		} catch (SocketTimeoutException e) {
//			e.printStackTrace();
//			System.out.println("connect time out...");
//			return "-1";
//		} catch (ConnectException e) {// Network is unreachable
//			e.printStackTrace();
//			System.out.println("connect exception..." + e);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return resultData;
//	}

	public static Map<String, String> stringParamsToMap(String params) {
		Map<String, String> mapKV = new HashMap<>();
		String[] array = params.split("&");
		for (int i = 0; i < array.length; i++) {
			System.out.println(array[i]);
			String kv = array[i];
			String[] kvArray = kv.split("=");
			/*for (int j = 0; j < kvArray.length; j++) {
				System.out.println(kvArray[j]+" --");
			}*/
			String key = kvArray.length>=1?kvArray[0]:"";
			String value = kvArray.length>=2?kvArray[1]:"";
			mapKV.put(key, value);
		}
		
		System.out.println(mapKV);
		return mapKV;
	}
	
	/**
	 * URL的Map参数进行URL编码
	 * @param params
	 * @return
	 */
	public static String encodeUrlParams(Map<String, String> params) {
		if (params==null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();

		Map.Entry<String, String> entry;
		String key;
		String value;
		int i=0;
		while (iter.hasNext()) {

			entry = iter.next();

			key = entry.getKey();

			value = entry.getValue();
			//System.out.println(key+":"+value);
			try {
				value = URLEncoder.encode(value, "UTF8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
//			if (i==0) {
//				sb.append("?");
//			}
			sb.append(key).append("=").append(value);
			if (i<params.size()-1) {
				sb.append("&");
			}
			i++;
		}
		String result = sb.toString();
		System.out.println("编码后的参数:"+result);
		//Log.d("params", "编码后的参数:"+result);
		return result;
	}
	/**
	 * HTTP POST REQUEST
	 * 
	 * @param url
	 *            请求url
	 * @param content
	 *            post的内容
	 */
	public static String postReqJ(String url, String content, Map headers) {

		try {
			// Post请求的url，与get不同的是不需要带参数
			URL postUrl = new URL(url);
			// 打开连接
			HttpURLConnection connection = (HttpURLConnection) postUrl
					.openConnection();
			// Output to the connection.Default is false,set to
			// true because post method must write something to
			// the connection
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true
			connection.setDoOutput(true);
			// Read from the connection .Default is true
			connection.setDoInput(true);
			// set the post method. Default is GET
			connection.setRequestMethod("POST");
			// Post can't use caches
			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			// This method takes effects to every instance of this
			// class. URLConnection.setFollowRedirects是static
			// 函数，作用于所有的URLConnection对象
			// connection.setFollowRedirects(true);

			// This method only takes effects to this instance
			// URLConnection.setInstanceFollowRedirects是成员函数
			// 仅作用于当前函数
			connection.setInstanceFollowRedirects(true);
			if (headers != null) {
//				connection.setRequestProperty(CONTENTTYPE, headers.get(CONTENTTYPE).toString());
//				connection.setRequestProperty(USERAGENT, headers.get(USERAGENT).toString());
//				connection.setRequestProperty(HOST, headers.get(HOST).toString());
//				connection.setRequestProperty(COOKIE, headers.get(COOKIE).toString());
				
				Iterator<Entry<String, String>> iter = headers.entrySet().iterator();

				Entry<String, String> entry;
				String key;
				String value;
				while (iter.hasNext()) {

				    entry = iter.next();

				    key = entry.getKey();

				    value = entry.getValue();
				    System.out.println(key+":"+value);
				    connection.setRequestProperty(key, value);
				}
			}
			// Set the content type to urlencoded,
			// because we will write some URL-encoded content to
			// the connection. Settings above must be set before connect!
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			// 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode进行编码
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成
			// 要注意的是connection.getOutputStream会隐含的进行connect
			connection.connect();
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream());
			// The URL-encoded content
			//content = URLEncoder.encode(content, "UTF-8");
			// 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
			out.writeBytes(content);
			out.flush();
			out.close();// flush and close
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
			int code = connection.getResponseCode();
			System.out.println(code);
			// 字符串连接改为StringBuilder提高效率
			StringBuilder result = new StringBuilder();
			String tmp = "";
			while ((tmp = reader.readLine()) != null) {
				result.append(tmp);
			}
			reader.close();// 关闭BufferedReader
			connection.disconnect();// 断开URLConnection
			// if(code == 200){
			// String cookie = connection.getHeaderField("Set-Cookie");
			// Map map = connection.getHeaderFields();
			//
			// System.out.println("message- " + map.get("Set-Cookie"));
			// }
			// System.out.println("len- " + result.toString().length());
			return result.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			System.out.println("connect time out...");
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			System.out.println("connect exception..." + e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Java发送HTTP post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param content
	 *            请求内容
	 * @param headers
	 *            HTTP header
	 * @return
	 */
	public static String postReqJ(String url, Map content, Map headers) {
		final String CONTENTTYPE = "Content-Type";
		final String USERAGENT = "User-Agent";
		final String HOST = "Host";
		final String COOKIE = "Cookie";
		try {
			// Post请求的url，与get不同的是不需要带参数
			URL postUrl = new URL(url);
			// 打开连接
			HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
			// Output to the connection.Default is false,set to
			// true because post method must write something to
			// the connection
			// 设置是否向connection输出，因为这个是post请求，参数要放在
			// http正文内，因此需要设为true

			connection.setDoOutput(true);
			// Read from the connection .Default is true
			connection.setDoInput(true);
			// set the post method. Default is GET
			connection.setRequestMethod("POST");
			// Post can't use caches
			// Post 请求不能使用缓存
			connection.setUseCaches(false);
			// This method takes effects to every instance of this
			// class. URLConnection.setFollowRedirects是static
			// 函数，作用于所有的URLConnection对象
			// connection.setFollowRedirects(true);

			// This method only takes effects to this instance
			// URLConnection.setInstanceFollowRedirects是成员函数
			// 仅作用于当前函数
			connection.setInstanceFollowRedirects(true);
			// Set the content type to urlencoded,
			// because we will write some URL-encoded content to
			// the connection. Settings above must be set before connect!
			// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
			// 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode进行编码
			if (headers != null) {
//				connection.setRequestProperty(CONTENTTYPE, headers.get(CONTENTTYPE).toString());
//				connection.setRequestProperty(USERAGENT, headers.get(USERAGENT).toString());
//				connection.setRequestProperty(HOST, headers.get(HOST).toString());
//				connection.setRequestProperty(COOKIE, headers.get(COOKIE).toString());
				
				Iterator<Entry<String, String>> iter = headers.entrySet().iterator();

				Entry<String, String> entry;
				String key;
				String value;
				while (iter.hasNext()) {

				    entry = iter.next();

				    key = entry.getKey();

				    value = entry.getValue();
				    System.out.println(key+":"+value);
				    connection.setRequestProperty(key, value);
				}
			}
			connection.setRequestProperty("client-info", "userID=6&version=832&phoneModel=nokia");
			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成
			// 要注意的是connection.getOutputStream会隐含的进行connect
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			// The URL-encoded content
			StringBuilder strContent = new StringBuilder();
			// 正文，正文内容其k跟get的URL中'?'后的参数字符串一致
			Iterator<Entry<String, String>> it = content.entrySet().iterator();
			int position = 0;
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue();
				if (position == content.size() - 1)
					strContent.append(key + "=" + value);
				else
					strContent.append(key + "=" + value + "&");
				position++;
			}
			out.writeBytes(URLEncoder.encode(strContent.toString(), "UTF-8"));
			out.flush();
			out.close();// flush and close
			int code = connection.getResponseCode();
			System.out.println(strContent);
			System.out.println("ResponseCode:" + code);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));// 设置编码,否则中文乱码
			// 字符串连接改为StringBuilder提高效率
			StringBuilder result = new StringBuilder();
			String tmp = "";
			while ((tmp = reader.readLine()) != null) {
				// result
				result.append(tmp);
			}
			reader.close();// 关闭BufferedReader
			connection.disconnect();// 断开URLConnection
			// if(code == 200){
			// String cookie = connection.getHeaderField("Set-Cookie");
			// Map map = connection.getHeaderFields();
			// COOKIE = (map.get("Set-Cookie").toString().split(";")[0]);
			// System.out.println("message- " + COOKIE);
			// }
			return result.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			//L.w("connect time out...");
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			//L.w("connect exception..." + e);
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			//L.w("exception..." + e);
			return null;
		}
	}
}
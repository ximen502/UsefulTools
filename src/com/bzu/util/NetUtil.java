package com.bzu.util;

import java.io.*;
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


	public static final String GET = "GET";
	public static final String POST = "POST";

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
		try {
			InetAddress address = InetAddress.getByName(hostIp);
			
			boolean reachable = address.isReachable(10000);
			System.out.println(reachable);
			return reachable;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

    protected static String getParamsEncoding() {
        return "UTF-8";
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
	 * 发送请求
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String getReqJ(String url, Map<String, String> headers) {
		return getReqJ(url, headers, false);
	}

	/**
	 * 发送请求 根据指定的url请求数据，使用java 提供的http get请求方式
	 * @param url
	 * @param headers
	 * @param singleLine 返回的字符串结果是否要单行显示（默认false保持原行数）
	 * @return 返回一个字符串
	 */
	private static String getReqJ(String url, Map<String, String> headers, boolean singleLine) {
		if (url == null || url.length() <= 0) {
			throw new RuntimeException("url can not be null or empty");
		}
		StringBuilder result = new StringBuilder();
		InputStream is = null;
		InputStreamReader isr = null;
		try {
			if (singleLine) {// 去除换行
				isr = new InputStreamReader(getReqJStream(url, headers));
				BufferedReader bufReader = new BufferedReader(isr);
				String tmp = null;
				result.append(tmp);
				while ((tmp = bufReader.readLine()) != null) {
				}
			} else {// 保留换行
				is = getReqJStream(url, headers);
				byte[] b = new byte[1024];
				String temp = null;
				int len = 0;
				while ((len = is.read(b)) != -1) {
					temp = new String(b, 0, len);
					result.append(temp);
				}
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			return "-1";
		} catch (ConnectException e) {// Network is unreachable
			e.printStackTrace();
			//L.w("connect exception..." + e);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (isr != null)// 防止Null Pointer Exception空指针
					isr.close();
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	/**
	 * 发送请求 根据指定的url请求数据，使用java 提供的http get请求方式
	 *
	 * @param url 包含数据的url
	 * @return 返回一个输入流
	 */
	public static InputStream getReqJStream(String url, Map<String, String> headers) {
		if (url == null || url.length() <= 0) {
			throw new RuntimeException("url can not be null or empty");
		}
		URL weburl = null;
		HttpURLConnection urlcon = null;
		try {
			weburl = new URL(url);
			urlcon = (HttpURLConnection) weburl.openConnection();
			urlcon.setConnectTimeout(25000);// 超时时间为25秒

			//set request property
			if (headers != null){
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();
				Entry<String, String> entry;
				String key;
				String value;
				while (it.hasNext()) {

					entry = it.next();

					key = entry.getKey();

					value = entry.getValue();
					//System.out.println(key+":"+value);
					urlcon.setRequestProperty(key, value);
				}
			}

			return urlcon.getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据指定的url请求数据，使用java 提供的http get请求方式
	 * 
	 * @param url 包含数据的url
     *
	 * @return 返回一个字符串
	 */
	public static String getReqJ(String url) {
		return getReqJ(url, null);
	}

//	/**
//	 * 根据指定的url请求数据，使用apache 提供的http get请求方式
//	 *
//	 * @param url
//	 *            包含数据的url
//	 * @return 返回一个字符串
//	 */
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
     *
     * @param params
     * @return
     */
    public static String encodeUrlParams(Map<String, String> params) {
        if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();

        Map.Entry<String, String> entry;
        String key;
        String value;
        int i = 0;
        while (iter.hasNext()) {

            entry = iter.next();

            key = entry.getKey();

            value = entry.getValue();
            //System.out.println(key+":"+value);
            try {
                key = URLEncoder.encode(key, getParamsEncoding());
                value = URLEncoder.encode(value, getParamsEncoding());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sb.append(key).append("=").append(value);
            if (i < params.size() - 1) {
                sb.append("&");
            }
            i++;
        }
        String result = sb.toString();
        System.out.println("编码后的参数:" + result);
        //Log.d("params", "编码后的参数:"+result);
        return result;
    }

    /**
     * URL的Map参数进行URL编码
     *
     * @param params
     * @param paramsEncoding
     * @return
     */
    public static byte[] encodeUrlParams(Map<String, String> params, String paramsEncoding) {
        if (params == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();

        Map.Entry<String, String> entry;
        String key;
        String value;

        try {
            while (iter.hasNext()) {

                entry = iter.next();

                key = entry.getKey();

                value = entry.getValue();

                key = URLEncoder.encode(key, paramsEncoding);

                value = URLEncoder.encode(value, paramsEncoding);
                sb.append(key).append("=").append(value).append("&");
                //System.out.println(key+":"+value);
            }
            String result = sb.toString();
            System.out.println("编码后的参数:" + result);
            return result.getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, e);
        }
        //Log.d("params", "编码后的参数:"+result);
    }

    /**
     * 根据url和参数发送post请求
     * @param url
     * @param params
     * @return
     */
    public static String postReqJ(String url, String params){
        return postReqJ(url, params, null);
    }

	/**
	 * HTTP POST REQUEST
	 * 
	 * @param url
	 *            请求url
	 * @param params
	 *            post的内容
     * @param headers 请求头
	 */
	public static String postReqJ(String url, String params, Map<String, String> headers) {
        return postReqJ(url, stringParamsToMap(params), headers);

		/*try {
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
			//params = URLEncoder.encode(params, "UTF-8");
			// 正文，正文内容其实跟get的URL中'?'后的参数字符串一致
			out.writeBytes(params);
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
		}*/
	}
	
	/**
	 * Java发送HTTP post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求内容
	 * @param headers
	 *            HTTP header
	 * @return
	 */
	public static String postReqJ(String url, Map params, Map headers) {

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
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
			if (headers != null) {
				Iterator<Entry<String, String>> it = headers.entrySet().iterator();

				Entry<String, String> entry;
				String key;
				String value;
				while (it.hasNext()) {

				    entry = it.next();

				    key = entry.getKey();

				    value = entry.getValue();
				    System.out.println(key+":"+value);
				    connection.setRequestProperty(key, value);
				}
			}

			// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成
			// 要注意的是connection.getOutputStream会隐含的进行connect
			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			// The URL-encoded content
//			StringBuilder sbParams = new StringBuilder();
//            if (params != null) {
//                // 正文，正文内容其k跟get的URL中'?'后的参数字符串一致
//                Iterator<Entry<String, String>> it = params.entrySet().iterator();
//                int position = 0;
//                while (it.hasNext()) {
//                    Map.Entry<String, String> entry = it.next();
//                    String key = entry.getKey();
//                    String value = entry.getValue();
//                    if (position == params.size() - 1) {
//                        sbParams.append(key).append("=").append(value);
//                    }else {
//                        sbParams.append(key).append("=").append(value).append("&");
//                    }
//                    position++;
//                }
//                final String encodeParams = URLEncoder.encode(sbParams.toString(), "UTF-8");
//                out.writeBytes(encodeParams);
//                System.out.println(sbParams.toString());
//                System.out.println(encodeParams);
//            }

            if(params != null){
                out.write(encodeUrlParams(params, getParamsEncoding()));
            }

            out.flush();
            out.close();// flush and close
            int code = connection.getResponseCode();
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

	/**
	 * 上传文件
	 * @param params 相关参数
	 * @param file 图片文件
	 */
	private void uploadFile(String uploadUrl, Map<String, String> params, File file) {
		if(params==null) {
			return;
		}

		final String CRLF = "\r\n";
		final String PREFIX = "--";
		try {
			String BOUNDARY = "AaB03x"; // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data";

			URL url = new URL(uploadUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);


			// http body的字符串
			StringBuffer body = new StringBuffer();

			Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
			Map.Entry<String, String> entry;
			String key;
			String value;
			while (iter.hasNext()) {
				entry = iter.next();
				key = entry.getKey();
				value = entry.getValue();
				//System.out.println(key+":"+value);
				body.append(PREFIX).append(BOUNDARY).append(CRLF);
				body.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(CRLF).append(CRLF);
				body.append(value).append(CRLF);
			}

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(body.toString().getBytes());// 发送表单字段数据

			// 添加原照的jpg----
			if(file!=null && file.exists()) {
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX).append(BOUNDARY).append(CRLF);
				// 声明pic字段，文件名为boris.png
				sb.append("Content-Disposition: form-data; name=\"IconPath\"; filename=\""+file.getName()+"\"\r\n");
				// 声明上传文件的格式
				sb.append("Content-Type: image/png\r\n\r\n");
				outStream.write(sb.toString().getBytes());
				InputStream inputStream = new FileInputStream(file);
				int readed = 0;
				byte[] buffer = new byte[8192];
				while ((readed = inputStream.read(buffer)) != -1){
					outStream.write(buffer, 0 , readed);
				}

				outStream.write(CRLF.getBytes());
			}
			// --End 原照的jpg---

			byte[] end_data = (PREFIX + BOUNDARY + "--").getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			int cah = conn.getResponseCode();
			if (cah != 200) {
				System.out.println("失败，请检查网络连接！");
				return;
			}
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] data = new byte[1024];
			int count = -1;
			while ((count = is.read(data, 0, 1024)) != -1)
				os.write(data, 0, count);
			data = null;
			String result = new String(os.toByteArray(), "UTF-8");
			outStream.close();
			conn.disconnect();
			System.out.println("成功！");

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("失败，请检查您网络是否通畅！");
		}
	}
}

package com.bzu.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

public class FileUtil {
	/**
	 * 根据一个绝对路径读文件
	 * @param fileName 文件的绝对路径
	 * @param charSet
	 * @return 返回文本文件中的字符串内容
	 */
	public static String readFileSdcard(String fileName, String charSet) {
		String res = "";
		try {
			InputStream is = new FileInputStream(fileName);
			if(charSet==null || charSet.length()==0){
				charSet = "UTF-8";
			}
			InputStreamReader isr = new InputStreamReader(is, charSet);
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String t;
			while((t = br.readLine())!=null){
				sb.append(t+"\r\n");
			}
			is.close();
			isr.close();
			res = sb.toString();
			
//			FileInputStream fin = new FileInputStream(fileName);
//			int length = fin.available();
//			byte[] buffer = new byte[length];
//			fin.read(buffer);
//			res = EncodingUtils.getString(buffer, "UTF-8");
//			fin.close();
//			buffer = null;
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(context, "加载错误。。。", 0).show();
			System.out.print("读取数据失败");
		}
		return res;
	}
	/**
	 * 根据一个绝对路径读文件
	 * @param fileName 文件的绝对路径
	 * @return 返回文本文件中的字符串内容
	 */
	public static String readFileSdcard(String fileName) {
		String res = "";
		try {
			InputStream is = new FileInputStream(fileName);
			InputStreamReader isr = new InputStreamReader(is, "UTF8");
			BufferedReader br = new BufferedReader(isr);
			StringBuilder sb = new StringBuilder();
			String t;
			while((t = br.readLine())!=null){
				sb.append(t);
			}
			is.close();
			isr.close();
			res = sb.toString();
			
//			FileInputStream fin = new FileInputStream(fileName);
//			int length = fin.available();
//			byte[] buffer = new byte[length];
//			fin.read(buffer);
//			res = EncodingUtils.getString(buffer, "UTF-8");
//			fin.close();
//			buffer = null;
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(context, "加载错误。。。", 0).show();
			System.out.print("读取数据失败");
		}
		
		return res;
	}
	
	/**
	 * 根据一个绝对路径将字符串写入一个文本文件
	 * @param fileName 一个文件的绝对路径 
	 * @param message 要写入的字符串内容
	 */
	public static void writeTxtFileSdcard(String fileName, String message, String charSet) {
		if(message==null||message.length()==0){return;}
		File f = new File(fileName);
		if(f.exists()){//
			final File to = new File(f.getAbsolutePath()+System.currentTimeMillis());
			f.renameTo(to);
			boolean folder = to.delete();
			System.out.println(f.toString()+" delete?"+folder);
		}
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		try {
			FileOutputStream fout = new FileOutputStream(f);
			if(charSet==null || charSet.length()==0){
				charSet = "UTF-8";
			}
			OutputStreamWriter osw = new OutputStreamWriter(fout, charSet);//UTF-8
			osw.write(message);
			osw.close();
//			byte[] bytes = message.getBytes();
//			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 得到URL中最后一个'/'后面的最后一个字符串，如果为null,则倒数第二个'/'
	 * @param url
	 * @return
	 */
	public static String getLastString(String url){
		if(url==null || url.length()==0){
			return System.currentTimeMillis()+"";
		}
		
		int index = url.lastIndexOf("/");
		if (index == url.length()-1) {
			url=url.substring(0, url.length()-1);
		}
		index = url.lastIndexOf("/");
		return url.substring(index+1);
	}
	
	//
	public static void writeFileAppend(String fileName, String message/*, String charSet*/) {
		File file = new File(fileName);
		try {
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(file.exists()){
			try {
				RandomAccessFile raf = new RandomAccessFile(file, "rwd");					
				raf.seek(file.length());
				raf.write(message.getBytes());		
				raf.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	/**
     * 从url中提取文件名
     * @param url 包含文件名的url
     * @return 返回文件名字符串
     */
    public static String getFileNamefromUrl(String url){
    	if(url == null){
			return null;
		}

		String fileName = "";
		int lastSlash = 0;// 最后一个"/"在字符串中的位置
		for(int i=0;i<url.length();i++){
			char c = url.charAt(i);
			int ic = (int)c;
			if(ic == 47){// 如果是"/"则记住它的位置
				lastSlash = i;
			}
		}
		// 截取url中最后一个"/"之后的字符串作为文件名
		fileName = url.substring(lastSlash + 1, url.length());
		return fileName;
    }
    /**
     * 从url中提取文件名
     * @param url 包含文件名的url
     * @return 返回文件名字符串
     */
    public static String getFileNamefromUrlPlus(String url){
    	if(url == null){
			return null;
		}

		String fileName = "";
		int lastSlash = url.lastIndexOf("/");// 最后一个"/"在字符串中的位置
		// 截取url中最后一个"/"之后的字符串作为文件名
		fileName = url.substring(lastSlash + 1, url.length());
		return fileName;
    }
    
    /**
     * 从windows风格路径中提取文件名
     * @param 包含文件名的路径 e.g. D:\\ok.jpg
     * @return 返回文件名字符串  e.g.ok.jpg
     */
    public static String getFileNamefromLocalPath(String url){
    	if(url == null){
			return null;
		}

		String fileName = "";
		int lastSlash = 0;// 最后一个"\"在字符串中的位置
		for(int i=0;i<url.length();i++){
			char c = url.charAt(i);
			int ic = (int)c;
			if(ic == '\\'){// 如果是"\"则记住它的位置
				lastSlash = i;
			}
		}
		// 截取url中最后一个"\"之后的字符串作为文件名
		fileName = url.substring(lastSlash + 1, url.length());
		return fileName;
    }
    
    public static String getFileMD5(String path) {
		if (path==null || path.trim().length()==0) {
			return "";
		}
		String md5 = "";
		InputStream is1=null;
		try {			
			is1 = new FileInputStream(path);
			int length = is1.available();
			byte[] bytes = new byte[length];
			is1.read(bytes, 0, length);
			md5 = MD5Util.getMD5String(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is1!=null) {
				try {
					is1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return md5;
	}
}

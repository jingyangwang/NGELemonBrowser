package io.github.mthli.Ninja.Ad;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import io.github.mthli.Ninja.Unit.DLog;

/**
 * 采用HttpURLConnection进行网络交互，改工具包含文件发送、cookie的维护、支持gzip,deflate传输格式功能
 */
public class CustomHttpClient {
	public int connTimeOut = 20000;
	public int readTimeOut = 20000;
	public boolean sendCookie = true;
	public static final String UTF8 = "utf-8";

	private CustomHttpClient() {
	}

	private CustomHttpClient(int connTimeOut, int readTimeOut) {
		this.connTimeOut = connTimeOut;
		this.readTimeOut = readTimeOut;
	}

	public static CustomHttpClient getInstance(int connTimeOut, int readTimeOut) {
		return new CustomHttpClient(connTimeOut, readTimeOut);
	}

	public static CustomHttpClient getInstance() {
		return new CustomHttpClient();
	}

	/**
	 * 
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能:
	 * 
	 * <FORM METHOD=POST ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param actionUrl
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www
	 *            .cnblogs.com/guoshiandroid或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 * @throws Exception
	 * @throws IOException
	 */
	public String post(Context context, String actionUrl, String encoding, Map<String, String> params) throws Exception {
		DataOutputStream outStream = null;
		HttpURLConnection conn = null;
		BufferedReader brout = null;
		try {
			String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data";
			conn = NetworkUtil.getURLConnection(actionUrl, context);
			conn.setConnectTimeout(connTimeOut);
			conn.setReadTimeout(readTimeOut);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			//			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Charset", encoding);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			if (params != null)
				for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
					sb.append(entry.getValue());
					sb.append("\r\n");
				}
			CookieSyncManager csm = CookieSyncManager.createInstance(context.getApplicationContext());
			CookieManager cookieManager = CookieManager.getInstance();
			csm.sync();
			String auth = cookieManager.getCookie(actionUrl);
			if (sendCookie) {
				DLog.i("debug", "发送cookie--->" + auth);
				if (!TextUtils.isEmpty(auth))
					conn.setRequestProperty("Cookie", auth);
			}
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			int code = conn.getResponseCode();
			if (code >= 400)
				throw new Exception("服务器异常");
			if(code >= 300 && code <400){
				
				String location = conn.getHeaderField("Location");  
				try{
				if (brout != null)
					brout.close();
				if (outStream != null)
					outStream.close();
				if (conn != null)
					conn.disconnect();
				}catch (Exception e) {
				}
			return	post(context, location, encoding, null);
			}else{
				String contentEncoding = conn.getContentEncoding();
				InputStream is = null;
				if ((null != contentEncoding) && (-1 != contentEncoding.indexOf("gzip"))) {
					is = new GZIPInputStream(conn.getInputStream());
				} else if ((null != contentEncoding) && (-1 != contentEncoding.indexOf("deflate"))) {
					is = new InflaterInputStream(conn.getInputStream());
				} else {
					is = conn.getInputStream();
				}

				InputStreamReader isrout = new InputStreamReader(is);
				brout = new BufferedReader(isrout, 1024);
				char[] ch = new char[1024];
				StringBuilder b = new StringBuilder();
				int length = 0;
				while ((length = brout.read(ch)) != -1) {
					b.append(ch, 0, length);
				}

				getHttpResponseHeader(conn);// 里面包含获取cookie的功能
				if (cookies.size() > 0) {
					for (int i = 0; i < cookies.size(); i++) {
						cookieManager.setCookie(actionUrl, cookies.get(i));
						CookieSyncManager.getInstance().sync();
						DLog.i("debug", "接受cookie" + i + "--->" + cookies.get(i));
					}
				} else {
					DLog.i("debug", "没有cookie");
				}
				return b.toString();
			}
		}
		finally {
			if (brout != null)
				brout.close();
			if (outStream != null)
				outStream.close();
			if (conn != null)
				conn.disconnect();
		}

	}
	
	

	/**
	 * 
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能:
	 * 
	 * <FORM METHOD=POST ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param actionUrl
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www
	 *            .cnblogs.com/guoshiandroid或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 * @throws Exception
	 * @throws IOException
	 */
	public String post_java(Context context, String actionUrl, String encoding, Map<String, String> params) throws Exception {
		DataOutputStream outStream = null;
		HttpURLConnection conn = null;
		BufferedReader brout = null;
		try {
			String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data";
			conn = NetworkUtil.getURLConnection(actionUrl, context);
			conn.setConnectTimeout(connTimeOut);
			conn.setReadTimeout(readTimeOut);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			//			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Charset", encoding);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			if (params != null)
				for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
//					sb.append("--");
//					sb.append(BOUNDARY);
//					sb.append("\r\n");
//					sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
					sb.append(entry.getValue());
//					sb.append("\r\n");
				}
			//cookie资源
			CookieSyncManager csm = CookieSyncManager.createInstance(context.getApplicationContext());
			CookieManager cookieManager = CookieManager.getInstance();
			csm.sync();
			String auth = cookieManager.getCookie(actionUrl);
			if (sendCookie) {
				DLog.i("debug", "发送cookie--->" + auth);
				if (!TextUtils.isEmpty(auth))
					conn.setRequestProperty("Cookie", auth);
			}
			//直接写过去
			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据
//			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
//			outStream.write(end_data);
			outStream.flush();
			int code = conn.getResponseCode();
			DLog.i("debug", "code: " + code);
			if (code >= 400)
				throw new Exception("服务器异常");
			if(code >= 300 && code <400){
				
				String location = conn.getHeaderField("Location");  
				try{
				if (brout != null)
					brout.close();
				if (outStream != null)
					outStream.close();
				if (conn != null)
					conn.disconnect();
				}catch (Exception e) {
				}
			return	post(context, location, encoding, null);
			}else{
				//获取资源
				String contentEncoding = conn.getContentEncoding();
				InputStream is = null;
				//压缩文件流
				if ((null != contentEncoding) && (-1 != contentEncoding.indexOf("gzip"))) {
					is = new GZIPInputStream(conn.getInputStream());
				} else if ((null != contentEncoding) && (-1 != contentEncoding.indexOf("deflate"))) {
					is = new InflaterInputStream(conn.getInputStream());
				} else {
					is = conn.getInputStream();
				}
				//从中读取数据
				InputStreamReader isrout = new InputStreamReader(is);
				brout = new BufferedReader(isrout, 1024);
				char[] ch = new char[1024];
				StringBuilder b = new StringBuilder();
				int length = 0;
				while ((length = brout.read(ch)) != -1) {
					b.append(ch, 0, length);
				}

				getHttpResponseHeader(conn);// 里面包含获取cookie的功能
				if (cookies.size() > 0) {
					for (int i = 0; i < cookies.size(); i++) {
						cookieManager.setCookie(actionUrl, cookies.get(i));
						CookieSyncManager.getInstance().sync();
						DLog.i("debug", "接受cookie" + i + "--->" + cookies.get(i));
					}
				} else {
					DLog.i("debug", "没有cookie");
				}
				//System.out.println("b:"+b.toString());
				return b.toString();
			}
		}
		finally {
			if (brout != null)
				brout.close();
			if (outStream != null)
				outStream.close();
			if (conn != null)
				conn.disconnect();
		}

	}

	private List<String> cookies = new ArrayList<String>();// 接受回来的cookie

	/**
	 * 获取Http响应头字段
	 * 
	 * @param http
	 * @return
	 */
	public Map<String, String> getHttpResponseHeader(HttpURLConnection http) {
		Map<String, String> header = new LinkedHashMap<String, String>();
		for (int i = 0;; i++) {
			String mine = http.getHeaderField(i);
			if (mine == null)
				break;
			header.put(http.getHeaderFieldKey(i), mine);
			if ("set-cookie".equalsIgnoreCase(http.getHeaderFieldKey(i))) {
				cookies.add(mine);
			}
		}
		return header;
	}
}
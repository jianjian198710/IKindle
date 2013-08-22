package Html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
public class HtmlCatcher {
	private static boolean useProxy = false;
	private static DefaultHttpClient httpClient;
	
	public static DefaultHttpClient getHttpClientInstance(){
		if(httpClient == null){
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}
	
	public static String catchHtml(String url){
		StringBuffer html = new StringBuffer();
		//创建HttpClient对象
		httpClient = getHttpClientInstance();
		//使用Ericsson Proxy
		if(isUseProxy()==true){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("www-proxy.ao.ericsson.se", 8080, "http"));
		}
		//以Get方式请求URL
		HttpGet httpGet = new HttpGet(url);
         
//        List <NameValuePair> formparams = new ArrayList<NameValuePair>();
//        formparams.add(new BasicNameValuePair("username", "jianjian198710@hotmail.com"));
//        formparams.add(new BasicNameValuePair("password", "198710"));
//        HttpPost httpPost = new HttpPost(url); 
//        httpPost.setEntity(new UrlEncodedFormEntity(formparams, Consts.UTF_8)); 
//        try {
//			System.out.println(EntityUtils.toString(httpPost.getEntity()));
//		} catch (ParseException | IOException e) {
//			e.printStackTrace();
//		}
//        
		try{
//			HttpResponse response = httpClient.execute(httpPost);
			HttpResponse response = httpClient.execute(httpGet);
			//获取状态码
			int resStatus = response.getStatusLine().getStatusCode();
			System.out.println(resStatus);
			//200 ok
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				if(entity!=null){
					try(BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()))){
						String s = null;
						while((s=br.readLine())!=null){
							html.append(s);
						}
					}catch(IOException ioe){
						ioe.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			System.out.println("访问"+url+"出错");
		}finally {  
	        httpClient.getConnectionManager().shutdown();  
	    }
		return html.toString();
	}
	
	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		HtmlCatcher.useProxy = useProxy;
	}

	public static void main(String[] args){
		setUseProxy(false);
		System.out.println(catchHtml("http://ikandou.com/accounts/login/?next=/g/"));
	}
}

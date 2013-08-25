package Html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
public class HtmlCatcher {
	private static boolean useProxy = false;
	private static DefaultHttpClient httpClient;
	
	public static DefaultHttpClient getHttpClientInstance(){
		if(httpClient == null){
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}
	
	public static String catchHtmlFirstGET(String url){
		StringBuffer html = new StringBuffer();
		//����HttpClient����
		httpClient = getHttpClientInstance();
		HttpGet httpGet = new HttpGet(url);
		if(isUseProxy()==true){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("www-proxy.ao.ericsson.se", 8080, "http"));
		}
		try{
			HttpResponse response = httpClient.execute(httpGet);
			//��ȡ״̬��
			int resStatus = response.getStatusLine().getStatusCode();
			System.out.println("����״̬��: "+resStatus);
			//200 ok
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				if(entity!=null){
					try(BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(),Charset.forName("UTF-8")))){
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
			System.out.println("����"+url+"����");
		} finally{
			httpGet.releaseConnection();
		}
		return html.toString();
	}
	
	public static String catchHtmlGET(String url){
		catchHtmlFirstPOST("http://ikandou.com/accounts/login/?next=/g/");
		StringBuffer html = new StringBuffer();
		//����HttpClient����
		httpClient = getHttpClientInstance();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Referer", "http://www.ikandou.com/accounts/login/?next=/g/");
		if(isUseProxy()==true){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("www-proxy.ao.ericsson.se", 8080, "http"));
		}
		try{
			HttpResponse response = httpClient.execute(httpGet);
			//��ȡ״̬��
			int resStatus = response.getStatusLine().getStatusCode();
			System.out.println("����״̬��: "+resStatus);
			//200 ok
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				if(entity!=null){
					try(BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(),Charset.forName("UTF-8")))){
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
			System.out.println("����"+url+"����");
		}
		return html.toString();
	}
	
	public static void catchHtmlFirstPOST(String url){
		StringBuffer html = new StringBuffer();
		//����HttpClient����
		httpClient = getHttpClientInstance();
		HttpPost httpPost = new HttpPost(url); 
//		HttpGet httpGet = new HttpGet("http://www.ikandou.com/g/popular/");
		//ʹ��Ericsson Proxy
		if(isUseProxy()==true){
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("www-proxy.ao.ericsson.se", 8080, "http"));
		}
		//��ȡCSRF ���IKanDou
		String csrf = HtmlParser.parseCSRF(catchHtmlFirstGET("http://ikandou.com/accounts/login/?next=/g/"));
		//д��Post�����Ĳ���
        List <NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("csrfmiddlewaretoken",csrf));
        formparams.add(new BasicNameValuePair("email", "jianjian198710@hotmail.com"));
        formparams.add(new BasicNameValuePair("password", "198710"));
        formparams.add(new BasicNameValuePair("remember_me","on"));
        httpPost.setEntity(new UrlEncodedFormEntity(formparams, Consts.UTF_8));  
        
		try{
			HttpResponse response = httpClient.execute(httpPost);
			System.out.println("Login On Successfully!");
			int resStatus = response.getStatusLine().getStatusCode();
			System.out.println("����״̬��: "+resStatus);
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				if(entity!=null){
					try(BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent(),Charset.forName("UTF-8")))){
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
			System.out.println("����"+url+"����");
		} finally{
			httpPost.releaseConnection();
			System.out.println(html.toString());
		}
	}
	
	
	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		HtmlCatcher.useProxy = useProxy;
	}

	public static void main(String[] args){
		setUseProxy(false);
//		String s = catchHtmlGET2("http://www.ikandou.com/g/download/ebook/inner/15162710/epub/");
//		HtmlCatcher.loginOn("http://www.ikandou.com/accounts/login/?next=/g/popular/");
		System.out.println(catchHtmlGET("http://www.ikandou.com/oldpopular/"));
//		System.out.println("!!!"+s);
	}
}

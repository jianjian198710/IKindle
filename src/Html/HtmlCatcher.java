package Html;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
public class HtmlCatcher {
	private static boolean useProxy;
	
	public static String catchHtml(String url){
		StringBuffer html = new StringBuffer();
		//����HttpClient����
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost("www-proxy.ao.ericsson.se", 8080, "http"));

		//��Get��ʽ����URL
		HttpGet httpGet = new HttpGet(url);
		try{
			HttpResponse response = httpClient.execute(httpGet);
			//��ȡ״̬��
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
			System.out.println("����"+url+"����");
		}finally {  
	        httpClient.getConnectionManager().shutdown();  
	    }
		return html.toString();
	}
	
	public static void main(String[] args){
		System.out.println(catchHtml("http://www.baidu.com"));
	}
}

package Download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import Html.HtmlCatcher;

public class HttpDownLoader {
	int i = 2;
	public void downLoad(String Url,String targetFile){
		HttpClient httpclient = HtmlCatcher.getHttpClientInstance();
		HttpGet httpGet = new HttpGet(Url);
		long length = 0;
		try{
			HttpResponse response = httpclient.execute(httpGet);
			int resStatus = response.getStatusLine().getStatusCode();
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				System.out.println("ContentType: "+entity.getContentType());
				if(entity!=null&&entity.isStreaming()){
					File file = new File(targetFile);
					try(FileOutputStream fos = new FileOutputStream(file);
					    InputStream is = entity.getContent()){
						System.out.println(targetFile+" ContentLength: "+entity.getContentLength());
						byte[] b = new byte[2048];
						int hasRead = 0;
						while((hasRead=is.read(b))!=-1){
							fos.write(b, 0, hasRead);
						}
						fos.flush();
						System.out.println(targetFile+"下载完成!!!");
					}catch(IOException e){
						System.out.println(targetFile+"下载出错!!!");
					}
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public File createFile(String filename) throws IOException{
		File file = new File(filename);
		if(file.exists()){
			int dot = filename.lastIndexOf(".");
			String format = filename.substring(dot);
			String name = filename.substring(0,dot)+"I";
			return createFile(name+format);
		}else{
			file.createNewFile();
			System.out.println(file.getName());
			return file;
		}
	}
	
	public static void main(String[] args) throws InterruptedException, IOException{
//		HtmlCatcher.setUseProxy(true);
//		HtmlCatcher.loginOn();
//		String s = "http://ikandou.com/oldbook/download/inner/71383848893";
//		String filename ="a.zip";
//		new HttpDownLoader().downLoad(s, filename);
		HttpDownLoader h = new HttpDownLoader();
		for(int i=0;i<3;i++){
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(h.createFile("尼玛.txt")));
			osw.write("aaa");
			osw.flush();
			osw.close();
		}
	}
}




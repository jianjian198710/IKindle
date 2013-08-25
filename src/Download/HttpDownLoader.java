package Download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import Html.HtmlCatcher;

public class HttpDownLoader {
	
	public long downLoad(String path,String targetFile){
		HttpClient httpclient = HtmlCatcher.getHttpClientInstance();
		HttpGet httpGet = new HttpGet(path);
		long length = 0;
		try{
			HttpResponse response = httpclient.execute(httpGet);
			int resStatus = response.getStatusLine().getStatusCode();
			System.out.println("×´Ì¬Âë: "+resStatus);
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				System.out.println("ContentType: "+entity.getContentType());
				if(entity!=null){
					File file = new File(targetFile);
					try(FileOutputStream fos = new FileOutputStream(file);
					    InputStream is = entity.getContent()){
						System.out.println("ContentLength: "+entity.getContentLength());
						byte[] b = new byte[2048];
						int hasRead = 0;
						while((hasRead=is.read(b))!=-1){
							fos.write(b, 0, hasRead);
							fos.flush();
						}
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}return length;
	}
	
	public static void main(String[] args) throws InterruptedException{
		String s1 ="http://www.crazyit.org/attachment.php?aid=MzMzNnxiZDQyYmRmOXwxMzc3NDA3Njk5fGFmZWQ3c0RGanhKM2srdGd5cSs2YU91MnYyeFVjVWhCK240ZTRWdm56NDE0Rk1F,";
		String s2 ="http://www.crazyit.org/attachment.php?aid=MzMzN3w4NDcyMTRmOHwxMzc3NDA3Njk5fGFmZWQ3c0RGanhKM2srdGd5cSs2YU91MnYyeFVjVWhCK240ZTRWdm56NDE0Rk1F,";
		String target = "lib.zip";
		String target2 = "lib2.zip";
		File file = new File(target);
		File file2 = new File(target2);
		//ContentLength: 6715479
		
		HttpDownLoader loader = new HttpDownLoader(); 
		long l1 = loader.downLoad(s1, target);
		System.out.println(file.length()==6715479);
//		System.out.println("l1==file1?: "+(l1==file.length()));
//		if(l1==file.length()){
//			long l2 = loader.downLoad(s2, target2);
//			System.out.println("l2==file2?: "+(l2==file2.length()));
//		}
	}
}




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

public class MultiThreadDownLoad {
	public void downLoad(String path,String targetFile,int threadNum){
		HttpClient httpclient = HtmlCatcher.getHttpClientInstance();
		HttpGet httpGet = new HttpGet(path);

		InputStream is = null;
		RandomAccessFile currentPart = null; 
		try{
			HttpResponse response = httpclient.execute(httpGet);
			int resStatus = response.getStatusLine().getStatusCode();
			System.out.println("×´Ì¬Âë: "+resStatus);
			if(resStatus==HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				System.out.println("ContentType: "+entity.getContentType());
				if(entity!=null){
					File file = new File(targetFile);
					try{
						RandomAccessFile raf = new RandomAccessFile(file,"rw");
						is = entity.getContent();
						long fileSize = entity.getContentLength();
						System.out.println("fileSize: "+fileSize);
						raf.setLength(fileSize);
						raf.close();
						int currentPartSize = (int)fileSize/threadNum+1;
						for(int i=0;i<threadNum;i++){
							int startPos = i*currentPartSize;
							currentPart = new RandomAccessFile(file,"rw");
							if(startPos<=fileSize){
								currentPart.seek(startPos);
								Thread t = new DownloadThread(currentPart, is, startPos, currentPartSize);
								System.out.println(t.getName());
								t.start();
							}
						}
					}catch(IOException e){
						e.printStackTrace();
					}
//					finally{
//						if(is!=null){
//							try{
//								is.close();
//							}catch(IOException ioe){
//								ioe.printStackTrace();
//							}
//						}if(currentPart!=null){
//							try{
//								currentPart.close();
//							}catch(IOException ioe){
//								ioe.printStackTrace();
//							}
//						}
//					}
				}
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		String s2 ="http://www.crazyit.org/attachment.php?aid=MzMzNnxiZDQyYmRmOXwxMzc3NDA3Njk5fGFmZWQ3c0RGanhKM2srdGd5cSs2YU91MnYyeFVjVWhCK240ZTRWdm56NDE0Rk1F,";
		String target = "lib1.zip";
		new MultiThreadDownLoad().downLoad(s2, target, 4);
	}
}



class DownloadThread extends Thread{
	private InputStream is;
	private RandomAccessFile currentPart;
	private int startPos;
	private int currentPartSize;
	public int length;
	
	public DownloadThread(RandomAccessFile currentPart, InputStream is, int startPos,
			int currentPartSize) {
		super();
		this.is = is;
		this.startPos = startPos;
		this.currentPartSize = currentPartSize;
		this.currentPart = currentPart;
	}

	@Override 
	public void run(){
		try {
			is.skip(startPos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buffer = new byte[1024*1024];
		int hasRead = 0;
		try {
			while(length<currentPartSize&&(hasRead=is.read(buffer))!=-1){
				System.out.println(this.getName()+"hasRead: "+hasRead);
				currentPart.write(buffer,0,hasRead);
				length+=hasRead;
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
//		finally{
//			if(is!=null){
//				try{
//					is.close();
//				}catch(IOException e){
//					
//				}
//			}if(currentPart!=null){
//				try{
//					currentPart.close();
//				}catch(IOException e){
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
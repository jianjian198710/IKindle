package Download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class DailyDownLoader {
	
	private HttpDownLoader httpDownloader;
	
	public DailyDownLoader(HttpDownLoader httpDownloader) {
		super();
		this.httpDownloader = httpDownloader;
	}

	//url��ʾ���쿪ʼ���صĵ�ַ
	public void dailyDL(int num){
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("IKanDouSort.properties"),Charset.forName("UTF-8")))){
			String s = null;
			while((s=br.readLine())!=null){
				if(s.startsWith(String.valueOf(num-1)+".")){
					for(int i=0;i<10;i++){
						String s1 = br.readLine();
						String s2 = s1.replace("\\", "");
						int equals = s2.indexOf("=");
						int dot = s2.indexOf(".");
						String name = s2.substring(equals+1);
						String url = s2.substring(dot+1,equals);
						String filename = "H:/Ebooks/"+name+".zip";
						getHttpDownloader().downLoad(url, filename);
						System.out.println(num+"."+url+"--->"+name+"�������!!!");
						num++;
					}
				}
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	
	public HttpDownLoader getHttpDownloader() {
		return httpDownloader;
	}

	public static void main(String[] args) {
//		int i = 1;
//		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("IKanDouCopy.properties"),Charset.forName("UTF-8")));
//				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("IKanDouSort.properties"),Charset.forName("UTF-8")))){
//			String s = null;
//			while((s=br.readLine())!=null){
//				if(!s.startsWith("#")){
//					bw.write(String.valueOf(i)+"."+s+"\n");
//					i++;
//				}
//			}
//			bw.flush();
//		}catch(IOException ioe){
//			ioe.printStackTrace();
//		}
		new DailyDownLoader().dailyDL(2);
	}

}

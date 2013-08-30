package Download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Html.HtmlCatcher;

public class DailyDownLoader {
	
	private static final String LEN = "^\\d+\\.([^=]{1,})=(.*)$";
	
	private HttpDownLoader httpDownloader;
	
	public DailyDownLoader(HttpDownLoader httpDownloader) {
		super();
		this.httpDownloader = httpDownloader;
	}
	//处理IKanDou下载List文件,加序号
	public void processDownLoadList(){
		int i = 1;
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("IKanDou.txt")));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("IKanDouSort.txt")))){
			String s = null;
			while((s=br.readLine())!=null){
				bw.write(String.valueOf(i)+"."+s+"\n");
				i++;
			}
			bw.flush();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	//url表示今天开始下载的地址
	public void dailyDL(final int num) throws IOException{
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("IKanDouSort.properties"),Charset.forName("UTF-8")))){
			Pattern pattern = Pattern.compile(LEN);
			for(int i=0;i<num-1;i++){
				br.readLine();
			}
			for(String s=br.readLine();s!=null;s=br.readLine()){
				Matcher m = pattern.matcher(s);
				while(m.find()){
					String url = m.group(1);
					String name = m.group(2);
					String filename = "H:/Ebooks/"+name;
					getHttpDownloader().downLoad(url, filename);
				}
			}
		}
	}
	
	public HttpDownLoader getHttpDownloader() {
		return httpDownloader;
	}

	public static void main(String[] args) throws IOException {
		/*一把搞定
		 * HtmlCatcher.setUseProxy(false);
		HtmlParser parser = new HtmlParser();
		parser.getAllPoplularBooks();
		HttpDownLoader httpDownLoader = new HttpDownLoader();
		DailyDownLoader dailyDownLoader = new DailyDownLoader(httpDownLoader);
		dailyDownLoader.processDownLoadList();
		dailyDownLoader.dailyDL(1);*/
		
		HtmlCatcher.setUseProxy(true);
		HtmlCatcher.loginOn();
		HttpDownLoader httpDownLoader = new HttpDownLoader();
		DailyDownLoader dailyDownLoader = new DailyDownLoader(httpDownLoader);
		dailyDownLoader.dailyDL(1);
	}

}

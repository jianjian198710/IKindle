package Html;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* 
 * CSRF Exp: <input type='hidden' name='csrfmiddlewaretoken' value='66ad078b7d56df869c9c079e776b3c01' />
 * G Page URL Exp: http://ikandou.com/g/popular/?page=2
 * G Book: <a href="/g/detail/11896372/">******
 * G PageNumber: <a href="?page=37" class="page">37</a>
 * OldDownloadURL Exp: http://ikandou.com/g/download/ebook/inner/716371611/epub/
 * PopularDownloadURL Exp: http://ikandou.com/oldbook/download/inner/3135985124
 */
public class HtmlParser {
	
	int pageCounter;
	public static final String DOMAIN ="http://ikandou.com"; 
	public static final String DOWNLOADPRE = "http://ikandou.com/oldbook/download/inner/";
	
	private static final String CSRF = "name=[\']csrfmiddlewaretoken[\'] value=[\']\\w+[\']"; 
	private static final String PAGENUMBER = "<a href=[\"][\\?]page=\\d+[\"] class=\"page\">\\d+</a>"; 
	private static final String OLDBOOK = "<a href=[\"]/g/detail/\\d+/[\"]>[\\S&&[^<]]{1,}"; 
	private static final String POPBOOK = "<a href=[\"]/oldbook/\\d+\\?\\w+=\\w+[\"]>\\s+<img src=[\"]http://img\\d+\\.\\w+\\.com/"+
			"((pics)||(mpic))/((\\w+)||(\\w+\\-\\w+\\-\\w+))\\.((gif)||(jpg))[\"] />"+
			"\\s+<div \\w+=[\"]\\w*[\"]>\\s+<span \\w+=[\"]\\w+[\"]>[^<]{1,}"; 
	private static final String POPBOOKCODE = "<a class=[\"]((minibutton )||(minibutton inactive))[\"][^<]{1,}</a><[^<]{1,}"; 
	
	//解析页面的CSRF
	public static String parseCSRF(String html){
		//CSRF Exp: <input type='hidden' name='csrfmiddlewaretoken' value='66ad078b7d56df869c9c079e776b3c01' />
		Pattern pattern = Pattern.compile(CSRF);
		Matcher m = pattern.matcher(html);
		//只需取一次
		if(m.find()){
			String csrfHtml = m.group();
			System.out.println("CSRF with Html: "+csrfHtml);
			int start = csrfHtml.indexOf("value");
			int end = csrfHtml.lastIndexOf("\'");
			//截取CSRF的值
			String csrf = csrfHtml.substring(start+7,end);
			System.out.println("CSRF Value is: "+csrf);
			return csrf;
		}
		return null;
	}
	
	public int parseMaxPage(String html){
		//G PageNumber: <a href="?page=37" class="page">37</a>
		Pattern pattern = Pattern.compile(PAGENUMBER);
		Matcher m = pattern.matcher(html);
		TreeSet<Integer> pages = new TreeSet<Integer>();
		while(m.find()){
			String pageHtml = m.group();
			int pageStart = pageHtml.lastIndexOf("\"")+2;
			int pageEnd = pageHtml.lastIndexOf("<");
			String page = pageHtml.substring(pageStart,pageEnd);
			pages.add(Integer.parseInt(page));
		}
		System.out.println("最大页数: "+pages.last());
		return pages.last();
	}
	
	public void parseOldBooks(String html){
		//G Book: <a href="/g/detail/11896372/">******
		Pattern pattern = Pattern.compile(OLDBOOK);
		Matcher m = pattern.matcher(html);
		while(m.find()){
			//获取书的Id
			String tmp = m.group();
			int idStart = tmp.indexOf("l")+2;
			int idEnd = tmp.lastIndexOf(">")-2;
			String id = tmp.substring(idStart,idEnd);
			//获取书名
			int nameStart = tmp.indexOf(">")+1;
			String name = tmp.substring(nameStart);
//			oldbooks.put(id, name);
		}
	}
	
	public void parsePopularBooks(String html)throws IOException{
		Pattern pattern = Pattern.compile(POPBOOK);
		Matcher m = pattern.matcher(html);
		while(m.find()){
			String tmp = m.group();
			//整理格式取出多余空格,显示'号
			String tmp2 = tmp.replaceAll(">\\s+", ">");
			String tmp3 = tmp2.replaceAll("&#39;", "'");
//			System.out.println("书的信息："+tmp3);
			//获取id
			int idStart = tmp3.indexOf("oldbook")+8;
			int idEnd = tmp3.indexOf("?");
			String id = tmp3.substring(idStart,idEnd);
//			System.out.println("id: "+id);
			//获取name
			int nameStart = tmp3.lastIndexOf(">")+1;
			String name = tmp3.substring(nameStart).trim();
			//获取BookDetailURL
			int detailURLStart = tmp3.indexOf("/");
			int detailURLEnd = tmp3.indexOf(">")-1;
			//获取书本DetailURL
			String detailURL = DOMAIN+tmp3.substring(detailURLStart,detailURLEnd);
//			System.out.println("detailURL: "+detailURL);
			
			String detailPage = HtmlCatcher.catchHtmlGET(detailURL);
			//取得下载码
			String downloadCodeAndFormat = parsePopularDownloadCode(detailPage);
			int star = downloadCodeAndFormat.lastIndexOf("*");
			String downloadCode = downloadCodeAndFormat.substring(0,star);
			String format = downloadCodeAndFormat.substring(star+1);
//			System.out.println("DownloadCode: "+downloadCode);
			//获取下载URL
			String downloadURL = DOWNLOADPRE+downloadCode;
			String value = name+"."+format;
			System.out.println("书名: "+name+"."+format+", DOWNLOAD: "+downloadURL);
			try(OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("IKanDou.txt",true))){
				osw.write(downloadURL+"="+value+"\n");
			}
		}
//		System.out.println(props.toString());
	}
	
	public String parsePopularDownloadCode(String detailPage){
//<li class=\"download\"><a class=\"minibutton \" href=\"/oldbook/download/1277631488\" target=\"_blank\">下载 pdf</a><span class=\"count\">22</span></li>
		Pattern pattern = Pattern.compile(POPBOOKCODE);
		Matcher m = pattern.matcher(detailPage);
		String popularDownloadCode = null;
		String popularFormat = null;
		int maxCounter = 0;
		while(m.find()){
			String tmp = m.group();
			//获取downloadCode
			int downloadCodeStart = tmp.indexOf("download")+9;
			String tmp2 = tmp.substring(downloadCodeStart);
			int downloadCodeEnd = tmp2.indexOf("\"");
			String downloadCode = tmp2.substring(0, downloadCodeEnd);
			//获取格式
			int formatBegin = tmp.indexOf("载")+2;
			int formatEnd = tmp.indexOf("</a>");
			String format = tmp.substring(formatBegin,formatEnd).trim().toLowerCase();
			
			//获取最大下载数
			int counterStart = tmp.lastIndexOf(">")+1;
			int counter = Integer.parseInt(tmp.substring(counterStart));
			if(counter>maxCounter){
				maxCounter = counter;
				popularDownloadCode = downloadCode;
				popularFormat = format;
			}
		}
		return popularDownloadCode+"*"+popularFormat;
	}
	
	public void getAllGBooks() throws IOException{
		HtmlCatcher.loginOn();
		//第二页才有显示最大页数
		int maxPage = parseMaxPage(HtmlCatcher.catchHtmlGET("http://www.ikandou.com/g/popular/?page=2"));
		for(int i=1;i<=maxPage;i++){
			String url = "http://www.ikandou.com/g/popular/?page="+i;
			String html = HtmlCatcher.catchHtmlGET(url);
			parseOldBooks(html);
		}
	}
	
	public void getAllPoplularBooks() throws IOException{
		HtmlCatcher.loginOn();
		int maxPage = parseMaxPage(HtmlCatcher.catchHtmlGET("http://ikandou.com/oldpopular/"));
		for(int i=1;i<=maxPage;i++){
			System.out.println("当前页: "+i);
			String url = "http://ikandou.com/oldpopular/?page="+i;
			String html = HtmlCatcher.catchHtmlGET(url);
			parsePopularBooks(html);
		}
//		String url = "http://ikandou.com/oldpopular/?page=2";
//		String html = HtmlCatcher.catchHtmlGET(url);
//		parsePopularBooks(html);
//		System.out.println("书的总数: "+props.size());
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		HtmlCatcher.setUseProxy(false);
		HtmlParser parser = new HtmlParser();
		parser.getAllPoplularBooks();
//		new HtmlParser().getAllGBooks();
//		parser.getAllPoplularBooks();
//		System.out.println(popularbooks.size());
		
//		String s = HtmlCatcher.catchHtmlGET("http://ikandou.com/oldbook/10953?sortby=popular");
//		System.out.println(parser.parsePopularDownloadCode(s));

	}
}

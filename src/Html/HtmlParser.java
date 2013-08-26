package Html;

import java.util.HashMap;
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
	static HashMap<String,String> oldbooks = new HashMap<String,String>();
	static HashMap<String,String> popularbooks = new HashMap<String,String>();
	
	//解析页面的CSRF
	public static String parseCSRF(String html){
		//CSRF Exp: <input type='hidden' name='csrfmiddlewaretoken' value='66ad078b7d56df869c9c079e776b3c01' />
		Pattern pattern = Pattern.compile("name=[\']csrfmiddlewaretoken[\'] value=[\']\\w+[\']");
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
		Pattern pattern = Pattern.compile("<a href=[\"][\\?]page=\\d+[\"] class=\"page\">\\d+</a>");
		Matcher m = pattern.matcher(html);
		TreeSet<Integer> pages = new TreeSet<Integer>();
		while(m.find()){
			String pageHtml = m.group();
			int pageStart = pageHtml.lastIndexOf("\"")+2;
			int pageEnd = pageHtml.lastIndexOf("<");
			String page = pageHtml.substring(pageStart,pageEnd);
			pages.add(Integer.parseInt(page));
		}
		System.out.println("古籍最大页数: "+pages.last());
		return pages.last();
	}
	
	public void parseOldBooks(String html){
		//G Book: <a href="/g/detail/11896372/">******
		Pattern pattern = Pattern.compile("<a href=[\"]/g/detail/\\d+/[\"]>[\\S&&[^<]]{1,}");
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
			oldbooks.put(id, name);
		}
	}
	
	public void parsePopularBooks(String html){
		//蛋疼的Regx = =!
//		Pattern pattern = Pattern.compile("<a href=[\"]/oldbook/\\d+\\?sortby=popular[\"]>\\s+<img src=[\"]http://img3\\.douban\\.com/mpic/\\w+\\.jpg[\"] />"+
//										"\\s+<div class=[\"]\\w*[\"]>\\s+<span class=[\"]title[\"]>[^<]{1,}");
		Pattern pattern = Pattern.compile("<a href=[\"]/oldbook/\\d+\\?\\w+=\\w+[\"]>\\s+<img src=[\"]http://img\\d+\\.\\w+\\.com/"+
										"((pics)||(mpic))/((\\w+)||(\\w+\\-\\w+\\-\\w+))\\.((gif)||(jpg))[\"] />"+
										"\\s+<div \\w+=[\"]\\w*[\"]>\\s+<span \\w+=[\"]\\w+[\"]>[^<]{1,}");
		Matcher m = pattern.matcher(html);
		while(m.find()){
			String tmp = m.group();
			//整理格式取出多余空格,显示'号
			String tmp2 = tmp.replaceAll(">\\s+", ">");
			String tmp3 = tmp2.replaceAll("&#39;", "'");
			System.out.println(tmp3);
			//获取id
			int idStart = tmp3.indexOf("oldbook")+2;
			int idEnd = tmp3.indexOf("?");
			String id = tmp3.substring(idStart,idEnd);
			//获取name
			int nameStart = tmp3.lastIndexOf(">");
			String name = tmp3.substring(nameStart);
			//获取BookDetailURL
			int detailURLStart = tmp3.indexOf("/");
			int detailURLEnd = tmp3.indexOf(">")-1;
			String detailURL = DOMAIN+tmp3.substring(detailURLStart,detailURLEnd);
			System.out.println("detailURL: "+detailURL);
			String downloadPage = HtmlCatcher.catchHtmlGET(detailURL);
			System.out.println(downloadPage);
			
			popularbooks.put(id, name);
		}
	}
	
	public void getAllGBooks(){
		HtmlCatcher.loginOn();
		//第二页才有显示最大页数
		int maxPage = parseMaxPage(HtmlCatcher.catchHtmlGET("http://www.ikandou.com/g/popular/?page=2"));
		for(int i=1;i<=maxPage;i++){
			String url = "http://www.ikandou.com/g/popular/?page="+i;
			String html = HtmlCatcher.catchHtmlGET(url);
			parseOldBooks(html);
		}
		System.out.println("书的总数: "+oldbooks.size());
		System.out.println("书的总目录: "+oldbooks);
	}
	
	public void getAllPoplularBooks(){
		HtmlCatcher.loginOn();
		int maxPage = parseMaxPage(HtmlCatcher.catchHtmlGET("http://ikandou.com/oldpopular/"));
//		for(int i=0;i<=maxPage;i++){
//			String url = "http://ikandou.com/oldpopular/?page="+i;
//			String html = HtmlCatcher.catchHtmlGET(url);
//			parsePopularBooks(html);
//			System.out.println("书的总数: "+popularbooks.size());
//		}
//		System.out.println("书的总数: "+popularbooks.size());
//		System.out.println("书的总目录: "+popularbooks);
		String url = "http://ikandou.com/oldpopular/?page=1";
		String html = HtmlCatcher.catchHtmlGET(url);
		parsePopularBooks(html);
		System.out.println("书的总数: "+popularbooks.size());
	}
	
	public HashMap<String, String> getDiffBooks(){
		for(String key: popularbooks.keySet()){
			if(oldbooks.containsKey(key)){
				popularbooks.remove(key);
			}
		}
		return popularbooks;
	}

	
	public static void main(String[] args) {
		HtmlCatcher.setUseProxy(true);
		HtmlParser parser = new HtmlParser();
		HtmlCatcher.loginOn();
//		new HtmlParser().getAllGBooks();
		parser.getAllPoplularBooks();
		System.out.println(popularbooks.size());
	}
}

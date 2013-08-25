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
 * DownloadURL Exp: http://ikandou.com/g/download/ebook/inner/716371611/epub/
 */
public class HtmlParser {
	
	int pageCounter;
	static HashMap<String,String> books = new HashMap<String,String>();
	
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
	
	public int parseMaxGPage(String html){
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
	
	public void parseBook(String html){
		//G Book: <a href="/g/detail/11896372/">******
		Pattern pattern = Pattern.compile("<a href=[\"]/g/detail/\\d+/[\"]>[\\S&&[^<]]{1,}");
		Matcher m = pattern.matcher(html);
		while(m.find()){
			//获取书的Id
			String tmp = m.group();
//			System.out.println(tmp);
			int idStart = tmp.indexOf("l")+2;
			int idEnd = tmp.lastIndexOf(">")-2;
			String id = tmp.substring(idStart,idEnd);
			//获取书名
			int nameStart = tmp.indexOf(">")+1;
			String name = tmp.substring(nameStart);
			books.put(id, name);
		}
	}
	
	public void getAllPopularBooks(){
		//第二页才有显示最大页数
		int maxPage = parseMaxGPage(HtmlCatcher.catchHtmlFirstGET("http://www.ikandou.com/g/popular/?page=2"));
		for(int i=1;i<=maxPage;i++){
			String url = "http://www.ikandou.com/g/popular/?page="+i;
			String html = HtmlCatcher.catchHtmlGET(url);
			parseBook(html);
		}
		System.out.println("书的总数: "+books.size());
		System.out.println("书的总目录: "+books);
	}
	
	public static void main(String[] args) {
		HtmlCatcher.setUseProxy(false);
		new HtmlParser().getAllPopularBooks();
	}
}

package Html;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* 
 * CSRF Exp: <input type='hidden' name='csrfmiddlewaretoken' value='66ad078b7d56df869c9c079e776b3c01' />
 * New Page URL Exp: http://ikandou.com/g/popular/?page=2
 * New Page Html: <a href="/g/detail/11896372/" title=\"1984\">
 * DownloadURL Exp: http://ikandou.com/g/download/ebook/inner/716371611/epub/
 */
public class HtmlParser {
	
	int pageCounter;
	
	public static String parseCSRF(String html){
		Pattern pattern = Pattern.compile("name=[\']csrfmiddlewaretoken[\'] value=[\']\\w+[\']");
		Matcher m = pattern.matcher(html);
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
	
	public void parseBook(String html){
//		Pattern pattern = Pattern.compile("([\"])(/g/detail/\\d+/)([\"])");
		Pattern pattern = Pattern.compile("<h5><a href=[\"]/g/detail/\\d+/[\"]([\u4E00-\u9FA5]{0,128})</a>");
		Matcher m = pattern.matcher(html);
		while(m.find()){
			System.out.println(m.group());
		}
	}
	
	public static void main(String[] args) {
		HtmlCatcher.setUseProxy(true);
//		String s = HtmlCatcher.catchHtml("http://ikandou.com/g/popular/?page=2");
//		String s = "<h5><a href=\"/g/detail/14208915/\">白话精华二十四史 </a>";
//		System.out.println(s);
//		new HtmlParser().parse(s);
	}

}

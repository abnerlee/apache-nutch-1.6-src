package edu.sjtu.nutch.kit.weibo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.nutch.protocol.ProtocolOutput;
import org.apache.nutch.protocol.ProtocolStatus;
import org.apache.nutch.protocol.httpcrawl.CommonAuthManagement;
import org.apache.nutch.protocol.httpcrawl.Http;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.mortbay.log.Log;

public class WeiboAuthManagement extends CommonAuthManagement {

	//static final Pattern LoginURLPattern = Pattern.compile("^(http://|https://){0,1}login.weibo.cn/login(/{0,1})",Pattern.CASE_INSENSITIVE);
	static final String LoginURLPattern = "^(http://|https://){0,1}login.weibo.cn/login(/{0,1}).*";
	static final String loginURL = "http://login.weibo.cn/login/";
	static final String username = "sjtucrawl@sina.cn";
	static final String password = "sjtu2013";
	
	private Http http;
	static String gsid = null;
	
	private String getLoginPageContent(HttpClient httpclient, ProtocolOutput out) throws ClientProtocolException, IOException
	{
		if(out!=null && out.getContent() != null && out.getContent().getContent()!=null)
		{
			String content = new String(out.getContent().getContent());
			if(content != null && content.length() > 50) 
				return content;
		}
		//get login page 
		HttpResponse res = Get(httpclient,loginURL);
		return EntityUtils.toString(res.getEntity());

	}
	
	private HttpResponse Get(HttpClient httpclient, String url) throws ClientProtocolException, IOException
	{
		HttpGet get = new HttpGet(url);
		get.setHeader("User-Agent",http.getUserAgent());  
		get.setHeader("Referer", "http://weibo.com/");  
		get.setHeader("Content-Type", "application/x-www-form-urlencoded");  
		HttpResponse res = httpclient.execute(get);
		get.abort();
		return res;
	}

	public boolean loginWeiboCN(DefaultHttpClient httpclient, ProtocolOutput out,  String username,String password)
	{
		RedirectStrategy oldStrategy = httpclient.getRedirectStrategy();
    	httpclient.setRedirectStrategy(new LaxRedirectStrategy());
    	String getgsid = null;  
	    try {  
		    String content = getLoginPageContent(httpclient,out); //to test
		    if(content==null) return false;  
		    Document doc = Jsoup.parse(content);   
		          
		    Element form=null , pwnd=null , vk=null;  
		    String rand=null , spwnd=null , svk=null;  
		    form  = doc.select("form[method=post]").first();  
		    pwnd  = doc.select("input[type=password]").first();  
		    vk = doc.select("input[name=vk]").first();  
		    Element backURL = doc.select("input[name=BackURL]").first();
		    Element backTitle = doc.select("input[name=backTitle]").first();
		    if(form==null || pwnd==null || vk==null) return false;  
		          
		    rand = form.attr("action");  
		    spwnd = pwnd.attr("name");  
		    svk = vk.attr("value");  
		    String sbackURL = backURL.attr("value");
		    String sbackTitle = backTitle.attr("value");
		    if(rand==null || spwnd==null || svk==null) return false;  
	    	
	        String url = loginURL+form.attr("action");
	        HttpPost post = new HttpPost(url); 
	        post.setHeader("User-Agent",http.getUserAgent());  
	        post.setHeader("Referer", "http://weibo.com/");  
	        post.setHeader("Content-Type", "application/x-www-form-urlencoded");  
	          
	        List<NameValuePair> qparams = new ArrayList<NameValuePair>();  
	        qparams.add(new BasicNameValuePair("mobile", username));  
	        qparams.add(new BasicNameValuePair(spwnd, password));  
	        qparams.add(new BasicNameValuePair("remember", "on"));  
	        qparams.add(new BasicNameValuePair("backURL", sbackURL));  
	        qparams.add(new BasicNameValuePair("backTitle", sbackTitle));  
	        qparams.add(new BasicNameValuePair("vk", svk));  
	        qparams.add(new BasicNameValuePair("submit", "µÇÂ¼"));  
	        UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");  
	        post.setEntity(params);       
	        HttpResponse res = httpclient.execute(post);  
	        post.abort();  
	        String result = EntityUtils.toString(res.getEntity());
	        List<Cookie> cookies = httpclient.getCookieStore().getCookies();  
	        if(cookies.size()!=0) getgsid=cookies.get(0).getValue();  
	        else getgsid=null;  
	        gsid = getgsid;  
	        
	        //follow redirection
	        doc = Jsoup.parse(result);
	        Element meta = doc.select("meta[http-equiv=refresh]").first();
	        if(meta!=null) 
	        {
	        	Element link = doc.getElementsByTag("a").first();
	        	res = Get(httpclient,link.attr("href"));
	        }
	        
	          
	    } catch (ClientProtocolException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } catch(Exception e){  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    } 
	    httpclient.setRedirectStrategy(oldStrategy); 
	    if(gsid==null) return false;  
        else return true;  
	}
	
	@Override
	public boolean needLogin(ProtocolOutput out) {
		String checkUrl = null;
		if(out.getStatus().getCode() == ProtocolStatus.TEMP_MOVED)
		{
			checkUrl = out.getStatus().getMessage();
		}
		else if(out.getStatus().isSuccess())
		{
			checkUrl = out.getContent().getUrl();
		}
		
		if(checkUrl!=null) 
		{
			boolean needlogin = Pattern.matches(LoginURLPattern,checkUrl);
			if(needlogin) Log.debug("need login at "+checkUrl);
			return needlogin;
		}
			
		return false;
	}

	@Override
	public boolean processLogin(Http http, ProtocolOutput out)
	{
		this.http = http;
		http.getCookieStore().saveToRecordFile();
		return loginWeiboCN(http.getClient(), out, username, password);
	}

	
	
}

package org.apache.nutch.protocol.httpcrawl;

import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.plugin.Extension;
import org.apache.nutch.plugin.ExtensionPoint;
import org.apache.nutch.plugin.PluginRepository;
import org.apache.nutch.plugin.PluginRuntimeException;
import org.apache.nutch.protocol.ProtocolOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonAuthFactory {
	
	public class DefaultCommonAuthManagement extends CommonAuthManagement
	{
		@Override
		public boolean needLogin(ProtocolOutput out) {
			return false;
		}

		@Override
		public boolean processLogin(Http http, ProtocolOutput out) {
			return true;
		}
		
		public ProtocolOutput loginCheckAndProcessLogin(Http http,ProtocolOutput out)
		{
			return out;
		}
	}
	
	/*
	public static List<CommonAuthManagement> authStore = new ArrayList<CommonAuthManagement>();
	
	public static WeiboAuthManagement am = new WeiboAuthManagement();
	
	public CommonAuthFactory(){
		//todo
		//authStore.add(new WeiboAuthManagement());
	}
	
	public static CommonAuthManagement getAuthManagement(String url)
	{
		//todo
		try {
			
			
			URL checkUrl = new URL(url);
			
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return am;
	}
	*/
	
	
	  public static final Logger LOG = LoggerFactory.getLogger(CommonAuthFactory.class);

	  private ExtensionPoint extensionPoint;

	  private Configuration conf;
	  
	  public DefaultCommonAuthManagement defaultAuthManagement;

	  public CommonAuthFactory(Configuration conf) {
		  this.defaultAuthManagement = new DefaultCommonAuthManagement();
	    this.conf = conf;
	    this.extensionPoint = PluginRepository.get(conf).getExtensionPoint(
	    		CommonAuthManagement.X_POINT_ID);
	    if (this.extensionPoint == null) {
	      System.err.print("x-point " + CommonAuthManagement.X_POINT_ID
	          + " not found.");
	    }
	  }
	  
	  public CommonAuthManagement getAuthManagment(String url)
	  {
		  try {
			  if(url!=null && this.extensionPoint!=null)
			  {
				  Extension[] extensions = this.extensionPoint.getExtensions();
			      for (int i = 0; i < extensions.length; i++) {
				      Extension extension = extensions[i];
				      if (Pattern.matches(extension.getAttribute("urlPattern"),url))
				    	  return (CommonAuthManagement) extension.getExtensionInstance();
			      }
			  }
		  } catch (PluginRuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		  }
		  return this.defaultAuthManagement;
	  }
	  
	 public void tryLoginAll(Http http)
	 {
		 if(this.extensionPoint!=null)
		 {
			 Extension[] extensions = this.extensionPoint.getExtensions();
		     for (int i = 0; i < extensions.length; i++) {
			      Extension extension = extensions[i];
			      try {
					((CommonAuthManagement) extension.getExtensionInstance()).processLogin(http, null);
				} catch (PluginRuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		     }
		 }
	 }
	  

}

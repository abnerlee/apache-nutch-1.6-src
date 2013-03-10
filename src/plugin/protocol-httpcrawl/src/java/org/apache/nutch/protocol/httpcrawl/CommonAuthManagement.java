package org.apache.nutch.protocol.httpcrawl;

import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.nutch.fetcher.Fetcher;
import org.apache.nutch.plugin.Pluggable;
import org.apache.nutch.protocol.Content;
import org.apache.nutch.protocol.Protocol;
import org.apache.nutch.protocol.ProtocolOutput;
import org.apache.nutch.protocol.ProtocolStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CommonAuthManagement implements Pluggable, Configurable {
	
	
	public static final Logger LOG = LoggerFactory.getLogger(CommonAuthManagement.class);
	public final static String X_POINT_ID = CommonAuthManagement.class.getName();
	private Configuration conf = null;
	
	
	public abstract boolean needLogin(ProtocolOutput out);
	
	public abstract boolean processLogin(Http http,ProtocolOutput out);
	
	public ProtocolOutput loginCheckAndProcessLogin(Http http,ProtocolOutput out)
	{
		int code = out.getStatus().getCode();
		if((code<400||code>=200)&&needLogin(out))
		{
			ProtocolStatus s=out.getStatus();
			s.setCode(ProtocolStatus.RETRY);
			s.setMessage("login required");
			out = new ProtocolOutput(new Content(),s);
			processLogin(http,out);
			http.getCookieStore().saveToRecordFile();
		}
		
		return out;
	}
	

	public Configuration getConf() {
		return this.conf;
	}


	public void setConf(Configuration arg0) {
		this.conf=arg0;
	}
	
}

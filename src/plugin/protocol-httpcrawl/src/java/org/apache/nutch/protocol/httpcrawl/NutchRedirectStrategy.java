package org.apache.nutch.protocol.httpcrawl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class NutchRedirectStrategy extends DefaultRedirectStrategy {
	
	public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)throws ProtocolException
    {
		boolean redirect = super.isRedirected(request, response, context);
		int code = response.getStatusLine().getStatusCode();
		if(code == 302 || code ==307)
			return false;
		else
			return redirect;
    }
	
	

}

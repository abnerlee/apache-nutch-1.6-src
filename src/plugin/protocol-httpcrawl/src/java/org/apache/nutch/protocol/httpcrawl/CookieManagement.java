package org.apache.nutch.protocol.httpcrawl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

public class CookieManagement implements CookieStore, Serializable{
	
	private static String recordFileName = "conf/NutchCookieStore";
	
	private static BasicCookieStore cookieStore = null;
	
	CookieManagement()
	{
		synchronized(this)
		{
			try {
				
				FileInputStream fis = new FileInputStream(recordFileName);
				ObjectInputStream ois=new ObjectInputStream(fis);
				cookieStore=(BasicCookieStore)ois.readObject();
				ois.close();
				
			} catch (Exception e) {
				cookieStore =new BasicCookieStore();
			} 
		}
	}

	public synchronized void saveToRecordFile()
	{
	
		try {			
			FileOutputStream fos = new FileOutputStream(recordFileName,false);
			ObjectOutputStream oos=new ObjectOutputStream(fos);
			oos.writeObject(cookieStore);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalize()
    {
		saveToRecordFile();
    }
	
	
	@Override
	public void addCookie(Cookie arg0) {
		cookieStore.addCookie(arg0);
		
	}

	@Override
	public void clear() {
		cookieStore.clear();
	}

	@Override
	public boolean clearExpired(Date arg0) {
		return cookieStore.clearExpired(arg0);
	}

	@Override
	public List<Cookie> getCookies() {
		return cookieStore.getCookies();
	}
	
}
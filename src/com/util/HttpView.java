package com.util;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class HttpView {
	public static String result[][] = new String[500][];
	public static int count = -1;
	static String TAG = "HttpView";
	public static int timeout = -1;

	public static boolean checkConnectivityServer(String ip, int port) {
		boolean success = false;
		try {
			Socket soc = new Socket();
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			soc.connect(socketAddress, 3000);
			success = true;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println(" Connecting to server " + success);
		return success;

	}

public static String connect2Server(String url) {
		
		HttpView.count = -1;
		StringBuffer res=new StringBuffer();
		
		Log.v(TAG, url);
		URL u;
		try {
	
			for (int i = 0; i < result.length; i++) {
				result[i] = null;
			}
			u = new URL(url);
			URLConnection uc= u.openConnection();
			if(timeout!=-1)
				uc.setConnectTimeout(timeout);
			
			Scanner scanner=new Scanner(uc.getInputStream());
			
			while (scanner.hasNext()) {
				String row = StringHelper.n2s(scanner.nextLine());
				if (row.length() > 0) {
					res.append(row + "\n");
					String cols[] = row.split(",");
					for (int i = 0; i < cols.length && cols[i] != null; i++) {
						cols[i] = cols[i].trim();
					}
					result[++HttpView.count] = cols;
					Log.v(TAG, HttpView.count+" "+row);
				}
			}
			scanner.close();
			u=null;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.v(TAG, "HttpView.count "+HttpView.count);
		return res.toString().trim();
	}

	public static String curl(String method,String... param){
		String url=AndroidConstants.MAIN_URL();
		String query="method="+method;
		for (int i = 0; param!=null&&i < param.length; i++) {
			query+="&"+param[i];
		}
		query=URLEncoder.encode(query);
		url+=query;
		return url;
	}
	public static Bitmap drawable_from_url(String url) {
		Bitmap x = null;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setRequestProperty("User-agent", "Mozilla/4.0");
			connection.connect();
			InputStream input = connection.getInputStream();
			x = BitmapFactory.decodeStream(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return x;
	}

	public static Object connect2ServerObject(String url) {
		Log.v(TAG, "Reading Object");
		Log.v(TAG, url);
		Object o = null;
		URL u;
		try {
			u = new URL(url);
			URLConnection uc = u.openConnection();
			if (timeout != -1)
				uc.setConnectTimeout(timeout);
			ObjectInputStream ois = new ObjectInputStream(uc.getInputStream());
			o = ois.readObject();
			System.out.println(o);
			u = null;
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return o;
	}

	

}

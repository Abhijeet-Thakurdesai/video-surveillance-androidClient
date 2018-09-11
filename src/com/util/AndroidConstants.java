package com.util;

public class AndroidConstants {
	public static String MAIN_SERVER_IP = "192.168.43.114";
	public static String MAIN_SERVER_PORT = "7878";
	public static final String MAIN_URL(){
		return "http://"	
			+ AndroidConstants.MAIN_SERVER_IP + ":"
			+ AndroidConstants.MAIN_SERVER_PORT + "/?";
	}
	public static String CURRENT_USER_IMEI = "";
	public static int CURRENT_USER_ID= 0;
    public static int IMAGE_CAPTURE_DELAY=2000;
}   

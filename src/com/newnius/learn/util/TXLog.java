package com.newnius.learn.util;

public class TXLog {
	public static void i(String TAG, String information){
		System.out.println(TAG+":"+information);
	}
	
	public static void d(String TAG, String information){
		System.out.println(TAG+":"+information);
	}
	
	public static void e(String TAG, String information){
		System.out.println(TAG+":"+information);
	}
	
	public static void e(String TAG, Exception ex){
		ex.printStackTrace();
	}
}

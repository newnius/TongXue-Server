/*
 * @author Newnius
 * 
 * take control of all the output
 * output log by tag
 * easy to debug
 * */
package com.newnius.learn.util;

import java.util.HashSet;
import java.util.Set;

public class Logger {
	private static Set<String> tags = new HashSet<>();
	private static int level = 0;
	public static final int LEVEL_INFO = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_WARN = 2;
	public static final int LEVEL_ERROR = 3;
	

	public static void info(String tag, String str) {
		if(level > LEVEL_INFO)
			return;
		if (tags.contains(tag)) {
			System.out.println(tag + ": " + str);
		}
	}

	public static void debug(String tag, String str) {
		if(level > LEVEL_DEBUG)
			return;
		if (tags.contains(tag)) {
			System.out.println(tag + ": " + str);
		}
	}
	
	public static void warn(String tag, String str) {
		if(level > LEVEL_WARN)
			return;
		if (tags.contains(tag)) {
			System.out.println(tag + ": " + str);
		}
	}
	
	public static void error(String tag, Exception ex) {
		if(level > LEVEL_ERROR)
			return;
		if (tags.contains(tag)) {
			ex.printStackTrace();
		}
	}

	public static void addTag(String tag) {
		synchronized (tags) {
			if (!tags.contains(tag)) {
				tags.add(tag);
			}
		}
		showTags();
	}

	public static void removeTag(String tag) {
		synchronized (tags) {
			if (tags.contains(tag)) {
				tags.remove(tag);
			}
		}
		showTags();
	}
	
	public static void removeAllTags(){
		synchronized(tags){
			tags.clear();
		}
	}

	public static void showTags() {
		String str = "Tags: [";
		boolean isFirstTag = true;
		for (String tag : tags) {
			if (!isFirstTag) {
				str += ",";
			}
			isFirstTag = false;
			str = str + tag;
		}
		str += "]";
		debug("LOGGER", str);
	}

}

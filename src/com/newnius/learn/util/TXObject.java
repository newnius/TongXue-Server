package com.newnius.learn.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by newnius on 16-3-16.
 */
public class TXObject {
	private static final String TAG = "TXObject";
	private Map<String, String> data;

	public TXObject() {
		data = new HashMap<>();
	}

	public void set(String key, String value) {
		if (data.containsKey(key)) {
			data.remove(key);
			TXLog.d(TAG, key + " is already set.");
		}
		data.put(key, value);
	}

	public void set(String key, Integer value) {
		if (data.containsKey(key)) {
			data.remove(key);
			TXLog.d(TAG, key + " is already set.");
		}
		data.put(key, value + "");
	}

	public void set(String key, Long value) {
		if (data.containsKey(key)) {
			data.remove(key);
			TXLog.d(TAG, key + " is already set.");
		}
		data.put(key, value + "");
	}
	
    public void set(String key, Float value){
        if(data.containsKey(key)) {
            data.remove(key);
            TXLogger.debug(TAG, key+" is already set.");
        }
        data.put(key, value+"");
    }

	public String get(String key) {
		if (data.containsKey(key))
			return data.get(key);
		TXLog.d(TAG, key + " not exist.");
		return null;
	}

	public int getInt(String key) {
		try {
			if (data.containsKey(key))
				return Integer.parseInt(data.get(key));
			TXLog.e(TAG, key + " not exist.");
			return 0;
		} catch (Exception ex) {
			return 0;
		}
	}

	public long getLong(String key) {
		try {
			if (data.containsKey(key))
				return Long.parseLong(data.get(key));
			TXLog.e(TAG, key + " not exist.");
			return 0;
		} catch (Exception ex) {
			return 0;
		}
	}
	
    public Float getFloat(String key) {
        try {
            if (data.containsKey(key))
                return Float.parseFloat(data.get(key));
            TXLogger.warn(TAG, key + " not exist.");
            return null;
        }catch(Exception ex){
            return null;
        }
    }

	public boolean hasKey(String key) {
		return data.containsKey(key) && data.get(key) != null;
	}

	public String toJson() {
		return new Gson().toJson(data);
	}

	public static TXObject fromJson(String json) {
		try {
			return new Gson().fromJson(json, new TypeToken<HashMap<String, String>>() {
			}.getType());
		} catch (Exception ex) {
			TXLog.e(TAG, "convert json to TXObject failed.");
			return null;
		}
	}

}

package com.xiaoyintong.app.common;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaoyintong.app.bean.Subregion;

import android.content.Context;

public class AccessUtil {
	
	public static void saveSubregionStruct(Context context ,String jsonString)
	{
		String fileName = "subregionStruct.txt";
		FileUtils.write(context, fileName, jsonString);
	}
	
	public static List<Subregion> getSubregions(Context context)
	{
		String fileName = "subregionStruct.txt";
		String jsonString = FileUtils.read(context, fileName);
		Gson gson = new Gson();
    	Type typeOfT = new TypeToken<List<Subregion>>(){}.getType();
    	return gson.fromJson(jsonString, typeOfT);
	}

}

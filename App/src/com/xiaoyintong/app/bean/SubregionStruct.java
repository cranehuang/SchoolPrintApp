package com.xiaoyintong.app.bean;

import java.util.List;
import java.util.Map;

public class SubregionStruct {
	
	private List<String> subregionNums;
	private Map<String, List<String>> structMap;
	
	public SubregionStruct(List<String> subregionNums , Map<String, List<String>> structMap)
	{
		this.subregionNums = subregionNums;
		this.structMap = structMap;
	}
	
	
	public List<String> getsubregionNums()
	{
		return this.subregionNums;
	}
	
	public Map<String, List<String>> getStructMap()
	{
		return this.structMap;
	}

}

package com.xiaoyintong.app.bean;

import java.util.ArrayList;
import java.util.List;


public class Subregion {
	
	private int part_name;
	private List<Location> part = new ArrayList<Location>();
	
	public Subregion(int part_name , List<Location> locations)
	{
		this.part_name = part_name;
		this.part = locations;
	}
	
	public List<Location> getLocations()
	{
		return this.part;
	}
	
	public int getPartNum()
	{
		return this.part_name;
	}
	
}

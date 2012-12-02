package com.hoos.around;

public class Location {
	public int location_id;
	public String location_name;
	public double location_lat;
	public double location_long;
	
    @Override
    public String toString() {
        return location_name;
    }
}

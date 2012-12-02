package com.hoos.around;

public class Class {
	public int course_id;
	public String course_mnem;
	public int location_id;
	public String course_start;
	public String course_end;
	
	public Boolean monday;
	public Boolean tuesday;
	public Boolean wednesday;
	public Boolean thursday;
	public Boolean friday;
	
    @Override
    public String toString() {
        return course_mnem + " @ " + course_start;
    }
}

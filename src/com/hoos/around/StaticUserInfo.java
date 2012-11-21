package com.hoos.around;

public class StaticUserInfo {
	static private int user_id;
	static private String fb_id;
	static private Boolean logged_in = false;
	
	static Boolean isLoggedIn() {
		return logged_in;
	}
	
	static void setUserID(int id) {
		logged_in = true;
		user_id = id;
	}
	
	static int getUserID() {
		return user_id;
	}
	
	static void unSetID() {
		user_id = 0;
		logged_in = false;
	}

	public static String getFbID() {
		return fb_id;
	}

	public static void setFbID(String fb_id) {
		StaticUserInfo.fb_id = fb_id;
	}
}

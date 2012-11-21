package com.hoos.around;

import java.util.ArrayList;

public class StaticUserInfo {
	static private int user_id; //user id stored in mysql db
	static private String fb_id; //facebook's user id for logged in user
	static private ArrayList<String> fb_friends = new ArrayList<String>(); //list of facebook friends of user
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

	public static ArrayList<String> getFbFriends() {
		return fb_friends;
	}

	public static void setFbFriends(ArrayList<String> fb_friends) {
		StaticUserInfo.fb_friends = fb_friends;
	}
	
	public static void addFbFriend(String friend) {
		fb_friends.add(friend);
	}
}

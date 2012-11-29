package com.hoos.around;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class HomeFragment extends Fragment{

	Facebook facebook = new Facebook("332459203518890");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    String fb_id = "";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 if(!StaticUserInfo.isLoggedIn() && !StaticUserInfo.wasError()) {	//If user is not logged in AND NO ERROR, Try to log him in
		        facebook.authorize(this.getActivity(), new DialogListener() {
		            @Override
		            public void onComplete(Bundle values) {
		            	Log.d("FB","Facebook Success!");
		                mAsyncRunner.request("me", new IdRequestListener());
		                mAsyncRunner.request("me/friends", new FriendsRequestListener());
		            }
		
		            @Override
		            public void onFacebookError(FacebookError error) {
		            	Log.d("FB","Facebook Facebook Error");
		            	StaticUserInfo.setError(true);
		            }
		
		            @Override
		            public void onError(DialogError e) {
		            	Log.d("FB","Facebook Error");
		            	StaticUserInfo.setError(true);
		            }
		
		            @Override
		            public void onCancel() {
		            	StaticUserInfo.setError(true);
		            }
		        });
	        }
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_fragment, container, false);
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
	
	public void ActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
	}
	
	 private class IdRequestListener implements RequestListener{

			@Override
			public void onComplete(String response, Object state) {
				try {
					final JSONObject json = Util.parseJson(response);
					final String id = json.getString("id");
					System.out.println(json.toString());
					RestClient.get("/users/fb_id/" + id, null, null, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONArray rsp) {
							if (rsp.length()==0) {
								//no content in response json means no user with this fb id exists
								System.out.println("New user logged in");
								try {
									RestClient.get("/users/add/"+json.getString("first_name")+"/"+json.getString("last_name")+"/"+id, null, null, new JsonHttpResponseHandler() {
										@Override
										public void onSuccess(JSONArray rsp) {
											//TODO handle user being added
											StaticUserInfo.setFbID(id);
											System.out.println("new user's fb id: "+id);
											try {
												StaticUserInfo.setUserID(rsp.getJSONObject(0).optJSONObject("User").getInt("user_id"));
											} catch (JSONException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										@Override
										public void onFailure(Throwable e, String rsp) {
											//TODO handle error adding user
											System.err.println(e.getMessage());
										}
									});
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							else {
								StaticUserInfo.setFbID(id);
								System.out.println("user's fb id: "+id);
								try {
									StaticUserInfo.setUserID(rsp.getJSONObject(0).optJSONObject("User").getInt("user_id"));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
						@Override
						public void onFailure(Throwable e, String rsp) {
							
						}
					});
				} catch (FacebookError e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}

			@Override
			public void onIOException(IOException e, Object state) {
				
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				
			}
	    	
	    }
	    private class FriendsRequestListener implements RequestListener{

			@Override
			public void onComplete(String response, Object state) {
				try {
					JSONObject json = Util.parseJson(response);
					JSONArray arr = json.getJSONArray("data");
					HashSet<String> friends = new HashSet<String>();
					for (int i=0; i<arr.length(); i++) {
						friends.add(arr.optJSONObject(i).getString("id"));
						//StaticUserInfo.addFbFriend(arr.optJSONObject(i).getString("id"));
					}
					StaticUserInfo.setFbFriends(friends);
				} catch (FacebookError e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
				
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				
			}
	    	
	    }

}

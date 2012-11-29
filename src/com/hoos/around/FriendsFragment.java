package com.hoos.around;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.zip.Inflater;

import org.json.*;

import com.hoos.around.ImageThreadLoader.ImageLoadedListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FriendsFragment extends Fragment{
	
	/*
	 * 	LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = current.getLatitude();
		longitude = current.getLatitude();
		
		String GPS_URL = "locations/gps/" + latitude  + "/" + longitude + "/1";
	 * 
	 */
	
	private ArrayList<User> UserList = new ArrayList<User>();
	private UserAdapter userAdapter;
	private ScheduleAdapter scheduleAdapter;
	private ListView UserListView;
	private ListView ClassListView;
	
	public void LoadSchedule(User user) {
		
		RestClient.get("schedules/id/" + user.user_id, null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray classes) {
                // Grab A Schedule
            	Schedule schedule = new Schedule();
            	schedule.courses = new ArrayList<Class>();
            	
				try {
					
					for(int x = 0; x < classes.length(); x++) {
						Class temp = new Class();
						JSONObject JSONSchedule = (JSONObject)classes.get(x);
						schedule.user_id = JSONSchedule.getJSONObject("Schedule").getInt("user_id");
						temp.course_id = JSONSchedule.getJSONArray("Course").getJSONObject(0).getInt("course_id");
						temp.course_start = JSONSchedule.getJSONArray("Course").getJSONObject(0).getString("course_start");
						temp.course_end = JSONSchedule.getJSONArray("Course").getJSONObject(0).getString("course_end");
						temp.course_mnem = JSONSchedule.getJSONArray("Course").getJSONObject(0).getString("course_mnem");
						temp.location_id = JSONSchedule.getJSONArray("Course").getJSONObject(0).getInt("location_id");
						schedule.courses.add(temp);
					}
					
					scheduleAdapter.clear();
					scheduleAdapter.addAll(schedule.courses);
					scheduleAdapter.notifyDataSetChanged();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
				}
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
				Log.d("JSON", response);
				Log.d("JSON", RestClient.getAbsoluteUrl("courses/view/"));
            }
            
        });	
	}
	
	public class UserAdapter extends ArrayAdapter<User>{

	    Context context; 
	    int layoutResourceId;    
	    
	    public UserAdapter(Context context, int layoutResourceId) {
	        super(context, layoutResourceId);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	User tempUser = this.getItem(position);
    	    if (convertView == null) {
    	    	convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.friends_fragment_userlist, parent, false);
    	    }
	    	TextView label = (TextView)convertView.findViewById(R.id.name);
    	    label.setText(tempUser.user_first + " " + tempUser.user_last);
    	    Log.d("VIEW", tempUser.user_first + " " + tempUser.user_last);
    	    return (convertView);
	    }

	}
	
	private class ScheduleAdapter extends ArrayAdapter<Class>{

	    Context context; 
	    int layoutResourceId;
	    private ImageThreadLoader imageLoader = new ImageThreadLoader();

	    
	    public ScheduleAdapter(Context context, int layoutResourceId) {
	        super(context, layoutResourceId);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	final Class tempClass = this.getItem(position);
    	    if (convertView == null) {
    	    	convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.friends_fragment_schedulelist, parent, false);
    	    }
	    	TextView label = (TextView)convertView.findViewById(R.id.name);
	    	final ImageView image = (ImageView)convertView.findViewById(R.id.list_image);
	    	//image.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
	        Bitmap cachedImage = null;
	        try {
	          cachedImage = imageLoader.loadImage("http://uva-cs4720-spinach.appspot.com/serve/" + tempClass.location_id, new ImageLoadedListener() {
	        	  public void imageLoaded(Bitmap imageBitmap) {
	        		  image.setImageBitmap(imageBitmap);
	        		  Log.e("IMAGE", "GOOD remote image URL: " + "http://uva-cs4720-spinach.appspot.com/serve/" + tempClass.location_id);
	        		  notifyDataSetChanged();                
	          	  }
	          });

	        } catch (MalformedURLException e) {
	          Log.e("IMAGE", "Bad remote image URL: " + "http://uva-cs4720-spinach.appspot.com/serve/" + tempClass.location_id, e);
	        }

	        if( cachedImage != null ) {
        	      image.setImageBitmap(cachedImage);
	        }

	        
    	    label.setText(tempClass.course_mnem);
    	    return (convertView);
	    }

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userAdapter = new UserAdapter(getActivity(), R.layout.friends_fragment_userlist);
		scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.friends_fragment_schedulelist);
		
        /*RestClient.get("users/view/", null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray users) {
                // Grab Users
            	UserList.clear();
				try {
					for(int x = 0; x < users.length(); x++) {
						User temp = new User();
						JSONObject JSONUser = (JSONObject)users.get(x);
						temp.user_id = JSONUser.getJSONObject("User").getInt("user_id");
						temp.user_first = JSONUser.getJSONObject("User").getString("user_first");
						temp.user_last = JSONUser.getJSONObject("User").getString("user_last");
						//temp.schedule_id = JSONUser.getJSONObject("User").getInt("schedule_id");
						UserList.add(temp);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
				}
				*/
                // Do something with the response
		
		  	LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			Location current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			double latitude = current.getLatitude();
			double longitude = current.getLatitude();
			final PvtJsonArray closeLocations = new PvtJsonArray();
			RestClient.get("/locations/gps/" + latitude + "/" + longitude, null, null, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONArray rsp) {
					closeLocations.setArray(rsp);
					System.out.println("locations gotten");
				}
				@Override
				public void onFailure(Throwable e, String rsp) {
					Log.d("JSON", e.getMessage());
				}
			});
			System.out.println(closeLocations.getArray().toString());
				UserList.clear();
				Object[] friends = StaticUserInfo.getFbFriends().toArray();
				for (int i=0; i<friends.length; i++) {
					final String friendFbId = friends[i].toString();
					RestClient.get("/users/fb_id/" + friendFbId, null, null, new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(JSONArray rsp) {
							User temp = new User();
							JSONObject JSONUser=null;
							try {
								JSONUser = rsp.getJSONObject(0);
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								temp.user_id = JSONUser.optJSONObject("User").getInt("user_id");
								temp.user_first = JSONUser.optJSONObject("User").getString("user_first");
								temp.user_last = JSONUser.getJSONObject("User").getString("user_last");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
//							if (UserList.add(temp)) {
//								System.out.println(temp.user_first + " added");
//							}
//							else {
//								System.out.println(temp.user_first + " not added");
//							}
							userAdapter.add(temp);
							userAdapter.notifyDataSetChanged();
						}
						@Override
						public void onFailure(Throwable e, String rsp) {
							System.err.println(e.getMessage());
							Log.d("JSON", rsp);
							Log.d("JSON", RestClient.getAbsoluteUrl("/users/fb_id/"+friendFbId));
						}
					});
				}
				//userAdapter.clear();
				//userAdapter.notifyDataSetChanged();
            }
            
           /* @Override
            public void onFailure(Throwable e, String response) {
				Log.d("JSON", response);
				Log.d("JSON", RestClient.getAbsoluteUrl("courses/view/"));
            }
            
        });	
	}*/
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.friends_fragment, container, false);
		
		UserListView = (ListView)view.findViewById(R.id.friendsList);
		UserListView.setAdapter(userAdapter);
		UserListView.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				   User selected = (User)adapter.getItemAtPosition(position);
				   
				   LoadSchedule(selected);
			   } 
			});
		
		ClassListView = (ListView)view.findViewById(R.id.classList);
		ClassListView.setAdapter(scheduleAdapter);
		ClassListView.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {

			   } 
			});
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
	
	private class PvtJsonArray {
		JSONArray array = new JSONArray();
		public void PvtJsonArray() {
			
		}
		public void PvtJsonArray(JSONArray arr) {
			array = arr;
		}
		public void setArray(JSONArray arr) {
			array = arr;
		}
		public JSONArray getArray() {
			return array;
		}
	}
}
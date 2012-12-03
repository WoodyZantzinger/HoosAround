package com.hoos.around;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.zip.Inflater;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

import com.hoos.around.ImageThreadLoader.ImageLoadedListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.Time;
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
	private ProgressDialog dialog;
	private TextView Current_Location;
	
	public void LoadSchedule(final User user) {
		dialog = ProgressDialog.show(this.getActivity(), "", "Loading Schedule...", true);
		RestClient.get("schedules/today/" + user.user_id + "/monday", null, null, new JsonHttpResponseHandler() {
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
					dialog.dismiss();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
					dialog.dismiss();
				}
            }
            
            
            
            @Override
            public void onFailure(Throwable e, String response) {
				Log.d("JSON", response);
				Log.d("JSON", RestClient.getAbsoluteUrl("courses/view/"));
				dialog.dismiss();
            }
            
        });
		
		RestClient.get("users/lastLocation/" + user.facebook_id + "/17.10.00/monday", null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject JSONClass) {
            	try{
					final Class temp = new Class();
					temp.course_start = JSONClass.getString("course_start");
					temp.course_end = JSONClass.getString("course_end");
					temp.course_mnem = JSONClass.getString("course_mnem");
					temp.location_id = JSONClass.getInt("location_id");
					
					String pattern = "HH:mm:ss";
					Time now = new Time();
					now.setToNow();
		            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		            try {
		                Date class_end = sdf.parse(temp.course_end);
		                //Date current_time = sdf.parse(now.hour + ":" + now.minute + ":" + now.second);
		                Date current_time = sdf.parse("17:10:00");

		                // Outputs -1 as class_End is before NOW
		                if(class_end.before(current_time)) {
		                	Current_Location.post(new Runnable() {
		                	    public void run() {
		                	    	Current_Location.setText(user.user_first + " Last Left " + temp.course_mnem);
		                	    } 
		                	});
		                } else {
		                	Current_Location.post(new Runnable() {
		                	    public void run() {
				                	Current_Location.setText(user.user_first + " Should be in " + temp.course_mnem);
		                	    } 
		                	});
		                }

		            } catch (ParseException e){
		                // Exception handling goes here
		            }
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
				}
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
    	    label.setText(tempUser.user_first + " " + tempUser.user_last + System.getProperty("line.separator") + ((Double)(tempUser.distance+.5)).intValue() + " meters away");
    	    Log.d("VIEW", tempUser.user_first + " " + tempUser.user_last + " " + position);
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

			String pattern = "HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            SimpleDateFormat sdf_output = new SimpleDateFormat("hh:mm aa");
    	    try {
				label.setText(tempClass.course_mnem + System.getProperty("line.separator") + sdf_output.format(sdf.parse(tempClass.course_start)) + " - " + sdf_output.format(sdf.parse(tempClass.course_end)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    return (convertView);
	    }

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(StaticUserInfo.isLoggedIn()) {
			userAdapter = new UserAdapter(getActivity(), R.layout.friends_fragment_userlist);
			scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.friends_fragment_schedulelist);		
		  	LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			Location current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			double latitude = 38;
			double longitude = -78;
			if (current != null) {
				latitude = current.getLatitude();
				longitude = current.getLongitude();
			}
			UserList.clear();
			Object[] friends = StaticUserInfo.getFbFriends().toArray();
			String friendStr = "";
			for (int i=0; i<friends.length; i++) {
				friendStr += friends[i] + "/";
			}
			dialog = ProgressDialog.show(this.getActivity(), "", "Loading Friends...", true);
			Log.d("FRND","Loading...");
			//RestClient.get("/users/view", null, null, new JsonHttpResponseHandler() {
			RestClient.get("/users/closestFriends/" + latitude + "/" + longitude + "/13.00.00/monday/" + friendStr, null, null, new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(JSONArray rsp) {
						Log.d("FRND","Success!...");
						try {
							ArrayList<User> users = new ArrayList<User>();
							for (int i=0; i<rsp.length(); i++) {
								User temp = new User();
								temp.setDistance(rsp.getJSONArray(i).getDouble(1));
								temp.setUser_first(rsp.getJSONArray(i).getJSONArray(0).getJSONObject(0).getJSONObject("User").getString("user_first"));
								temp.setUser_last(rsp.getJSONArray(i).getJSONArray(0).getJSONObject(0).getJSONObject("User").getString("user_last"));
								temp.setUser_id(rsp.getJSONArray(i).getJSONArray(0).getJSONObject(0).getJSONObject("User").getInt("user_id"));
								temp.facebook_id = (rsp.getJSONArray(i).getJSONArray(0).getJSONObject(0).getJSONObject("User").getInt("fb_id"));
								users.add(temp);
								System.out.println("1user " + i + " " + temp.user_first);
							}	
							for (int i=0; i<users.size(); i++) {
								System.out.println("2user" + i + " " + users.get(i).user_first);
							}
							userAdapter.addAll(users);
							userAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							dialog.dismiss();
						}
						dialog.dismiss();
						Log.d("JSON", rsp.toString());
					}
					
					@Override
					public void onSuccess(JSONObject rsp) {
						Log.d("YES", "YYYEEYEYSYSHDYHFDHF");
					}
					
					@Override
					public void onFailure(Throwable e, String rsp) {
						Log.d("JSON", e.getMessage());
						dialog.dismiss();
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
		
		if(StaticUserInfo.isLoggedIn()) {
			View view = inflater.inflate(R.layout.friends_fragment, container, false);
			
			Current_Location = (TextView)view.findViewById(R.id.currentstatus);
			
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
		} else {
			View view = inflater.inflate(R.layout.error_fragment, container, false);
			return view;
		}
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
	
	private class JSONObjectWrapper {
		JSONObject json = new JSONObject();

		public void JSONObjectWrapper(JSONObject obj) {
			json = obj;
		}
		public void setJsonObject(JSONObject obj) {
			json = obj;
		}
		public JSONObject getJsonObject() {
			return json;
		}
		public String toString() {
			return json.toString();
		}
	}
}
package com.hoos.around;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoos.around.ImageThreadLoader.ImageLoadedListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ScheduleFragment extends Fragment{
	
	private ArrayList<Class> ClassList = new ArrayList<Class>();
	private TextView detail_header;
	private TextView detail_time;
	private TextView detail_location;
	private ScheduleAdapter scheduleAdapter;
	private ListView ClassListView;
    private ImageThreadLoader imageLoader = new ImageThreadLoader();
    
    private Class selected_class;
	
	private class ScheduleAdapter extends ArrayAdapter<Class>{

	    Context context; 

	    public ScheduleAdapter(Context context, int layoutResourceId) {
	        super(context, layoutResourceId);
	        this.context = context;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	final Class tempClass = this.getItem(position);
    	    if (convertView == null) {
    	    	convertView = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.schedule_fragment_classlist, parent, false);
    	    }
	    	TextView label = (TextView)convertView.findViewById(R.id.name);
	    	
    	    label.setText(tempClass.course_mnem);
    	    return (convertView);
	    }

	}
	
	public void setDetail(Class class_Detail) {
		detail_header.setText(class_Detail.course_mnem);
		detail_location.setText(String.valueOf(class_Detail.location_id));
		detail_time.setText(class_Detail.course_start + " till " + class_Detail.course_end);
	}
	
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
						Log.d("JSON", "MSG RECIEVED");
						
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.friends_fragment_schedulelist);
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.schedule_fragment, container, false);
		
		ClassListView = (ListView)view.findViewById(R.id.scheduleList);
		ClassListView.setAdapter(scheduleAdapter);
		ClassListView.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
				   setDetail((Class)adapter.getItemAtPosition(position));
				   adapter.setSelection(position);
			   } 
			});
		
		detail_header = (TextView)view.findViewById(R.id.scheduleDetailHeader);
		detail_time = (TextView)view.findViewById(R.id.scheduleDetailTime);
		detail_location = (TextView)view.findViewById(R.id.scheduleDetailLocation);
		
		User temp_user = new User();
		temp_user.user_id = 1;
		
		LoadSchedule(temp_user);
		
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
	
	/*
	 * 
	 * 
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

	 * 
	 */
	
}
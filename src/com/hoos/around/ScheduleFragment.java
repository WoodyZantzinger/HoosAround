package com.hoos.around;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hoos.around.ImageThreadLoader.ImageLoadedListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Dialog;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ScheduleFragment extends Fragment{
	
	private List<Class> ClassList = new ArrayList<Class>();
	
	private TextView detail_header;
	private TextView detail_time;
	private TextView detail_location;
	private Button new_class;
	private ListView ClassListView;
	
	private ScheduleAdapter scheduleAdapter;
	
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
	
	public void LoadClasses() {
		RestClient.get("courses/view", null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray classes) {
                // Populate Class List
				try {
					
					for(int x = 0; x < classes.length(); x++) {
						Class temp = new Class();
						JSONObject JSONClasses = (JSONObject)classes.get(x);
						temp.course_id = JSONClasses.getJSONObject("Course").getInt("course_id");
						temp.course_start = JSONClasses.getJSONObject("Course").getString("course_start");
						temp.course_end = JSONClasses.getJSONObject("Course").getString("course_end");
						temp.course_mnem = JSONClasses.getJSONObject("Course").getString("course_mnem");
						temp.location_id = JSONClasses.getJSONObject("Course").getInt("location_id");
						ClassList.add(temp);
					}
					
					//MAKE BUTTON VISISLBE
					new_class.setClickable(true);
					
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
	
	public void LoadSchedule(User user) {
			
			RestClient.get("schedules/id/" + user.user_id, null, null, new JsonHttpResponseHandler() {
	            @Override
	            public void onSuccess(JSONArray classes) {
	                // Grab A Schedule
	            	try {
	            		Schedule new_schedule = RestClient.parse_schedule(classes);
						scheduleAdapter.clear();
						scheduleAdapter.addAll(new_schedule.courses);
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
		
		new_class = (Button)view.findViewById(R.id.scheduleListAdd);
		new_class.setClickable(false);
		LoadClasses();
		new_class.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				//
				// SET UP THE DIALOG THAT WILL POPUP WHEN THE ADD BUTTON IS CLICKED
				//
				
				final Dialog addDialog = new Dialog(arg0.getContext(), R.style.CustomDialogTheme);
				addDialog.setContentView(R.layout.add_class_dialog);
				
				final Spinner class_spinner = (Spinner) addDialog.findViewById(R.id.class_spinner);
				ArrayAdapter<Class> dataAdapter = new ArrayAdapter<Class>(arg0.getContext(),
					android.R.layout.simple_spinner_item, ClassList);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				class_spinner.setAdapter(dataAdapter);
				
				addDialog.show();
				
				//
				// SET UP THE BUTTON WITHIN THE DIALOG
				//
				
				Button close_btn = (Button) addDialog.findViewById(R.id.submit_button);
				close_btn.setOnClickListener(new View.OnClickListener() {
				    public void onClick(View v) {
				  
						RestClient.get("schedules/add/" + StaticUserInfo.getUserID() + "/" + ((Class)class_spinner.getSelectedItem()).course_id, null, null, new JsonHttpResponseHandler() {
				            @Override
				            public void onSuccess(JSONArray classes) {
				                // Grab A Schedule
				            	try {
				            		Schedule new_schedule = RestClient.parse_schedule(classes);
									scheduleAdapter.clear();
									scheduleAdapter.addAll(new_schedule.courses);
									scheduleAdapter.notifyDataSetChanged();
							    	addDialog.dismiss();
									Log.d("JSON", "MSG RECIEVED");
									
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									Log.d("JSON", e.getMessage());
							    	addDialog.dismiss();
								}
				            }
				            
				            @Override
				            public void onFailure(Throwable e, String response) {
								Log.d("JSON", response);
								Log.d("JSON", RestClient.getAbsoluteUrl("courses/view/"));
				            }
				            
				        });	
				    	
				    	
				    }
				});
				
			}
		});
		
		detail_header = (TextView)view.findViewById(R.id.scheduleDetailHeader);
		detail_time = (TextView)view.findViewById(R.id.scheduleDetailTime);
		detail_location = (TextView)view.findViewById(R.id.scheduleDetailLocation);
		
		User temp_user = new User();
		temp_user.user_id = StaticUserInfo.getUserID();
		
		LoadSchedule(temp_user);
		
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
	
}
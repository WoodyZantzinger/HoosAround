package com.hoos.around;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScheduleFragment extends Fragment{
	
	private double latitude, longitude;
	private TextView locationName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location current = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		latitude = current.getLatitude();
		longitude = current.getLatitude();
		
		String GPS_URL = "locations/gps/" + latitude  + "/" + longitude + "/1";
		
/*		
		final Handler handler = new Handler(){
			  @Override
			  public void handleMessage(Message msg) {
				  locationName.setText((String)msg.obj);
				  Log.d("JSON", (String)msg.obj);
				  Log.d("JSON", Thread.currentThread().getName());
				  super.handleMessage(msg);
			  }
			};
		
		
		RestClient.get(GPS_URL, null, handler, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray courses) {
                // Pull out location
                JSONObject nearest;
                String nearest_name = "";
				try {
					nearest = (JSONObject)courses.get(0);
					nearest_name = nearest.getJSONObject("Location").getString("location_name");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
				}

                // Do something with the response
				Message msg = handler.obtainMessage();
				msg.obj = nearest_name;
				handler.sendMessage(msg);

            }
            
            @Override
            public void onStart() {
            	super.onStart();
            }
            
            @Override
            public void onFinish() {
            	super.onFinish();
                // Completed the request (either success or failure)
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
				Log.d("JSON", response);
				Log.d("JSON", RestClient.getAbsoluteUrl("courses/view/"));
            }
            
        });
        */
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_fragment, container, false);
		locationName = (TextView)view.findViewById(R.id.location);
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
	
}
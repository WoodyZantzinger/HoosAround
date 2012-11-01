package com.hoos.around;

import java.util.List;
import java.util.Vector;

import org.json.*;

import com.loopj.android.http.JsonHttpResponseHandler;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FriendsFragment extends Fragment{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        RestClient.get("courses/view/", null, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray courses) {
                // Pull out the first event on the public timeline
                JSONObject firstClass;
                String ClassText = "";
				try {
					firstClass = (JSONObject)courses.get(0);
	                ClassText = firstClass.getJSONObject("Course").getString("course_mnem");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
				}

                // Do something with the response
				Log.d("JSON", ClassText);
            }
            
            @Override
            public void onFailure(Throwable e, String response) {
				Log.d("JSON", response);
				Log.d("JSON", RestClient.getAbsoluteUrl("courses/view/"));
            }
            
        });		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.friends_fragment, container, false);
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
}
package com.hoos.around;

import java.util.ArrayList;
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
import android.widget.ListView;
import android.widget.TextView;

public class FriendsFragment extends Fragment{
	
	private ArrayList<User> UserList = new ArrayList<User>();
	private UserAdapter userAdapter;
	private ListView UserListView;
	
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
    	    TextView label = (TextView)convertView;
    	    if (convertView == null) {
    	    	convertView = new TextView(this.context);
    	    	label = (TextView)convertView;
    	    }
    	    label.setText(tempUser.user_first + " " + tempUser.user_last);
    	    return (convertView);
	    }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userAdapter = new UserAdapter(getActivity(), android.R.layout.simple_list_item_1);
		
        RestClient.get("users/view/", null, null, new JsonHttpResponseHandler() {
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
						temp.schedule_id = JSONUser.getJSONObject("User").getInt("schedule_id");
						UserList.add(temp);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.d("JSON", e.getMessage());
				}

                // Do something with the response
				userAdapter.clear();
				userAdapter.addAll(UserList);
				userAdapter.notifyDataSetChanged();
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
		
		UserListView = (ListView)view.findViewById(R.id.friendsList);
		UserListView.setAdapter(userAdapter);
		return view;
	}
	
	public void setText(String item) {
		TextView view = (TextView) getView().findViewById(R.id.header);
		view.setText(item);
	}
}
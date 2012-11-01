package com.hoos.around;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class NavigationFragment extends ListFragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		String[] values = new String[] { "Home", "GPS Checkin", "List of Classes"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		getListView().setItemChecked(position, true);
		
		String item = (String) getListAdapter().getItem(position);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		if (item.toLowerCase().contains("home")) {
			HomeFragment frag = (HomeFragment)Fragment.instantiate(getActivity(), HomeFragment.class.getName());
			ft.replace(R.id.details, frag);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
		}
		
		if (item.toLowerCase().contains("gps")) {
            ScheduleFragment frag = (ScheduleFragment)Fragment.instantiate(getActivity(), ScheduleFragment.class.getName());
			ft.replace(R.id.details, frag);
			//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
		}
		 
		if (item.toLowerCase().contains("classes")) {
			FriendsFragment frag = (FriendsFragment)Fragment.instantiate(getActivity(), FriendsFragment.class.getName());
			ft.replace(R.id.details, frag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
		}
	}
}
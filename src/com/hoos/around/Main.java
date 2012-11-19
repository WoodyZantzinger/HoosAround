package com.hoos.around;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main extends Activity {
	
	class MyTabsListener implements ActionBar.TabListener {
		public Fragment fragment;
		 
		public MyTabsListener(Fragment fragment) {
			this.fragment = fragment;
		}
		 
		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			//Toast.makeText(StartActivity.appContext, "Reselected!", Toast.LENGTH_LONG).show();
		}
		 
		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.details, fragment);
		}
		 
		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
		}
		 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//ActionBar gets initiated
        ActionBar actionbar = getActionBar();
        //Tell the ActionBar we want to use Tabs.
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        
        //initiating both tabs and set text to it.
        ActionBar.Tab HomeTab = actionbar.newTab().setText("Home");
        ActionBar.Tab FriendsTab = actionbar.newTab().setText("Friends");
        ActionBar.Tab ScheduleTab = actionbar.newTab().setText("Schedule");
        ActionBar.Tab SettingsTab = actionbar.newTab().setText("Settings");
 
        Fragment HomeFrag = new HomeFragment();
        Fragment FriendsFrag = new FriendsFragment();
        Fragment ScheduleFrag = new ScheduleFragment();
        Fragment SettingsFrag = new SettingsFragment();
        
        //set the Tab listener. Now we can listen for clicks.
        HomeTab.setTabListener(new MyTabsListener(HomeFrag));
        FriendsTab.setTabListener(new MyTabsListener(FriendsFrag));
        ScheduleTab.setTabListener(new MyTabsListener(ScheduleFrag));
        SettingsTab.setTabListener(new MyTabsListener(SettingsFrag));
        
        //add the two tabs to the actionbar
        actionbar.addTab(HomeTab);
        actionbar.addTab(FriendsTab);
        actionbar.addTab(ScheduleTab);
        actionbar.addTab(SettingsTab);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		HomeFragment frag = (HomeFragment)Fragment.instantiate(this, HomeFragment.class.getName());
		ft.replace(R.id.details, frag);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
	}
	public void promptLogin(View view) {
		Intent intent = new Intent(this, Login.class);
		startActivity(intent);
	}
}

package com.kleward.bimp;

import java.util.ArrayList;
import java.util.HashMap;

import model.UserNotificationModel;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticUrl;
import adapter.UserNotificationArrayAdapter;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
/**
 * @author VIVEK
 * This fragment holds user notification stuff.
 */
public class UserNotificationFragment extends Fragment{
	// Define all global variables over here 
	ExpandableListView expandList;
	
	String brand_id;
	String brand_logo;
	String brand_name;
	String created_on;
	String description;
	String discount;
	String end_date;
	String notification_logo;
	String start_date;
	
	String user_id;
	String jsonString;
	
	NativeAlertDialog nad;
	SessionManagement session;
	UserNotificationModel unm;
	UserNotificationArrayAdapter listAdapter;
	ArrayList<UserNotificationModel> noti_array_list;
	View rootView;
	private ProgressDialog progressDialog;
	
	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.user_notification_fragment, container, false);
	
		session = new SessionManagement(getActivity());
		HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(session.KEY_UID);
		
        //  Check for manufacturer over here .. 
        String manufactures = android.os.Build.MANUFACTURER;
    	if(manufactures.equalsIgnoreCase("samsung"))
    	{
    		try{
    			// Setting Badges count to 0
                SharedPreferences spp = getActivity().getSharedPreferences("badge", 0);
        		SharedPreferences.Editor edit = spp.edit();
        		edit.putInt("count", 0);
        		edit.commit();
        		
        		ContentValues cv = new ContentValues();
        		cv.put("badgecount", 0);
        		getActivity().getContentResolver().update(Uri.parse("content://com.sec.badge/apps"), cv, "package=?", new String[] {getActivity().getPackageName()});	
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	else if(manufactures.equalsIgnoreCase("Sony"))
    	{
    		try{
    			SharedPreferences spp = getActivity().getSharedPreferences("badge", 0);
        		SharedPreferences.Editor edit = spp.edit();
        		edit.putInt("count", 0);
        		edit.commit();
        		
        		Intent in = new Intent();

        		in.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        		in.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", "com.kleward.bimp.Splash");
        		in.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", false);
        		in.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", "0");
        		in.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", "com.kleward.bimp");
        		getActivity().sendBroadcast(in);
    		}
    		catch(Exception exception)
    		{
    			exception.printStackTrace();
    		}
    	}
        
		initui();
		
		// Hit async Task over here .. 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new UserNotificationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
		} else {
			new UserNotificationAsyncTask().execute();
		}
		

		// Listview Group click listener work goes over here 
		expandList.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return false;
			}
		});

		expandList.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
			}
		});

		expandList.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {

			}
		});

		expandList.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				return false;
			}
		});
		
		return rootView;
	}
	
	// Initialise UI variables over here .. 
	private void initui() {
		expandList = (ExpandableListView) rootView.findViewById(R.id.noti_exp);
	}
	
	// User notification AsyncTask starts over here.. 
	private	class UserNotificationAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait ...");
			progressDialog.setIndeterminate(true);
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			try{
				JSONObject json = new JSONObject();
				json.put("X_REST_USERNAME", StaticUrl.X_REST_USERNAME);
				json.put("X_REST_PASSWORD", StaticUrl.X_REST_PASSWORD);
				//json.put("user_id", user_id);
				
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticUrl.USER_GET_PUSH);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return jsonString;
			}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			nad = new NativeAlertDialog();
			progressDialog.dismiss();
			if(result.equalsIgnoreCase("1") || result.equalsIgnoreCase("2"))
			{
				nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), getResources().getString(R.string.server_1));
			}
			else if(result.equalsIgnoreCase("3"))
			{
				nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), getResources().getString(R.string.server_2));
			}
			else{
				if(result.length() > 0)
				{
					try{
						JSONObject jsonObject = new JSONObject(result);
						JSONObject jObject = jsonObject.getJSONObject("GetNotificationList_response");
						
						String success = jObject.getString("success");
						String message = jObject.getString("message");
						if(success.equalsIgnoreCase("true"))
						{
							noti_array_list = new ArrayList<UserNotificationModel>();
							JSONArray jsonArray = jObject.getJSONArray("notification_list");
							for(int i = 0; i < jsonArray.length(); i++)
							{
								JSONObject object = jsonArray.getJSONObject(i);
								brand_id = object.getString("brand_id");
								brand_logo = object.getString("brand_logo");
								brand_name = object.getString("brand_name");
								created_on = object.getString("created_on");
								description = object.getString("description");
								discount = object.getString("discount");
								end_date = object.getString("end_date");
								notification_logo = object.getString("notification_logo");
								start_date = object.getString("start_date");
								
								unm = new UserNotificationModel();
								unm.brand_id = brand_id;
								unm.brand_logo = brand_logo;
								unm.brand_name = brand_name;
								unm.created_on = created_on;
								unm.description = description;
								unm.discount = discount;
								unm.end_date = end_date;
								unm.notification_logo = notification_logo;
								unm.start_date = start_date;
								
								noti_array_list.add(unm);
							}
							listAdapter = new UserNotificationArrayAdapter(getActivity(), noti_array_list);
							expandList.setAdapter(listAdapter); // Set adapter for Expandable list over hre 
						}
						else
						{
							nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), message);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), getResources().getString(R.string.server_3));
				}
				
			}
		}
	}
} // End of mabn class over here .. 

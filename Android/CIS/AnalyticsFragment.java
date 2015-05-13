package com.kleward.cis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import model.AnalyticsModel;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticUrl;
import adapter.AnalyticsArrayAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
/********************************************
 * @author VIVEK 
 * This class deals with APP ANALYTIC SECTION
 ********************************************/
public class AnalyticsFragment extends Fragment{
	
	TextView one, two, three, spinner;
	LinearLayout main_analytics;
	
	NativeAlertDialog nad;
	SessionManagement session;
	private ProgressDialog progressDialog;
	View rootView;
	
	ArrayList<AnalyticsModel> m_arrayList = null;
	AnalyticsModel am;
	AnalyticsArrayAdapter aaa;
	ListView lv;
	//ArrayList<String> list_array = new ArrayList<String>();
	
	String user_id = "";
	String region = "";
	String date_id = "";
	String t = "1";
	String jsonString = "";
	
	String Fatalities_Count = "";
	String Intensity_Level = "";
	String N_Critical_Incidents = "";
	String incident_name = "";
	String total_incident = "";
	String region_id = "";
	
	String list_str = "All,Most Recent concluded week,This Week,Last 7 days";
	List<String> list;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.analytics_fragment, container, false);
		
		/*list_array.add("All");
		list_array.add("Most Recent");
		list_array.add("This Week");
		list_array.add("Last 7 days");*/
		
		list = Arrays.asList(list_str.split("\\s*,\\s*"));
		
		session = new SessionManagement(getActivity());
		HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(session.KEY_UID);
        
        SharedPreferences spp = getActivity().getSharedPreferences("user_profile", 0);
        region_id = spp.getString("r_id", "");
        
        System.out.println("====================region_id ================= " + region_id + " ________ " + user_id);
        
        initui(); // Get all UI over here
        main_analytics.setVisibility(View.INVISIBLE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new AnalatycsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
		} else {
			new AnalatycsAsyncTask().execute();
		}
        
        spinner.setText("All");
        
        spinner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(getActivity());
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.analytics_dialog);
				dialog.setCancelable(true);
				
				ListView lv = (ListView) dialog.findViewById(R.id.analytics_dialog_list);
				
				@SuppressWarnings("unchecked")
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(),R.layout.serail_list_dialog_row, R.id.d_r_tv , list);
				lv.setAdapter(arrayAdapter);
				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						spinner.setText(list.get(arg2));
						dialog.cancel();
						
						if(arg2 == 0)
							date_id = "";
						if(arg2 == 1)
							date_id = "1";
						if(arg2 == 2)
							date_id = "2";
						if(arg2 == 3)
							date_id = "3";
						
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new AnalatycsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
						} else {
							new AnalatycsAsyncTask().execute();
						}
					}
				});
				dialog.show();
			}
		});
        
		return rootView;
	}
	
	
	private void initui() {
		one = (TextView) rootView.findViewById(R.id.N_Critical_Incidents);
		two = (TextView) rootView.findViewById(R.id.Intensity_Level);
		three = (TextView) rootView.findViewById(R.id.Fatalities_Count);
		main_analytics = (LinearLayout) rootView.findViewById(R.id.main_analytics);
		lv = (ListView) rootView.findViewById(R.id.analytics_list);
		spinner = (TextView) rootView.findViewById(R.id.spinner);
	}


	private	class AnalatycsAsyncTask extends AsyncTask<String, Void, String> {
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
				// Need to make this API dynamic over here.. 
				JSONObject json = new JSONObject();
				json.put("app_USERNAME", StaticUrl.X_REST_USERNAME);
				json.put("app_PASSWORD", StaticUrl.X_REST_PASSWORD);
				json.put("id", user_id); // Need to make this dynamic
				json.put("region", region_id); // Need to make this dynamic
				json.put("date_id", date_id);
				json.put("t", t);
				
				System.out.println("===================region check==================== " + json.toString());
				
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticUrl.APP_ANALYTICS);
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
					try
					{
						m_arrayList = new ArrayList<AnalyticsModel>();
						JSONObject jsonObject = new JSONObject(result);
						String success = jsonObject.getString("success");
						
						if(success.equalsIgnoreCase("true"))
						{
							JSONObject json = jsonObject.getJSONObject("list");
							Fatalities_Count = json.getString("Fatalities_Count");
							Intensity_Level = json.getString("Intensity_Level");
							N_Critical_Incidents = json.getString("N_Critical_Incidents");
							
							one.setText(N_Critical_Incidents);
							two.setText(Intensity_Level);
							three.setText(Fatalities_Count);
							
							JSONArray array = jsonObject.getJSONArray("incident_by_cate");
							for(int i = 0; i < array.length(); i++)
							{
								JSONObject obj = array.getJSONObject(i);
								incident_name = obj.getString("incident_name");
								total_incident = obj.getString("total_incident");
								
								am = new AnalyticsModel();
								am.incident_name = incident_name;
								am.total_incident = total_incident;
								m_arrayList.add(am);
							}
							main_analytics.setVisibility(View.VISIBLE);
							aaa = new AnalyticsArrayAdapter(getActivity(), m_arrayList, AnalyticsFragment.this);
							lv.setVisibility(View.VISIBLE);	
							lv.setAdapter(aaa);
							
							lv.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									
								}
							});
						}
						else
						{
							String message = jsonObject.getString("message");
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
	
	
}

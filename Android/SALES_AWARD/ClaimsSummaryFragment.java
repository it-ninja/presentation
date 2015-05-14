package com.kleward.salesaward;

import java.util.ArrayList;
import java.util.HashMap;

import model.ClaimSummaryModel;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticUrl;
import adapter.ClaimSummaryArrayAdapter;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
/**
 * @author VIVEK
 * This fragment class contains all claim summary section.
 */
public class ClaimsSummaryFragment extends Fragment{
	
	/** Define all global variables over here */
	View rootView;
	PullToRefreshListView pullToRefreshView;
	static final int MENU_MANUAL_REFRESH = 0;
	static final int MENU_DISABLE_SCROLL = 1;
	static final int MENU_SET_MODE = 2;
	static final int MENU_DEMO = 3;
	
	int refresh = 0;
	String jsonString;
	String user_id = "";
	
	NativeAlertDialog nad;
	SessionManagement session;
	
	ListView lv;
	ClaimSummaryArrayAdapter csaa;
	ClaimSummaryModel csm;
	ArrayList<ClaimSummaryModel> m_ArrayList = null;
	private ProgressDialog progressDialog;
	
	String product_name = "";
	String promotion_name = "";
	String serial_number = "";
	String vendor_status = "";
	String sales_award_status = "";
	String promotion_value_salesperson = "";
	String merchant_status = "";
	
	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.claims_summary_fragment, container, false);
		
		session = new SessionManagement(getActivity());
		session = new SessionManagement(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(session.KEY_UID);
		
        // Hit Claim summary asyncTask over here ... 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new ClaimSummaryAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
		} else {
			new ClaimSummaryAsyncTask().execute();
		}
		
		// Initialising pull to refresh lib over here ...
		pullToRefreshView = (PullToRefreshListView) rootView.findViewById(R.id.claim_summary_list);
        lv = pullToRefreshView.getRefreshableView();
        
        m_ArrayList = new ArrayList<ClaimSummaryModel>();
		m_ArrayList.add(new ClaimSummaryModel());
		pullToRefreshView.setOnPullEventListener(new PullToRefreshBase.OnPullEventListener<ListView>() {
			@Override
			public void onPullEvent(PullToRefreshBase<ListView> refreshView,State state, Mode direction) {
			}
		});
		
		// All pull to refresh update work goes over here... 
		pullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
				
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				
				refresh = m_ArrayList.size(); // Getting updated value from last position of array list
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new ClaimSummaryAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
				} else {
					new ClaimSummaryAsyncTask().execute();
				}
			}
		});
		pullToRefreshView.setMode(com.handmark.pulltorefresh.library.PullToRefreshBase.Mode.PULL_FROM_END);
		
		
		// Add an end-of-list listener
		pullToRefreshView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
			@Override
			public void onLastItemVisible() {
			}
		});
		
		// Getting actual list view reference over here .. 
		m_ArrayList.clear();
		csaa = new ClaimSummaryArrayAdapter(getActivity(), m_ArrayList, this);
		registerForContextMenu(lv);// Need to use the Actual ListView when registering for Context Menu
		lv.setAdapter(csaa);
		
		// List view click listener goes here ... 
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(), ClaimSummaryFragmentDetail.class);
				intent.putExtra("product_name", m_ArrayList.get(arg2 - 1).product_name);
				intent.putExtra("promotion_name", m_ArrayList.get(arg2 - 1).promotion_name);
				intent.putExtra("serial_number", m_ArrayList.get(arg2 - 1).serial_number);
				intent.putExtra("vendor_status", m_ArrayList.get(arg2 - 1).vendor_status);
				intent.putExtra("sales_award_status", m_ArrayList.get(arg2 - 1).sales_award_status);
				intent.putExtra("promotion_value_salesperson", m_ArrayList.get(arg2 - 1).promotion_value_salesperson);
				intent.putExtra("merchant_status", m_ArrayList.get(arg2 - 1).merchant_status);
				startActivity(intent);
			}
		});
		
		return rootView;
	}
	
	// All Claim summary AsyncTask starts over here.. 
	private	class ClaimSummaryAsyncTask extends AsyncTask<String, Void, String> {
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
				json.put("contact_id", user_id);
				json.put("offset", String.valueOf(refresh));
				
				System.out.println("=======================refresh value is ================== " +  refresh);
				
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticUrl.CLAIMS_SUMMARY);
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
						JSONObject sObject = jsonObject.getJSONObject("sucessResult");
						String Success = sObject.getString("Success");
						String Message = sObject.getString("Message");
						
						if(Success.equalsIgnoreCase("True"))
						{
							JSONArray jsonArray = jsonObject.getJSONArray("claim_summary");
							for(int i = 0; i < jsonArray.length(); i++)
							{
								JSONObject object = jsonArray.getJSONObject(i);
								product_name = object.getString("product_name");
								promotion_name = object.getString("promotion_name");
								serial_number = object.getString("serial_number");
								vendor_status = object.getString("vendor_status");
								sales_award_status = object.getString("sales_award_status");
								promotion_value_salesperson = object.getString("promotion_value_salesperson");
								merchant_status = object.getString("merchant_status");
								
								csm = new ClaimSummaryModel();
								csm.product_name = product_name;
								csm.promotion_name = promotion_name;
								csm.serial_number = serial_number;
								csm.vendor_status = vendor_status;
								csm.sales_award_status = sales_award_status;
								csm.promotion_value_salesperson = promotion_value_salesperson;
								csm.merchant_status = merchant_status;
								
								m_ArrayList.add(csm); // Adding data to array list
							}
						}
						else
						{
							nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), Message);
						}
						
						csaa.notifyDataSetChanged();
						pullToRefreshView.onRefreshComplete();
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

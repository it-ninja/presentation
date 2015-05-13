package com.kleward.asp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import utils.ImageLoader;
import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticCredentials;

public class PartyTicket extends Fragment{
	
	ImageView party_ticket;
	TextView party_tv;
	RelativeLayout paryid;
	
	View rootView;
	
	String can_give_away = "";
	String can_plus_one = "";
	String give_away_button_text = "";
	String give_away_confirm_box_message = "";
	String plus_one_button_text = "";
	String plus_one_confirm_box_message = "";
	String swipe_give_away_text1 = "";
	String swipe_give_away_text2 = "";
	String swipe_plus_one_text = "";
	String ticket_image = "";
	String ticket_text_line1 = "";
	String ticket_text_line2 = "";
	String ticket_text_line3 = "";
	
	private ProgressDialog progressDialog;
	NativeAlertDialog nad;
	String jsonString = "";
	SessionManagement session;
	String user_id = "";
	String party_key = "";
	String session_token = "";
	ImageLoader imageLoader;
	
	Typeface bold;
	Picasso picasso;

	private Tracker tracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//===================GA=====================
		tracker = GoogleAnalyticsApp.getTracker(getActivity());
		tracker.setScreenName("PartyTicketScreen");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.party_ticket, container, false);
	
		picasso = Picasso.with(getActivity());
		
		session = new SessionManagement(getActivity());
	    HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(session.KEY_UID); // Use this for User ID.
        SharedPreferences sp = getActivity().getSharedPreferences("party_info", 0);
        party_key = sp.getString("party_key", "");
        session_token = sp.getString("session_token", "");
        imageLoader = new ImageLoader(getActivity());
        
		initui();
		
		paryid.setVisibility(View.INVISIBLE);
		
		bold = Typeface.createFromAsset(getActivity().getAssets(), "circular_bold.ttf" );
		party_tv.setTypeface(bold);
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new UserticketAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
		} else {
			new UserticketAsyncTask().execute();
		}
		
		return rootView;
	}

	private void initui() {
		party_ticket = (ImageView) rootView.findViewById(R.id.party_ticket);
		party_tv = (TextView) rootView.findViewById(R.id.party_tv);
		paryid = (RelativeLayout) rootView.findViewById(R.id.paryid);
	}
	
	
	private	class UserticketAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setMessage("Please wait ...");
			progressDialog.setCancelable(false);
			progressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			try{
				
				JSONObject json = new JSONObject();
				json.put("X_REST_USERNAME", StaticCredentials.X_REST_USERNAME);
				json.put("X_REST_PASSWORD", StaticCredentials.X_REST_PASSWORD);
				json.put("user_id", user_id);
				json.put("party_key", party_key);
				json.put("session_token", session_token);
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticCredentials.APP_USER_TICKET_INFO);
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
						JSONObject jsonObject = new JSONObject(result);
						String status = jsonObject.getString("status");
						if(status.equalsIgnoreCase("true"))
						{
							paryid.setVisibility(View.VISIBLE);
							
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new UpdateticketAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
							} else {
								new UpdateticketAsyncTask().execute();
							}
							
							JSONObject object = jsonObject.getJSONObject("result");
							ticket_image = object.getString("ticket_image");
							
							picasso.load(ticket_image).placeholder(R.drawable.background_transparent).priority(Picasso.Priority.HIGH)
					        .fit().centerCrop()
					        .into(party_ticket);
							
							ticket_text_line1 = object.getString("ticket_text_line1");
							ticket_text_line2 = object.getString("ticket_text_line2");
							ticket_text_line3 = object.getString("ticket_text_line3");
							
							party_tv.setText(ticket_text_line1 + " " + ticket_text_line2 + " " + ticket_text_line3);
							
							can_give_away = object.getString("can_give_away");
							can_plus_one = object.getString("can_plus_one");
							give_away_button_text = object.getString("give_away_button_text");
							give_away_confirm_box_message = object.getString("give_away_confirm_box_message");
							plus_one_button_text = object.getString("plus_one_button_text");
							plus_one_confirm_box_message = object.getString("plus_one_confirm_box_message");
							swipe_give_away_text1 = object.getString("swipe_give_away_text1");
							swipe_give_away_text2 = object.getString("swipe_give_away_text2");
							swipe_plus_one_text = object.getString("swipe_plus_one_text");
							
						}
						else
						{
							paryid.setVisibility(View.INVISIBLE);
							String message = jsonObject.getString("message");
							if(message.equalsIgnoreCase("user_not_exist"))
								showExpireUserDialog(getActivity(), getResources().getString(R.string.expire1));
							else if(message.equalsIgnoreCase("user_inactive"))
								showExpireUserDialog(getActivity(), getResources().getString(R.string.expire2));
							else if(message.equalsIgnoreCase("session_expired"))
								showExpireUserDialog(getActivity(), getResources().getString(R.string.expire3));
							else
								nad.showNativeAlertDialog(getActivity(), "", message);
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
	
	private void showExpireUserDialog(final Context context,String message)
	{
		//Typeface regular = Typeface.createFromAsset(context.getAssets(), "circular_regular.ttf" );
		Typeface bold = Typeface.createFromAsset(context.getAssets(), "circular_bold.ttf" );
		final Dialog dialog = new Dialog(context);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.restrict_user_alert_dialog);
		dialog.setCancelable(false);
		TextView native_content = (TextView) dialog.findViewById(R.id.restrict_title); native_content.setTypeface(bold);
		native_content.setText(message);
		TextView restrict_btn = (TextView) dialog.findViewById(R.id.restrict_btn); restrict_btn.setTypeface(bold);
		restrict_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				session.logoutUser();
				dialog.cancel();
				getActivity().finish();
			}
		});
		dialog.show();
		
	}
	
	private	class UpdateticketAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
		}
		@Override
		protected String doInBackground(String... params) {
			try{
				
				JSONObject json = new JSONObject();
				json.put("X_REST_USERNAME", StaticCredentials.X_REST_USERNAME);
				json.put("X_REST_PASSWORD", StaticCredentials.X_REST_PASSWORD);
				json.put("user_id", user_id);
				json.put("party_key", party_key);
				json.put("session_token", session_token);
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticCredentials.aPP_USER_UPDATE_TICKET_INFO);
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

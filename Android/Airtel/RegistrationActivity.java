package com.kleward.asp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import utils.CommonMethodStuff;
import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.StaticCredentials;

public class RegistrationActivity extends ActionBarActivity{

	TextView register_one, register_two,  register_three, register_four,  register_five,  register_six,  
	register_eight,  register, register_ten, register_eleven, eone, etwo;
	EditText register_name, register_email,  register_cell, register_pass,  register_confirm_pass;
	LinearLayout register_go_login;
	CheckBox register_check;
	
	Typeface bold, regular;
	
	String jsonString = "";
	String name = "";
	String email = "";
	String cell = "";
	String pass = "";
	String repass = "";
	String gcm_id = "";
	String is_checked = "0"; // Here "1" means that user has accepted term's and condition else use has not accepted term's and condition.
	
	CommonMethodStuff cms;
	NativeAlertDialog nad;
	private ProgressDialog progressDialog;
	ActionBar actionBar;

	private Tracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.registration_activity);

		//===================GA=====================
		tracker = GoogleAnalyticsApp.getTracker(this);
		tracker.setScreenName("RegistrationScreen");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
		
		SharedPreferences sp = getSharedPreferences("gcm", 0);
        gcm_id = sp.getString("gcm_id", "");
		
		cms = new CommonMethodStuff();
		
		initui();

		actionBar = getSupportActionBar();
		actionBar.hide();
		
		bold = Typeface.createFromAsset( getAssets(), "circular_bold.ttf" );
		regular = Typeface.createFromAsset( getAssets(), "circular_regular.ttf" );
		
		String s= " I agree to all the TERMS & CONDITIONS of the campaign";
		SpannableString ss1=  new SpannableString(s);
		ss1.setSpan(new RelativeSizeSpan(1f), 19,36, 0); // set size
		ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 0, 0);// set color
		register_eight.setText(ss1); 
		
		register_eight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegistrationActivity.this, TermsAndCondition.class);
				startActivityForResult(intent, 30);
			}
		});
		
		eone.setTypeface(regular);
		etwo.setTypeface(bold);
		etwo.setPaintFlags(etwo.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		
		//register_one.setTypeface(regular);
		register_two.setTypeface(regular);
		register_three.setTypeface(regular);
		register_four.setTypeface(regular);
		register_five.setTypeface(regular);
		register_six.setTypeface(regular);
		register_eight.setTypeface(bold);
		register.setTypeface(bold);
		register_ten.setTypeface(regular);
		register_eleven.setTypeface(regular);
		
		register_name.setTypeface(regular);
		register_email.setTypeface(regular);
		register_cell.setTypeface(regular);
		register_pass.setTypeface(regular);
		register_confirm_pass.setTypeface(regular);
		
		// Register button click listener 
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				name = register_name.getText().toString();
				email = register_email.getText().toString();
				cell = register_cell.getText().toString();
				pass = register_pass.getText().toString();
				repass = register_confirm_pass.getText().toString();
				
				Animation shake = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.shake);
				
				if(name.length() == 0 || email.length() == 0 || cell.length() == 0 || pass.length() == 0 || repass.length() == 0)
				{
					
					if(repass.length() == 0)
					{
						register_confirm_pass.requestFocus();
						register_confirm_pass.startAnimation(shake);
						register_confirm_pass.setError(getResources().getString(R.string.v_pass_four));
					}
					
					if(pass.length() == 0)
					{
						register_pass.requestFocus();
						register_pass.startAnimation(shake);
						register_pass.setError(getResources().getString(R.string.v_pass_one));
					}
					
					if(cell.length() == 0)
					{
						register_cell.requestFocus();
						register_cell.startAnimation(shake);
						register_cell.setError(getResources().getString(R.string.v_phone_one));
					}
					
					if(email.length() == 0)
					{
						register_email.requestFocus();
						register_email.startAnimation(shake);
						register_email.setError(getResources().getString(R.string.v_email_one));
					}
					
					if(name.length() == 0)
					{
						register_name.requestFocus();
						register_name.startAnimation(shake);
						register_name.setError(getResources().getString(R.string.v_name));
					}
					
					return;
				}
				
				if(!cms.checkEmail(email) || !cms.checkMobile(cell) || !pass.equals(repass) || pass.length() < 6)
				{
					if(!pass.equals(repass))
					{
						register_confirm_pass.requestFocus();
						register_confirm_pass.startAnimation(shake);
						register_confirm_pass.setError(getResources().getString(R.string.v_pass_three));
					}
					
					if(pass.length() < 6)
					{
						register_pass.requestFocus();
						register_pass.startAnimation(shake);
						register_pass.setError(getResources().getString(R.string.v_pass_two));
					}
					
					if(!cms.checkMobile(cell))
					{
						register_cell.requestFocus();
						register_cell.startAnimation(shake);
						register_cell.setError(getResources().getString(R.string.v_phone_two));
					}
					
					if(!cms.checkEmail(email))
					{
						register_email.requestFocus();
						register_email.startAnimation(shake);
						register_email.setError(getResources().getString(R.string.v_email_two));
					}
					
					return;
				}
				
				if(register_check.isChecked() == true)
				{
					is_checked = "1";
					
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new UserRegistrationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
					} else {
						new UserRegistrationAsyncTask().execute();
					}

				}
				else
				{
					final Dialog dialog = new Dialog(RegistrationActivity.this);
					dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.restrict_user_alert_dialog);
					dialog.setCancelable(false);
					TextView native_content = (TextView) dialog.findViewById(R.id.restrict_title);
					native_content.setText("Please accept to Terms & Conditions to register.");
					TextView restrict_btn = (TextView) dialog.findViewById(R.id.restrict_btn);
					restrict_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dialog.cancel();
						}
					});
					dialog.show();
				}
			}
		});
		
		// Go to login click listener
		register_go_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	private void initui() 
	{
		register_one = (TextView) findViewById(R.id.register_one);
		register_two = (TextView) findViewById(R.id.register_two);
		register_three = (TextView) findViewById(R.id.register_three);
		register_four = (TextView) findViewById(R.id.register_four);
		register_five = (TextView) findViewById(R.id.register_five);
		register_six = (TextView) findViewById(R.id.register_six);
		register_eight = (TextView) findViewById(R.id.register_eight);
		register = (TextView) findViewById(R.id.register);
		register_ten = (TextView) findViewById(R.id.register_ten);
		register_eleven = (TextView) findViewById(R.id.register_eleven);
		
		register_name = (EditText) findViewById(R.id.register_name);
		register_email = (EditText) findViewById(R.id.register_email);
		register_cell = (EditText) findViewById(R.id.register_cell);
		register_pass = (EditText) findViewById(R.id.register_pass);
		register_confirm_pass = (EditText) findViewById(R.id.register_confirm_pass);
		
		register_go_login = (LinearLayout) findViewById(R.id.register_go_login);
		
		register_check = (CheckBox) findViewById(R.id.register_check);
		
		eone = (TextView) findViewById(R.id.eone);
		etwo = (TextView) findViewById(R.id.etwo);
	}
	
	private	class UserRegistrationAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(RegistrationActivity.this);
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
				json.put("device_type", StaticCredentials.X_DEVICE_TYPE);
				json.put("user_name", name);
				json.put("user_email", email);
				json.put("user_mobile", cell);
				json.put("device_token", gcm_id);
				json.put("user_password", cms.encodeTextToBase64(pass));
				json.put("is_term", is_checked);
				
				jsonString = ParseJsonData.getParsedData(RegistrationActivity.this, json.toString(), StaticCredentials.APP_USER_REGISTRATION);
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
				nad.showNativeAlertDialog(RegistrationActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_1));
			}
			else if(result.equalsIgnoreCase("3"))
			{
				nad.showNativeAlertDialog(RegistrationActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_2));
			}
			else{
				if(result.length() > 0)
				{
					try
					{

						JSONObject jsonObject = new JSONObject(result);
						String status = jsonObject.getString("status");
						String message = jsonObject.getString("message");
						System.out.println("=============messagemessaheaschaa========== " + message);
						if(status.equalsIgnoreCase("true"))
						{
							final Dialog dialog = new Dialog(RegistrationActivity.this);
							dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
							dialog.setContentView(R.layout.restrict_user_alert_dialog);
							dialog.setCancelable(false);
							TextView native_content = (TextView) dialog.findViewById(R.id.restrict_title);
							native_content.setText(message);
							TextView restrict_btn = (TextView) dialog.findViewById(R.id.restrict_btn);
							restrict_btn.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									dialog.cancel();
									finish();
								}
							});
							dialog.show();
						}
						else
						{
							final Dialog dialog = new Dialog(RegistrationActivity.this);
							dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
							dialog.setContentView(R.layout.restrict_user_alert_dialog);
							dialog.setCancelable(false);
							TextView native_content = (TextView) dialog.findViewById(R.id.restrict_title);
							native_content.setText(message);
							TextView restrict_btn = (TextView) dialog.findViewById(R.id.restrict_btn);
							restrict_btn.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									dialog.cancel();
								}
							});
							dialog.show();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					nad.showNativeAlertDialog(RegistrationActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_3));
				}
				
			}
			
		}
	}
	

}

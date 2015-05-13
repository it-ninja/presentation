package com.kleward.bimp;

import org.json.JSONObject;
import utils.CommonMethodStuff;
import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticUrl;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author vivek
 * This class is used to verify OTP code send to user.
 */
public class OTPVerification extends ActionBarActivity{

	TextView cell, error, font_send, actionBarTitle, back,otp_tv1, otp_tv2, otp_tv3, otp_tv4,otp_tv5, otp_tv6, otp_tv7;
	EditText code;
	LinearLayout verify, resend, actionBarBack;
	String gcm_id = "";
	String cell_number = "";
	String et_code;
	String random_num = "1626";
	String jsonString = "";
	String user_id;
	String f_user_id = "";
	String login_type = "";
	
	SessionManagement sm;
	NativeAlertDialog nad;
	CommonMethodStuff cms;
	private ActionBar actionBar;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_verification);
		
		cms = new CommonMethodStuff();
		sm = new SessionManagement(OTPVerification.this);
		
		SharedPreferences sp = getSharedPreferences("user_credentials", 0);
        gcm_id = sp.getString("gcm_id", "");
        
        SharedPreferences spp = getSharedPreferences("facebook", 0);
        f_user_id = spp.getString("user_id", "");
        
        SharedPreferences sppp = getSharedPreferences("login_type", 0);
        login_type = sppp.getString("type", "");
		
		initui();
		
		// Setting font icon over here ..
		Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		Typeface fontText = Typeface.createFromAsset( getAssets(), "Roboto-Light.ttf" );
		Typeface fontText1 = Typeface.createFromAsset( getAssets(), "Roboto-Medium.ttf" );
		
		font_send.setTypeface(font);
		
		error.setVisibility(View.GONE); // Make it non visible unless error occur.
		
		// Action Bar initial work goes over here ..
		actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.common_action_bar_view);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.blue));
		
		actionBarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.title_tv);
		actionBarBack = (LinearLayout) actionBar.getCustomView().findViewById(R.id.back_ll);
		back = (TextView) actionBar.getCustomView().findViewById(R.id.back_tv);
		
		back.setTypeface(font);
		actionBarTitle.setText("Sign Up");
		actionBarTitle.setTypeface(fontText1);
		actionBarBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(OTPVerification.this, SignUpActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		try{
			Bundle extra = getIntent().getExtras();
			if (savedInstanceState == null) {

				if (extra == null) {
					cell_number = "";
				} else {
					cell_number = extra.getString("cell");
				}
				} else {
					cell_number = (String) savedInstanceState.getSerializable("cell");
				}
		}
		catch(Exception e){ e.printStackTrace();}
		
		cell.setText(cell_number);// Set's entered cell#
		
		code.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				if(arg3 == 0)
				{
					error.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
		
		// Verify button click listener.
		verify.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedPreferences spp = getSharedPreferences("random", 0);
				String getCode = spp.getString("random", "");
				
				et_code = code.getText().toString();
				
				Animation shake = AnimationUtils.loadAnimation(OTPVerification.this, R.anim.shake);
				if(et_code.length() == 0)
				{
					code.requestFocus();
					code.startAnimation(shake);
					code.setError(getResources().getString(R.string.hard_19));
					return;
				}
				
				if(getCode.equalsIgnoreCase(et_code))
				{
					if(login_type.equalsIgnoreCase("social"))
					{
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new RegistrationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
						} else {
							new RegistrationAsyncTask().execute();
						}
					}
					else
					{
						Intent intent = new Intent(OTPVerification.this, RegistrationActivity.class);
						intent.putExtra("cell", cell_number);
						startActivity(intent);
						finish();
					}
					
					/**/
				}
				else
				{
					error.setVisibility(View.VISIBLE);
				}
			}
		});
		
		// Re-send button click listener.
		resend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				random_num = cms.generateRandomNumber();
				System.out.println("============Rondom number is==========" + random_num);
				SharedPreferences spp = getSharedPreferences("random", 0);
				SharedPreferences.Editor edit = spp.edit();
				edit.putString("random", random_num);
				edit.commit();
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					new AuthCellAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
				} else {
					new AuthCellAsyncTask().execute();
				}
			}
		});
		
		
		otp_tv1.setTypeface(fontText);
		otp_tv2.setTypeface(fontText);
		otp_tv3.setTypeface(fontText);
		otp_tv4.setTypeface(fontText);
		otp_tv5.setTypeface(fontText);
		otp_tv6.setTypeface(fontText);
		otp_tv7.setTypeface(fontText);
		cell.setTypeface(fontText);
		code.setTypeface(fontText);
		error.setTypeface(fontText);
	}
	
	// User registration AsyncTask starts over here.. 
	private	class RegistrationAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(OTPVerification.this);
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
				json.put("user_name", "");
				json.put("user_email", "");
				json.put("user_phone", cell_number);
				json.put("user_age", "");
				json.put("user_sex", "");
				json.put("user_city", "");
				json.put("push_notification_id", gcm_id);
				json.put("registration_type", "social");
				json.put("facebook_user_id", f_user_id);
				json.put("user_password", "");
				System.out.println("f_user_id " + f_user_id + " __ " + gcm_id + " __ " + cell_number);
				
				jsonString = ParseJsonData.getParsedData(OTPVerification.this, json.toString(), StaticUrl.USER_REGISTRATION);
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
				nad.showNativeAlertDialog(OTPVerification.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_1));
			}
			else if(result.equalsIgnoreCase("3"))
			{
				nad.showNativeAlertDialog(OTPVerification.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_2));
			}
			else{
				if(result.length() > 0)
				{
					try{
						JSONObject jObject = new JSONObject(result);
						JSONObject object = jObject.getJSONObject("UserMailRegistration_response");
						String success = object.getString("success");
						if(success.equalsIgnoreCase("true"))
						{
							
							JSONObject dataObject = jObject.getJSONObject("data");
							user_id = dataObject.getString("userId");
							
							sm.createLoginSession("", user_id);
							
							// Adding user_id to preference file .. This also contains gcm_id
							SharedPreferences spp = getSharedPreferences("user_credentials", 0);
							SharedPreferences.Editor edit = spp.edit();
							edit.putString("user_id", user_id);
							edit.commit();
							
							// Clearing generated random number for OTP code
							SharedPreferences sp = getSharedPreferences("random", 0);
							SharedPreferences.Editor e = sp.edit();
							e.clear();
							e.commit();
							
							Intent intent = new Intent(OTPVerification.this, DashBoardScreen.class);
							startActivity(intent);
							finish();
						}
						else
						{
							String message = object.getString("message");
							nad.showNativeAlertDialog(OTPVerification.this, getResources().getString(R.string.app_name) , message);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					nad.showNativeAlertDialog(OTPVerification.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_3));
				}
				
			}
		}
	}

	private void initui() {
		cell = (TextView) findViewById(R.id.otp_cell);
		code = (EditText) findViewById(R.id.otp_code);
		verify = (LinearLayout) findViewById(R.id.otp_verify);
		error = (TextView) findViewById(R.id.otp_error);
		resend = (LinearLayout) findViewById(R.id.otp_resend);
		font_send = (TextView) findViewById(R.id.font_send);
		otp_tv1 = (TextView) findViewById(R.id.otp_tv1);
		otp_tv2  = (TextView) findViewById(R.id.otp_tv2);
		otp_tv3 = (TextView) findViewById(R.id.otp_tv3);
		otp_tv4 = (TextView) findViewById(R.id.otp_tv4);
		otp_tv5 = (TextView) findViewById(R.id.otp_tv5);
		otp_tv6 = (TextView) findViewById(R.id.otp_tv6);
		otp_tv7 = (TextView) findViewById(R.id.otp_tv7);
	}
	
	// Authenticate mobile AsyncTask starts over here.. 
	private	class AuthCellAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(OTPVerification.this);
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
				json.put("phone_no", cell_number); // Need to update with new number dialog.
				json.put("otp_code", random_num);
				
				jsonString = ParseJsonData.getParsedData(OTPVerification.this, json.toString(), StaticUrl.OTP_VERIFICATION);
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
			progressDialog.dismiss();
			try{
				if(result.length() > 0)
				{
					JSONObject jObject = new JSONObject(result);
					JSONObject object = jObject.getJSONObject("UserOTPVerification_response");
					String success = object.getString("success");
					
					if(success.equalsIgnoreCase("true"))
					{
						Toast.makeText(getApplicationContext(), "Code successfully send over your number.", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Some thing went wrong", Toast.LENGTH_SHORT).show();
					}
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(OTPVerification.this, SignUpActivity.class);
		startActivity(intent);
		finish();
	}


}

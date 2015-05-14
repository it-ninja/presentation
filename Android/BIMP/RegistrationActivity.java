package com.kleward.bimp;

import java.util.ArrayList;
import java.util.Calendar;

import model.CityModel;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.CommonMethodStuff;
import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticUrl;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * @author VIVEK
 * This class contains final registration stuff. 
 */
public class RegistrationActivity extends ActionBarActivity{

	/**
	 * Define all global UI variables over here 
	 */
	EditText email, pass, re_pass, age, sex, city;
	//CheckBox cb;
	TextView t_c, actionBarTitle, back, reg_tv1, reg_tv2, reg_tv3, reg_tv4;
	LinearLayout sign_up, cb_ll, actionBarBack;
	
	/**
	 * Define class UI variables over here ...  
	 */
	String user_name = "";
	String user_pass = "";
	String user_rpass = "";
	String user_age = "";
	String user_sex = "";
	String user_city = "";
	String jsonString = "";
	String cell_number = "";
	String gcm_id = "";
	String user_id;
	String registration_type = ""; // Need to change this on later terms
	
	String f_user_name = "";
	String f_user_email = "";
	String f_user_sex = "";
	//String user_age_date_string;
	
	String city_id = "";
	String city_name = "";
	
	private Calendar cal;
	private int day;
	private int month;
	private int year;
	int age_check_flag = 0;
	
	/**
	 * Globally define classes over here .. 
	 */
	CommonMethodStuff cms;
	SessionManagement sm;
	NativeAlertDialog nad;
	CityModel cityModel;
	private ArrayList<CityModel> cityArrayList = null;
	private ActionBar actionBar;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);
		
		// Hit asynctask to get city details 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			new CityAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
		} else {
			new CityAsyncTask().execute();
		}
		
		cms = new CommonMethodStuff();
		sm = new SessionManagement(RegistrationActivity.this);
		
		// Shared Pref for USER GCM ID
		SharedPreferences sp = getSharedPreferences("user_credentials", 0);
        gcm_id = sp.getString("gcm_id", "");
        
        SharedPreferences spp = getSharedPreferences("login_type", 0);
        registration_type = spp.getString("type", "");
        
        SharedPreferences share = getSharedPreferences("facebook", 0);
        f_user_name = share.getString("user_name", "");
        f_user_email = share.getString("user_email", "");
        f_user_sex = share.getString("user_sex", "");
        if(f_user_sex.equalsIgnoreCase("male"))
        	f_user_sex = "Male";
        else
        	f_user_sex = "Female";
        
		initui(); // Initialise all UI variables over here .. 
		
		// Email btn click listener goes over here .. 
		email.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				email.setError(null);
			}
		});
		
		// Pass btn click listener goes over here ... 
		pass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				pass.setError(null);
			}
		});
		
		// Repass btn click listener goes over here .. 
		re_pass.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				re_pass.setError(null);
			}
		});
		
		// City btn click listener goes over here ... 
		city.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				city.setError(null);
				// Show over popup menu over here 
				PopupMenu menu = new PopupMenu(RegistrationActivity.this, city);
				if(cityArrayList.size() > 0)
				{
					for(int cat = 0; cat < cityArrayList.size(); cat++)
					{
						menu.getMenu().add(Menu.NONE, cat, Menu.NONE, cityArrayList.get(cat).city_name);
					}
				}
				menu.show();
				
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						city.setText(cityArrayList.get(item.getItemId()).city_name);
						return true;
					}
				});
				
			}
		});
		
		// Get all TypeFace over here .. 
		Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		Typeface fontText = Typeface.createFromAsset( getAssets(), "Roboto-Light.ttf" );
		Typeface fontText1 = Typeface.createFromAsset( getAssets(), "Roboto-Medium.ttf" );
		
		
		// Set typeface over here .. 
		reg_tv1.setTypeface(fontText);
		reg_tv2.setTypeface(fontText);
		reg_tv3.setTypeface(fontText);
		reg_tv4.setTypeface(fontText);
		email.setTypeface(fontText); pass.setTypeface(fontText); re_pass.setTypeface(fontText); age.setTypeface(fontText); sex.setTypeface(fontText); 
		city.setTypeface(fontText);
		
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
				Intent intent = new Intent(RegistrationActivity.this, LoginTypeActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		// Taking reference from LoginTypeActivity class
		if(registration_type.equalsIgnoreCase("social"))
		{
			pass.setVisibility(View.GONE);
			re_pass.setVisibility(View.GONE);
			
			email.setText(f_user_email);
			sex.setText(f_user_sex);
		}
		
		// Get all class bundle value over here .. 
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
		
		// Age work goes over here
		cal = Calendar.getInstance();
		day = cal.get(Calendar.DAY_OF_MONTH);
		month = cal.get(Calendar.MONTH);
		year = cal.get(Calendar.YEAR);
		
		// Age btn click listener goes over here 
		age.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {
				age.setError(null);
				showDialog(0); // Opens date dailog
			}
		});
		
		// Sex work goes over here 
		sex.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sex.setError(null);
				PopupMenu menu = new PopupMenu(RegistrationActivity.this, sex);

				menu.getMenu().add("Male");
				menu.getMenu().add("Female");
				menu.show();
				
				menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						sex.setText(item.getTitle().toString());
						return true;
					}
				});
			}
		});
		
		/**
		 * NEED TO REMOVE ViewWeb.java class
		 */
		// Terms and Condition custom view.
		/*TextView tv = (TextView) findViewById(R.id.tv);
		tv.setText(" ");
		t_c.setText("terms & conditions");
		t_c.setPaintFlags(t_c.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		t_c.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(RegistrationActivity.this, ViewWeb.class);
				startActivity(intent);
			}
		});*/
		
		// Sign Up button click listener.
		sign_up.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				user_name = email.getText().toString();
				user_pass = pass.getText().toString();
				user_rpass = re_pass.getText().toString();
				//user_age = age.getText().toString();
				user_sex = sex.getText().toString();
				user_city = city.getText().toString();
				
				Animation shake = AnimationUtils.loadAnimation(RegistrationActivity.this, R.anim.shake); // Anim for UI
				
				// Validation work goes over here .. 
				if(registration_type.equalsIgnoreCase("social"))
				{
					if(user_name.length() == 0 || user_age.length() == 0 || user_sex.length() == 0  || user_city.length() == 0)
					{
						if(user_city.length() == 0)
						{
							city.requestFocus();
							city.startAnimation(shake);
							city.setError(getResources().getString(R.string.rcity));
						}
						if(user_sex.length() == 0)
						{
							sex.requestFocus();
							sex.startAnimation(shake);
							sex.setError(getResources().getString(R.string.rsex));
						}
						if(user_age.length() == 0)
						{
							age.requestFocus();
							age.startAnimation(shake);
							age.setError(getResources().getString(R.string.rage));
						}
						if(user_name.length() == 0)
						{
							email.requestFocus();
							email.startAnimation(shake);
							email.setError(getResources().getString(R.string.hard_5));
						}
						return;
					}
					
					if(!cms.checkEmail(user_name))
					{
						if(user_name.length() == 0)
						{
							email.requestFocus();
							email.startAnimation(shake);
							email.setError(getResources().getString(R.string.hard_7));
						}
						return;
					}
					else
					{
						// Hit Async Task over here.. 
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new RegistrationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
						} else {
							new RegistrationAsyncTask().execute();
						}
					}
						
					/*if(cb.isChecked() == true)
					{
						// Hit Async Task over here.. 
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new RegistrationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
						} else {
							new RegistrationAsyncTask().execute();
						}
					}
					else
					{
						cb_ll.requestFocus();
						cb_ll.startAnimation(shake);
					}*/
				}
				else
				{
					if(user_name.length() == 0 || user_pass.length() == 0 || user_rpass.length() == 0 || user_age.length() == 0 || user_sex.length() == 0  || user_city.length() == 0)
					{
						if(user_city.length() == 0)
						{
							city.requestFocus();
							city.startAnimation(shake);
							city.setError(getResources().getString(R.string.rcity));
						}
						if(user_sex.length() == 0)
						{
							sex.requestFocus();
							sex.startAnimation(shake);
							sex.setError(getResources().getString(R.string.rsex));
						}
						if(user_age.length() == 0)
						{
							age.requestFocus();
							age.startAnimation(shake);
							age.setError(getResources().getString(R.string.rage));
						}
						if(user_rpass.length() == 0)
						{
							re_pass.requestFocus();
							re_pass.startAnimation(shake);
							re_pass.setError(getResources().getString(R.string.rpass));
						}
						if(user_pass.length() == 0)
						{
							pass.requestFocus();
							pass.startAnimation(shake);
							pass.setError(getResources().getString(R.string.hard_6));
						}
						if(user_name.length() == 0)
						{
							email.requestFocus();
							email.startAnimation(shake);
							email.setError(getResources().getString(R.string.hard_5));
						}
						return;
					}
					
					if(!cms.checkEmail(user_name) || user_pass.length() < 6)
					{
						if(user_pass.length() < 6)
						{
							pass.requestFocus();
							pass.startAnimation(shake);
							pass.setError(getResources().getString(R.string.hard_8));
						}
						if(user_name.length() == 0)
						{
							email.requestFocus();
							email.startAnimation(shake);
							email.setError(getResources().getString(R.string.hard_7));
						}
						return;
					}
					
					if(user_pass.equals(user_rpass))
					{
						/*if(cb.isChecked() == true)
						{*/
							// Hit Async Task over here.. 
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new RegistrationAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
							} else {
								new RegistrationAsyncTask().execute();
							}
						/*}
						else
						{
							cb_ll.requestFocus();
							cb_ll.startAnimation(shake);
						}*/
					}
					else
					{
						re_pass.requestFocus();
						re_pass.startAnimation(shake);
						re_pass.setError(getResources().getString(R.string.hard_11));
					}
				}
			}
		});
	}

	// Date picker dialog
	@Override
	 @Deprecated
	 protected Dialog onCreateDialog(int id) {
	  return new DatePickerDialog(this, datePickerListener, year, month, day);
	 }

	 private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
	  public void onDateSet(DatePicker view, int selectedYear,
	    int selectedMonth, int selectedDay) {
	   
		  /**
		   * Perform all age validation over here .. 
		   */
		  int agey = CommonMethodStuff.calculateMyAge(selectedYear, selectedMonth + 1, selectedDay);
	       System.out.println("===================final age uis ================ 1111 " + agey);
		  if(agey >= 13)
		  {
			  System.out.println("===================final age uis ================ 22222 " + agey);
			  user_age = cms.pad(selectedYear).toString() + "-" + cms.pad((selectedMonth + 1)).toString() + "-" + cms.pad(selectedDay).toString();
			  age.setText(String.valueOf(agey));
			  age_check_flag = 0;
		  }
		  else
		  {
			  System.out.println("===================final age uis ================ 33333 " + agey);
			  if(age_check_flag == 0 )
			  {
				  System.out.println("===================final age uis ================ 44444 " + agey);
				  age_check_flag = 1;
				  user_age = "";
				  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegistrationActivity.this);
					alertDialogBuilder.setTitle("BIMP");
						alertDialogBuilder.setMessage("This app requires age greater than 12 years.")
							.setCancelable(false)
							.setPositiveButton("OK",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									age.setText("");
									age_check_flag = 0;
									dialog.cancel();
								}
							  });
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
					
				  age.setText(""); 
			  }
			  
		  }	   
	  }
	 };
	
	 // Initialise all UI variables over here .. 
	private void initui() {
		email = (EditText) findViewById(R.id.r_username);
		pass = (EditText) findViewById(R.id.r_password);
		re_pass = (EditText) findViewById(R.id.r_rpassword);
		age = (EditText) findViewById(R.id.r_age);
		sex = (EditText) findViewById(R.id.r_sex);
		city = (EditText) findViewById(R.id.r_city);
		//cb = (CheckBox) findViewById(R.id.r_cb);
		//t_c = (TextView) findViewById(R.id.r_term_condition);
		sign_up = (LinearLayout) findViewById(R.id.r_sign_up);
		cb_ll = (LinearLayout) findViewById(R.id.ll);
		reg_tv1 = (TextView) findViewById(R.id.reg_tv1);
		reg_tv2 = (TextView) findViewById(R.id.reg_tv2);
		reg_tv3 = (TextView) findViewById(R.id.reg_tv3);
		reg_tv4 = (TextView) findViewById(R.id.reg_tv4);
	}
	
	// User registration AsyncTask starts over here.. 
	private	class RegistrationAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(RegistrationActivity.this);
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
				json.put("user_email", user_name);
				json.put("user_phone", cell_number);
				json.put("user_age", user_age);
				json.put("user_sex", user_sex);
				json.put("user_city", user_city);
				json.put("push_notification_id", gcm_id);
				json.put("registration_type", registration_type);
				
				System.out.println("============ final age =============== " + user_age);
				
				// Taking reference from LoginTypeActivity class
				if(registration_type.equalsIgnoreCase("social"))
					json.put("user_password", "");
				else
					json.put("user_password", user_pass);
				
				
				jsonString = ParseJsonData.getParsedData(RegistrationActivity.this, json.toString(), StaticUrl.USER_REGISTRATION);
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
					try{
						JSONObject jObject = new JSONObject(result);
						JSONObject object = jObject.getJSONObject("UserMailRegistration_response");
						String success = object.getString("success");
						if(success.equalsIgnoreCase("true"))
						{
							
							JSONObject dataObject = jObject.getJSONObject("data");
							user_id = dataObject.getString("userId");
							
							sm.createLoginSession(user_name, user_id);
							
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
							
							Intent intent = new Intent(RegistrationActivity.this, DashBoardScreen.class);
							//intent.putExtra("navigationShow", "0");
							startActivity(intent);
							finish();
						}
						else
						{
							String message = object.getString("message");
							nad.showNativeAlertDialog(RegistrationActivity.this, getResources().getString(R.string.app_name) , message);
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
	
	// User registration AsyncTask starts over here.. 
	private	class CityAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(RegistrationActivity.this);
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
				jsonString = ParseJsonData.getParsedData(RegistrationActivity.this, json.toString(), StaticUrl.GET_CITY_LIST);
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
					try{
						JSONObject jsonObject = new JSONObject(result);
						JSONObject jObject = jsonObject.getJSONObject("GetCityList_response");
						cityArrayList = new ArrayList<CityModel>();
						String success = jObject.getString("success");
						if(success.equalsIgnoreCase("true"))
						{
							JSONArray array = jObject.getJSONArray("data");
							for(int i = 0; i < array.length(); i++)
							{
								JSONObject object = array.getJSONObject(i);
								city_id = object.getString("city_id");
								city_name = object.getString("city_name");
								
								cityModel = new CityModel();
								cityModel.city_id = city_id;
								cityModel.city_name = city_name;
								
								cityArrayList.add(cityModel);
							}
						}
						else
						{
							String message = jObject.getString("message");
							nad.showNativeAlertDialog(RegistrationActivity.this, getResources().getString(R.string.app_name), message);
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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(RegistrationActivity.this, LoginTypeActivity.class);
		startActivity(intent);
		finish();
	}


}// End of main class over here.. 

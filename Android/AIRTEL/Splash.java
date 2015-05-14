package com.kleward.asp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticCredentials;

import static utils.CommonUtilities.SENDER_ID;

/**
 * @author VIVEK
 * This class deals with splash options.
 */
public class Splash extends Activity {
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	String gcm_id = "";
	SessionManagement session;
	
	String jsonString = "";
	String user_id = "";
	String party_key = "";
	String session_token = "";
	NativeAlertDialog nad;
	
	TextView two, last;

	/** ALL GCM PUSH NOTIFICATION BASED WORK GOES OVER HERE  */
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCMDemo";

	TextView mDisplay;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	SharedPreferences prefs;
	Context context;
	String regid;

	private Tracker tracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		context = getApplicationContext();

		//===================GA=====================
		tracker = GoogleAnalyticsApp.getTracker(this);
		tracker.setScreenName("SplashScreen");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

		setContentView(R.layout.splash);
		session = new SessionManagement(Splash.this);
		
		Typeface bold = Typeface.createFromAsset( getAssets(), "circular_bold.ttf" );
		Typeface regular = Typeface.createFromAsset( getAssets(), "circular_regular.ttf" );
		
		two = (TextView) findViewById(R.id.two); two.setTypeface(bold);
		last = (TextView) findViewById(R.id.last); last.setTypeface(regular);
		
		SharedPreferences spp = getSharedPreferences("topone1", 0);
		SharedPreferences.Editor edit = spp.edit();
		edit.putString("value", "yes");
		edit.putString("value1", "yes");
		edit.commit();

        SharedPreferences s = getSharedPreferences("connect", 0);
        SharedPreferences.Editor edit1 = s.edit();
        edit1.putString("fb", "yes");
        edit1.putString("twitter", "yes");
        edit1.putString("instagram", "yes");
        edit1.putString("what", "yes");
        edit1.commit();

		// Perform all Splash stuff over here.
		startMainWork();
	}
	
	public void startMainWork()
	{

		// Check device for Play Services APK. If check succeeds, proceed with
		//  GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);


			SharedPreferences sp = getSharedPreferences("gcm", 0);
			gcm_id = sp.getString("gcm_id", "");

			System.out.println("=============FInal GCM check ===========11111 " + gcm_id);

			if (gcm_id.equalsIgnoreCase(""))
			{
				registerInBackground();
			}

		} else {
			Toast.makeText(getApplicationContext(), "You need to install a valid Google Play Services. Please install and try again.", Toast.LENGTH_SHORT).show();
			
		}



		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 3000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					}
					SharedPreferences spP = getSharedPreferences("show_tutorial", 0);
					String value = spP.getString("value", "no");
					if(value.equalsIgnoreCase("") || value.equalsIgnoreCase("no"))
					{
						Intent intent = new Intent(Splash.this, TutorialFragmentActivity.class);
						startActivity(intent);
						finish();
					}
					else
					{
						if(session.isLoggedIn())
						{
							session = new SessionManagement(Splash.this);
						    HashMap<String, String> user = session.getUserDetails();
					        user_id = user.get(session.KEY_UID); // Use this for User ID.
					        SharedPreferences spp = getSharedPreferences("party_info", 0);
					        party_key = spp.getString("party_key", "");
					        session_token = spp.getString("session_token", "");

					        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								new UserLoginAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
							} else {
								new UserLoginAsyncTask().execute();
							}
						}
						else
						{
							Thread logoTimer1 = new Thread() {
								public void run() {
									try {
										int logoTimer = 0;
										while (logoTimer < 3000) {
											sleep(100);
											logoTimer = logoTimer + 100;
										}
							Intent intent = new Intent(Splash.this, LoginTypeActivity.class);
							startActivity(intent);
							finish();
									} catch (Exception e) {
										e.printStackTrace();
									} finally {

									}
								}
							};
							logoTimer1.start();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {

				}
			}
		};
		logoTimer.start();
	}

	/**
	 * ALL GCM BASE CODE WORK GOES OVER HERE
	 */

	// You need to do the Play Services APK check here too.
	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing registration ID is not guaranteed to work with
		// the new app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences, but
		// how you store the registration ID in your app is up to you.
		return getSharedPreferences(Splash.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {

		AsyncTask<String, Void, String> asyncTask = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... strings) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = regid;

					
					// The request to your server should be authenticated if your app
					// is using accounts.
					sendRegistrationIdToBackend();

					// Persist the registration ID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				super.onPostExecute(msg);
				//Toast.makeText(getApplicationContext(), "I am here at main thread background task ... " + msg, Toast.LENGTH_SHORT).show();
				System.out.println("=============FInal GCM check ===========22222 " + msg);
				SharedPreferences sp = getSharedPreferences("gcm", 0);
				SharedPreferences.Editor edit = sp.edit();
				edit.putString("gcm_id", msg);
				edit.commit();

				//mDisplay.append(msg + "\n");
			}
		}.execute();

	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. 
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {

		System.out.println("================REG ID IS : " + regId);

		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * ALL GCM BASED CODE WORK ENDS OVER HERE
	 */


	private	class UserLoginAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			try{
				JSONObject json = new JSONObject();
				json.put("X_REST_PASSWORD", "pAirtelSocialParty");
				json.put("X_REST_USERNAME", "airtelSocialParty");
				json.put("user_id", user_id);
				json.put("party_key", party_key);
				json.put("session_token", session_token);
				
				jsonString = ParseJsonData.getParsedData(Splash.this, json.toString(), StaticCredentials.APP_GET_FIRST_PAGE_OPTIONS);
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
				startDash();
			}
			else if(result.equalsIgnoreCase("3"))
			{
				startDash();
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
							JSONObject object = jsonObject.getJSONObject("result");
							String is_dj_voted = object.getString("is_dj_voted");
							String is_venue_voted = object.getString("is_venue_voted");
							
							if(is_dj_voted.equalsIgnoreCase("yes") && is_venue_voted.equalsIgnoreCase("yes"))
								startNavigation();
							else
								startDash();
						}
						else
							startDash();
					}
					catch(Exception e)
					{
						startDash();
					}
				}
				else
				{
					startDash();
				}
				
			}
			
		}
	}
	
	private void startDash()
	{
		Thread logoTimer = new Thread() {
		public void run() {
			try {
				int logoTimer = 0;
				while (logoTimer < 3000) {
					sleep(100);
					logoTimer = logoTimer + 100;
				}
				Intent intent = new Intent(Splash.this, DashBoardScreen.class);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				finish();
			}
		}
	};
	logoTimer.start();
	}
	
	private void startNavigation()
	{
		Thread logoTimer = new Thread() {
		public void run() {
			try {
				int logoTimer = 0;
				while (logoTimer < 3000) {
					sleep(100);
					logoTimer = logoTimer + 100;
				}
				Intent intent = new Intent(Splash.this, NavigationFragementActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				finish();
			}
		}
	};
	logoTimer.start();
	}
    
}

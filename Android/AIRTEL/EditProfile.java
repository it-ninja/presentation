package com.kleward.asp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import utils.CommonMethodStuff;
import utils.ImageLoader;
import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticCredentials;

/**
 * @author vivek
 * Edit profile work goes over here
 */
public class EditProfile extends Fragment{

	/**
	 * Define all global UI and class variables over here  */

	ImageView profile_iv;
	TextView profile_one, profile_two, profile_s_up, profile_three, profile_four, profile_five, profile_update_pass,profile_s_clear, profile_update_cancel;
	EditText profile_name, profile_cell, profile_old_pass, profile_new_pass, profile_confirm_pass;
	
	View rootView;
	SessionManagement session;
	Typeface bold;
	Typeface regular;
	private ProgressDialog progressDialog;
	NativeAlertDialog nad;
	ImageLoader imageLoader;
	
	String user_id = "";
	String jsonString = "";
	
	String old_pass = "";
	String new_pass = "";
	String confirm_pass = "";
	
	String name = "";
	String FINAL_IMAGE_BASE_CODE = "";
	
	String user_email = "";
	String user_image = "";
	String user_mobile = "";
	String user_name = "";
	String image_url = "";
	
	String pick_image_name;
	String picturePath;
	static String imageEncoded;
	Bitmap bitmap;
	String file_path;
	
	String party_key = "";
	String session_token = "";
	
	CommonMethodStuff cms;

	private Tracker tracker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//===================GA Tracker work goes over here =====================
		tracker = GoogleAnalyticsApp.getTracker(getActivity());
		tracker.setScreenName("EditProfile");
		tracker.send(new HitBuilders.ScreenViewBuilder().build());

	}

	@SuppressWarnings("static-access")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    rootView = inflater.inflate(R.layout.edit_profile, container, false);
	 
	    cms = new CommonMethodStuff();
	    
	    session = new SessionManagement(getActivity());
	    HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(session.KEY_UID); // Use this for User ID.
        SharedPreferences sp = getActivity().getSharedPreferences("party_info", 0);
        party_key = sp.getString("party_key", "");
        session_token = sp.getString("session_token", "");
        
        initui();
        
        SharedPreferences spp = getActivity().getSharedPreferences("user_profile", 0);
		
		user_email = spp.getString("user_email", "");
		image_url = spp.getString("user_image", "");
		user_mobile = spp.getString("user_mobile", "");
		user_name = spp.getString("user_name", "");

        System.out.println("=================image_url================ " + image_url);

        if(image_url.contains("noImage."))
        {
            image_url = "";
        }

		// Image loading work goes over here
		imageLoader = new ImageLoader(getActivity());
		int loader = R.drawable.change_pic;
		imageLoader.DisplayImage(image_url, loader, profile_iv);
		profile_name.setText(user_name);
		profile_cell.setText(user_email);

		// Clear profle btn goes over here
		profile_s_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				 SharedPreferences spp = getActivity().getSharedPreferences("user_profile", 0);
					user_email = spp.getString("user_email", "");
					user_mobile = spp.getString("user_mobile", "");
					user_name = spp.getString("user_name", "");
					
					profile_name.setText(user_name);
					profile_cell.setText(user_email);
			}
		});

		// Update cancel btn click goes over here
		profile_update_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				profile_old_pass.setText("");
				profile_new_pass.setText("");
				profile_confirm_pass.setText("");
			}
		});

		// Get typeFace over here
	    bold = Typeface.createFromAsset(getActivity().getAssets(), "circular_bold.ttf" );
	    regular = Typeface.createFromAsset(getActivity().getAssets(), "circular_regular.ttf" );

		// Set typeface over here
		profile_one.setTypeface(regular);
		profile_two.setTypeface(regular);
		profile_s_up.setTypeface(bold);
		profile_three.setTypeface(regular);
		profile_four.setTypeface(regular);
		profile_five.setTypeface(regular);
		profile_update_pass.setTypeface(bold);
		profile_name.setTypeface(regular);
		profile_cell.setTypeface(regular);
		profile_old_pass.setTypeface(regular);
		profile_new_pass.setTypeface(regular);
		profile_confirm_pass.setTypeface(regular);
		profile_s_clear.setTypeface(bold);
		profile_update_cancel.setTypeface(bold);
		
		// Image work click listener work goes over here..
		profile_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	            startActivityForResult(i, 0); // Result can be fetched in onActivityResult();*/

					AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
					myAlertDialog.setTitle("Upload Pictures Option");
					myAlertDialog.setMessage("Upload picture via");

					myAlertDialog.setPositiveButton("Gallery",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									pickPhoto.setType("image/*");
									startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

								}
							});

					myAlertDialog.setNegativeButton("Camera",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {
									Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
									startActivityForResult(intent, 0);
								}
							});
					myAlertDialog.show();
				}

		});
	    
		// Profile update work goes over here.. 
		profile_s_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				name = profile_name.getText().toString();
				
				Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
				
				if(name.length() == 0)
				{
					profile_name.requestFocus();
					profile_name.startAnimation(shake);
					profile_name.setError(getResources().getString(R.string.v_pass_one));
					return;
				}
				else
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new UpdateProfileAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
					} else {
						new UpdateProfileAsyncTask().execute();
					}
				}
			}
		});
	    
		// Change password work goes over here ..
		profile_update_pass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				old_pass = profile_old_pass.getText().toString();
				new_pass = profile_new_pass.getText().toString();
				confirm_pass = profile_confirm_pass.getText().toString();
				
				Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
				
				if(old_pass.length() == 0 || new_pass.length() == 0 || confirm_pass.length() == 0)
				{
					if(confirm_pass.length() == 0)
					{
						profile_confirm_pass.requestFocus();
						profile_confirm_pass.startAnimation(shake);
						profile_confirm_pass.setError(getResources().getString(R.string.v_pass_one));
					}
					
					if(new_pass.length() == 0)
					{
						profile_new_pass.requestFocus();
						profile_new_pass.startAnimation(shake);
						profile_new_pass.setError(getResources().getString(R.string.v_pass_one));
					}
					
					if(old_pass.length() == 0)
					{
						profile_old_pass.requestFocus();
						profile_old_pass.startAnimation(shake);
						profile_old_pass.setError(getResources().getString(R.string.v_pass_one));
					}
					return;
				}
				
				if(new_pass.length() < 6)
				{
					profile_new_pass.requestFocus();
					profile_new_pass.startAnimation(shake);
					profile_new_pass.setError(getResources().getString(R.string.v_pass_one));
					return;
				}
				
				if(!new_pass.equals(confirm_pass))
				{
					profile_confirm_pass.requestFocus();
					profile_confirm_pass.startAnimation(shake);
					profile_confirm_pass.setError(getResources().getString(R.string.v_pass_one));
					return;
				}
				else
				{
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new ForgotPassAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
					} else {
						new ForgotPassAsyncTask().execute();
					}
				}
			}
		});
		
		return rootView;
	}

	// Initialise all UI variables over here
	private void initui() {
		profile_iv = (ImageView) rootView.findViewById(R.id.profile_iv);
		
		profile_s_clear = (TextView) rootView.findViewById(R.id.profile_s_clear);
		profile_update_cancel = (TextView) rootView.findViewById(R.id.profile_update_cancel);
		
		profile_one = (TextView) rootView.findViewById(R.id.profile_one);
		profile_two = (TextView) rootView.findViewById(R.id.profile_two);
		profile_s_up = (TextView) rootView.findViewById(R.id.profile_s_up);
		profile_three = (TextView) rootView.findViewById(R.id.profile_three);
		profile_four = (TextView) rootView.findViewById(R.id.profile_four);
		profile_five = (TextView) rootView.findViewById(R.id.profile_five);
		profile_update_pass = (TextView) rootView.findViewById(R.id.profile_update_pass);
		
		profile_name = (EditText) rootView.findViewById(R.id.profile_name);
		profile_cell = (EditText) rootView.findViewById(R.id.profile_cell);
		profile_old_pass = (EditText) rootView.findViewById(R.id.profile_old_pass);
		profile_new_pass = (EditText) rootView.findViewById(R.id.profile_new_pass);
		profile_confirm_pass = (EditText) rootView.findViewById(R.id.profile_confirm_pass);
	}
	
	// Forgot Password AsyncTask
	private	class ForgotPassAsyncTask extends AsyncTask<String, Void, String> {
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
				json.put("user_old_password", cms.encodeTextToBase64(old_pass));
				json.put("user_new_password", cms.encodeTextToBase64(new_pass));
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticCredentials.APP_USER_CHANGE_PASSWORD);
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
						String message = jsonObject.getString("message");
						if(status.equalsIgnoreCase("true"))
						{
							nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), message);
							profile_old_pass.setText("");
							profile_new_pass.setText("");
							profile_confirm_pass.setText("");
						}
						else
						{
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

	// Gets out when session expired
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
	
	// Update Profile
	private	class UpdateProfileAsyncTask extends AsyncTask<String, Void, String> {
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
				json.put("user_name", name);
				jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticCredentials.APP_USER_UPDATE_PROFILE);
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
						String message = jsonObject.getString("message");
						if(status.equalsIgnoreCase("true"))
						{
							JSONObject object = jsonObject.getJSONObject("result");
							user_email = object.getString("user_email");
							user_image = object.getString("user_image");
							user_mobile = object.getString("user_mobile");
							user_name = object.getString("user_name");
							
							SharedPreferences spp = getActivity().getSharedPreferences("user_profile", 0);
							SharedPreferences.Editor edit = spp.edit();
							edit.putString("user_email", user_email);
							edit.putString("user_image", user_image);
							edit.putString("user_mobile", user_mobile);
							edit.putString("user_name", user_name);
							edit.commit();
							
							nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), message);
						}
						else
						{
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

	// Provides Image URI ..
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}

	// Activity call back goes over here
	  @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
		  switch (requestCode) {
			  case 0:
				  if (resultCode == getActivity().RESULT_OK) {
					  Bitmap photo = (Bitmap) data.getExtras().get("data");
					  profile_iv.setImageBitmap(photo);
					  encodeTobase64(photo);
					  System.out.println("==============Encoded Image is ================ " + imageEncoded);
					  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						  new UploadImageAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
					  } else {
						  new UploadImageAsyncTask().execute();
					  }

					  //Uri selectedImage = getImageUri(getActivity(), photo);

				  }
				  break;
			  case 1:
				  if (resultCode == getActivity().RESULT_OK) {
					  Uri selectedImage = data.getData();
					  try {
						  InputStream image_stream = getActivity().getContentResolver().openInputStream(selectedImage);
						  Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
						  profile_iv.setImageBitmap(bitmap);
						  encodeTobase64(bitmap);
						  System.out.println("==============Encoded Image is ================ " + imageEncoded);
						  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							  new UploadImageAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
						  } else {
							  new UploadImageAsyncTask().execute();
						  }

					  }catch(Exception e)
					  {
						  e.printStackTrace();
					  }
				  }
				  break;
		  }
	        
	        /*if (requestCode == 0 && null != data) {
                try{
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    File f = new  File(picturePath);
                    int index = f.getName().lastIndexOf(".");
                    String str=f.getName().substring(0,index);
                    String strEx = f.getName().substring(index);
                    System.out.println("Vivek check for cpd 323" + strEx);
                    str=str.replaceAll("[\\W]", "_");
                    pick_image_name = str + strEx;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;//returning null for below statement
                    bitmap = BitmapFactory.decodeFile(picturePath.toString(), options);
                    profile_iv.setImageBitmap(bitmap);

                    System.out.println("================Image View================" + bitmap.toString());
                    cursor.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

	            try
	            {
	            	encodeTobase64(bitmap);
	            	 System.out.println("==============Encoded Image is ================ " + imageEncoded);
	            	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new UploadImageAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
					} else {
						new UploadImageAsyncTask().execute();
					}
	            }
	            catch(Exception e)
	            {
	            }
	        }*/
	    }

	// Encode data to Base64 ( Image encoding  )
	  public static String encodeTobase64(Bitmap image)
	  {
	      Bitmap immagex=image;
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	      immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
	      byte[] b = baos.toByteArray();
	      imageEncoded = Base64.encodeToString(b,0);// Base64.DEFAULT
	      return imageEncoded;
	  }
	  

	// Upload Image AsyncTask goes over here ..
		private	class UploadImageAsyncTask extends AsyncTask<String, Void, String> {
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
					json.put("profile_image_str", imageEncoded);
					jsonString = ParseJsonData.getParsedData(getActivity(), json.toString(), StaticCredentials.APP_USER_UPLOAD_PIC);
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
							String message = jsonObject.getString("message");
							if(status.equalsIgnoreCase("true"))
							{
								JSONObject object = jsonObject.getJSONObject("result");
								image_url = object.getString("image_url");
								
								SharedPreferences spp = getActivity().getSharedPreferences("user_profile", 0);
								SharedPreferences.Editor edit = spp.edit();
								edit.putString("user_image", image_url);
								edit.commit();
								
								nad.showNativeAlertDialog(getActivity(), getResources().getString(R.string.app_name), message);
							}
							else
							{
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
	
}

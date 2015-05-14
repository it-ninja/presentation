package com.kleward.salesaward;

import java.util.HashMap;

import jim.h.common.android.zxinglib.integrator.IntentIntegrator;
import jim.h.common.android.zxinglib.integrator.IntentResult;

import org.json.JSONObject;

import utils.NativeAlertDialog;
import utils.ParseJsonData;
import utils.SessionManagement;
import utils.StaticUrl;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
/**
 * @author VIVEK
 * This class deals with Bar code scanner
 */
public class BarCodeReaderActivity extends Activity{
	
	/** 
	 * Define all global variables over here 
	 * */
	private Handler  handler = new Handler();
	NativeAlertDialog nad;
	SessionManagement session;
	String jsonString;
	String user_id;
	String serial_number;
	private ProgressDialog progressDialog;
	
	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		session = new SessionManagement(BarCodeReaderActivity.this); // Initiating Session over here .. 
		HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(session.KEY_UID);
        
        // Here we are initiating Barcode scanner over here ..
		IntentIntegrator.initiateScan(BarCodeReaderActivity.this, R.layout.capture,R.id.viewfinder_view, R.id.preview_view, true);
	}
	
	// Barcode scanning result call backs over here ... 
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
      
         switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode,
                        resultCode, data);
                if (scanResult == null) {
                    return;
                }
                final String result = scanResult.getContents();
                if (result != null) {
                	serial_number = result;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                        	String message = "Your scanned code is " + result;
                        	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BarCodeReaderActivity.this);
                			alertDialogBuilder.setTitle("Confirm code");
                				alertDialogBuilder.setMessage(message)
                					.setCancelable(false)
                					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                						public void onClick(DialogInterface dialog,int id) {
                							// Based on result call back we are hitting Async Task over here ...
                							// Here we are validating return code from here .. 
                							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                								new UpdateCodeAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
                							} else {
                								new UpdateCodeAsyncTask().execute();
                							}
                							dialog.cancel();
                						}
                					  })
                					.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                						public void onClick(DialogInterface dialog,int id) {
                							dialog.cancel();
                							BarCodeReaderActivity.this.finish();
                						}
                					});
                					AlertDialog alertDialog = alertDialogBuilder.create();
                					alertDialog.show();
                        }
                    });
                }
                else
                {
                	BarCodeReaderActivity.this.finish();
                }
                break;
            default:
            	finish();
        }
    }
	
	/**
	 * Update code AsyncTask goes over here 
	 * @author VIVEK
	 * Used to update data from here ... 
	 */
	private	class UpdateCodeAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(BarCodeReaderActivity.this);
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
				json.put("serial_number", serial_number);
				
				jsonString = ParseJsonData.getParsedData(BarCodeReaderActivity.this, json.toString(), StaticUrl.CLAIM_PROCESS);
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
				nad.showNativeAlertDialog(BarCodeReaderActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_1));
			}
			else if(result.equalsIgnoreCase("3"))
			{
				nad.showNativeAlertDialog(BarCodeReaderActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_2));
			}
			else{
				if(result.length() > 0)
				{
					try
					{
						JSONObject object = new JSONObject(result);
						String success = object.getString("success");
						String message = object.getString("message");
						
						if(success.equalsIgnoreCase("true"))
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BarCodeReaderActivity.this);
                			alertDialogBuilder.setTitle("Success");
                				alertDialogBuilder.setMessage(message)
                					.setCancelable(false)
                					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                						public void onClick(DialogInterface dialog,int id) {
                							dialog.cancel();
                							BarCodeReaderActivity.this.finish();
                							
                						}
                					  });
                					
                					AlertDialog alertDialog = alertDialogBuilder.create();
                					alertDialog.show();
							
						}
						else
						{
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BarCodeReaderActivity.this);
                			alertDialogBuilder.setTitle("Failure");
                				alertDialogBuilder.setMessage(message)
                					.setCancelable(false)
                					.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                						public void onClick(DialogInterface dialog,int id) {
                							dialog.cancel();
                							BarCodeReaderActivity.this.finish();
                						}
                					  });
                					
                					AlertDialog alertDialog = alertDialogBuilder.create();
                					alertDialog.show();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					nad.showNativeAlertDialog(BarCodeReaderActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.server_3));
				}
				
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	

}

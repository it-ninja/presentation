package com.training.aamc;

import helper.JasonToArrayList;
import helper.MyVars;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import model.FileType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.training.aamc.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import database.DataHelper;

/**
 * THis class deals with login type  activity 
 * @author vivek
 *
 */
public class MainActivity extends CommonFunctionalityActivity {
	/**
	 * Define all Global variables over here .. 
	 */
	String isLoggedIn = "False";
	String TAG = "AAMC";
	EditText un;
	EditText pw;
	String key = "9907232651klewar",sUname,sPassword;
	InputStream is = null;
	JSONObject jObj = null;
	String jsonString = "";
	DataHelper dataHelper;
	Context context;
	String uName = "";
	String uPass = "";
	public List<List<List<List<List<FileType>>>>> parentList1;
	
	TextView et,howToUseTv;
	String loginType = "login";
	  public void onCreate(Bundle icicle) {
		    super.onCreate(icicle);
		    setContentView(R.layout.loginpage);
		    //getLastNonConfigurationInstance();
		   context = this.getApplicationContext();
		   howToUseTv = (TextView)findViewById(R.id.howToUse);
		   et = (TextView) findViewById(R.id.tv_error);
		   howToUseTv.setPaintFlags(howToUseTv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // SET TEXT FLAG UNDERLINE
		   un = (EditText) findViewById(R.id.et_un);
		   pw = (EditText) findViewById(R.id.et_pw);
		   howToUseTv.setText("How to use");
		   howToUseTv.setTextColor(Color.BLACK);
		   dataHelper = new DataHelper(getApplicationContext()); // DB HELPER INIT
		   loginType = getIntent().getStringExtra("loginType");
		   //to be deleted
		  
		   if(loginType.equalsIgnoreCase("login"))
		   {
			    dataHelper.open();
			    if(DataHelper.getCourseList().getCount()>0)
			    {
			    	finish();
			    	Intent tabIntent = new Intent(this,GridHome.class);
			    	tabIntent.putExtra("positionTab", 2);
				    startActivity(tabIntent);
			    }
			    dataHelper.close();
		   }
		   else
		   {
			   SharedPreferences myPrefs = context.getSharedPreferences("isLoggedIn", 0);
			   uName = myPrefs.getString("jUname", "null");
			   un.setText(uName);
			   un.setEnabled(false);
			   un.setClickable(false);
			   un.setKeyListener(null);
		   }
		  et.setVisibility(View.GONE);
	  }

	  // To be used for saving instance for UI orientation change ... 
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        boolean isVisible = false;
        if(et.getVisibility() == 0)
        {
            isVisible = true;
        }
        Log.d("Sagar","SaveInstance"+et.getVisibility());
        outState.putBoolean("etvisibility",isVisible);
        outState.putString("etvalue",et.getText().toString());
        outState.putBoolean("isProgressShowing",isProgressShowing);
        if(isProgressShowing)
        {
            pDialoge.dismiss();
            isProgressShowing = true;
        }
    }

    // Restore UI orientation 
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean isVisible = savedInstanceState.getBoolean("etvisibility");
        Log.d("Sagar","restore"+isVisible);
        if(isVisible)
        {
            et.setVisibility(View.VISIBLE);
            et.setText(savedInstanceState.getString("etvalue"));
        }
        if(savedInstanceState.getBoolean("isProgressShowing"))
        {
            showDialouge("Please wait...");
        }
    }

    
    protected void writeSharedPrefrence(String isLogedInS,String sUserName,String sPWord,String jUsername)
	  {
		  Log.d(TAG, "Writing Shared Prefrence"+sUserName+" - "+sPWord);
		  SharedPreferences myPrefs = getSharedPreferences("isLoggedIn", 0);
	      SharedPreferences.Editor prefsEditor = myPrefs.edit();
	      prefsEditor.putString("isLoggedIn", isLogedInS);
	      prefsEditor.putString("sUname", sUserName);
	      prefsEditor.putString("sPassword", sPWord);
	      prefsEditor.putString("jUname", jUsername);
	      prefsEditor.commit();
	  }
    // How to use call goes over here .. 
	  public void callHowToUse(View v)
	  {
		  Intent tabIntent = new Intent(this,HowToUse.class);
		  tabIntent.putExtra("calledFromLogin", "true");
	   	 	startActivity(tabIntent);  
	  }
	  
	  protected String readSharedPrefrenceLogedIn()
	  {
		  SharedPreferences myPrefs = this.getSharedPreferences("isLoggedIn", 0);
	      String prefName = myPrefs.getString(isLoggedIn, "False");
	      return prefName;
	  }
	  
	  // Check for DB FILES
	  protected void isModuleFileAvialable(boolean avialable)
	  {
		  Toast.makeText(getApplicationContext(), "Module Avialable "+String.valueOf(avialable), Toast.LENGTH_LONG).show();
	  }
	  protected void isModuleXMLCorrupted()
	  {
		  
	  }
	  
	  // GET FROM LOCAL DB 
	  protected void readModuleXML()
	  {
		  Log.i(TAG, "Reading Module");
		File AAMCDirectory = new File("/"+Environment.getExternalStorageDirectory().getPath()+"/Download/AAMC");
		if(!AAMCDirectory.exists())
		{
			Log.i(TAG, "AAMCDirectory Does not exist");
			AAMCDirectory.mkdir();
			Log.i(TAG, "AAMCDirectory Creating");
		}
		else
		{
			Log.i(TAG, "AAMCDirectory Exist");
		}
		  
		File file = new File("/"+Environment.getExternalStorageDirectory().getPath()+"/Download/AAMC/Module.json");
   	 	if (file.exists()) 
   	 	{
   	 	isModuleFileAvialable(true);
	   	 	File sdcard = Environment.getExternalStorageDirectory();
	
	   	 	StringBuilder text = new StringBuilder();
		
		   	try {
		   	    BufferedReader br = new BufferedReader(new FileReader(file));
		   	    String line;
		
		   	    while ((line = br.readLine()) != null) {
		   	        text.append(line);
		   	        //text.append('\n');
		   	    }
		   	   
		   	}
		   	catch (IOException e) {
		   	    //You'll need to add proper error handling here
		   	}
		    MyVars.myJsonString = text.toString();
	   	//Find the view by its id
	   	
	    //Toast.makeText(getApplicationContext(), "Module text \n "+text.toString(), Toast.LENGTH_LONG).show();
		Log.i("Read Data", MyVars.myJsonString);  
		try {
			jObj = new JSONObject(MyVars.myJsonString);
			Log.e("JSON Parser", "Working Fine");
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
	//	JasonToArrayList jal = new JasonToArrayList();
	//	jal.jasonToArrayList(jObj);
	   	//Set the text
	   	
   	 	}
	   	else
   	    {
   	    	//isModuleXMLAvialable(false);
   	    	//writeModuleXMLOnSDCard();
   	    }
	  }
	  protected void writeModuleXMLOnSDCard()
	  {
		  try {
				File myFile = new File("/"+Environment.getExternalStorageDirectory().getPath()+"/Download/AAMC/Module.json");
				myFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(myFile);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append(jsonString);
				myOutWriter.close();
				fOut.close();
				Toast.makeText(getBaseContext(),"Done writing SD 'mysdfile.txt'",Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
			}
	  }
	  
	  // LOGIN PROCESS TASK GOES OVER HERE 
	  public void loginProcess(View v)
	  {
		  uName = un.getText().toString().trim();
		  uPass = pw.getText().toString().trim();
	     if(!uName.equalsIgnoreCase("") && !uPass.equalsIgnoreCase(""))
	     {
             if(isNetworkAvailable())
             {
                 AsyncTask task = new LoginTask().execute();
             }

	     }
	     else
	     {
	    	 showMsg("Enter valid username and password");
	     }
	      et.setVisibility(View.GONE);
	  }

	  // GET SERVER DATA
	  protected String getStringFromUrl(String url) {

			// Making HTTP request
			
		  try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpPost = new HttpGet(url.trim());
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();			
				Log.i("is",is.toString());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				//Log.e("GOT From Web", sb.toString());
				jsonString = sb.toString();
				return jsonString;
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}
			return null;
			// try parse the string to a JSON object
	  }
	  
	  // VALIDATE LOGIN STATUS
	  protected void checkLoginStatus()
	  {
		  JSONObject jObj = null;
		  JSONObject rootObject = null;
		  try {
				jObj = new JSONObject(jsonString);
			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}
		  if(jObj != null)
		  {
			  try {
					rootObject = jObj.getJSONObject("Root");
					Log.e("Root length", String.valueOf(rootObject.length()));
				
				} catch (JSONException e) {
					e.printStackTrace();
				}
			  if(rootObject != null)
			  {
				  try
				  {
					  isLoggedIn = rootObject.getString("Value");
				  }
				  catch (JSONException e) {
						e.printStackTrace();
					}
			  }
			  Log.e("Loggedin", isLoggedIn);
			  Log.i("Writ","Writ data on sd card");
			  writeModuleXMLOnSDCard();
			  
		  }
		  else
		  {
			  //Load json Agaiin
		  }

	  }
	  
	  // ENCRYPT DATA 
	  private String Encrypt(String text, String key)
   	       throws Exception {
   	       Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
   	       byte[] keyBytes= new byte[16];
   	       byte[] b= key.getBytes("UTF-8");
   	       int len= b.length;
   	       if (len > keyBytes.length) len = keyBytes.length;
   	       System.arraycopy(b, 0, keyBytes, 0, len);
   	       SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
   	       IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
   	       cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);

   	       byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
   	     //  String result = Base64.encodeBytes(results);
   	       String result = Base64.encodeToString(results, 0);
   	       return result;
   	   }
	  
	  // LOGIN ASYNC TASK 
	  public class LoginTask extends AsyncTask<String, Void, Boolean> {

		  String status = "Invalid";
		    public LoginTask() {
		    }
		    protected void onPreExecute() {
		    //    this.dialog.setMessage("Progress start");
		    //    this.dialog.show();
		    	showDialouge("Please wait...");
		    }

		        @Override
		    protected void onPostExecute(final Boolean success) {
		      /*  if (dialog.isShowing()) {
		            dialog.dismiss();
		        }
*/

		        //MessageListAdapter adapter = new MessageListAdapter(activity, titles);
		       // setListAdapter(adapter);
		      //  adapter.notifyDataSetChanged();
		        hideDialouge();
		        Log.d("Status",status);
		        if (success) {
		        
		        } else {
		        	if(status.equalsIgnoreCase("Invalid"))
		        	 {
		        		showMsg("Enter valid username and password");
		        		et.setText("Enter valid username and password");
		        	 }
		        	else if(status.equalsIgnoreCase("blocked"))
		        	{
		        		et.setText("You have been blocked.");
		        		showMsg("Account deactivated!\nTo re-activate your account, please" +
		        				" check your registered email account for a mail from AAMC or contact AAMC Training.");
		        	}
		        	else
		        	{
		        		showMsg("Unauthorised user.");
		        		et.setText("Unauthorised user.");
		        	}
		        	et.setVisibility(View.VISIBLE);
		        }
		    }

		    protected Boolean doInBackground(final String... args) {
		    	
				
				if(uName.length() > 0 && uPass.length()>0)
				{
					
					// Storing data of username for user login purpose ..
					SharedPreferences p = getSharedPreferences("cpd_username", 0);
					SharedPreferences.Editor e = p.edit();
					e.putString("username", un.getText().toString());
					e.commit();
					
					String eUname = "";
					String eUpass = "";
					
					try {
						eUname = Encrypt(uName, key);
						Log.e("EncryptedUname", eUname);
						eUpass = Encrypt(uPass, key);
						Log.e("EncryptedPassword", eUpass);
						String myUrl = MyVars.loginUrl+eUname+"~"+eUpass+"~9907232651klewar~Android~true";
						myUrl = myUrl.replaceAll("\\n","");
						Log.i("myUrl",myUrl);
						jsonString = getStringFromUrl(myUrl);
						Log.i("json", jsonString);
						JSONObject myJobj = null;
						try {
							myJobj = new JSONObject(jsonString);
							if(myJobj != null)
							Log.e("JSON Parser", "Working Fine");
						} catch (JSONException ee) {
							Log.e("JSON Parser", "Error parsing data " + ee.toString());
						}
						JasonToArrayList jal = new JasonToArrayList();
						dataHelper.open();
						status = jal.jasonToArrayList(myJobj, dataHelper,false);
						if(status.equalsIgnoreCase("valid"))
						{
							writeSharedPrefrence("True", eUname, eUpass,uName);
							
							SharedPreferences spppp = getSharedPreferences("username", 0);
							SharedPreferences.Editor editors = spppp.edit();
							editors.putString("username", eUname);
							editors.commit();
							
							dataHelper.close();
							loadCovers();
							startCourseActivity();
							if(loginType.equalsIgnoreCase("login"))
							{
								finish();
							}
							startCourseActivity();
						    return true;
							
						}
						else
						{
							dataHelper.close();
							return false;
						}
						//jal.jasonToArrayList(myJobj);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				else
				{
					
				}
		    	return false;
		}
	  }
	  // INITIATES COURSE ACTIVITY 
	  protected void startCourseActivity()
	  {
		  homeCalled(null);
		  /*
		  Intent tabIntent = new Intent(this,GridHome.class);
		    startActivity(tabIntent);*/
	  }
	  
	  // DIALOG WORK GOES OVER HERE 
	  public void showMsg(String str)
	  {
		  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MainActivity.this);

			// set title
		//	alertDialogBuilder.setTitle("Sorry...");

			// set dialog message
			alertDialogBuilder
					.setMessage(str)
					.setCancelable(false)
					.setNegativeButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int id) {
									// if this button is clicked,
									// just close
									// the dialog box and do nothing
									dialog.cancel();
								}
							});

			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
      	
	  }
} 
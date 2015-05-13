package com.training.aamc;

import helper.InteractiveArrayAdapter;
import helper.JasonToArrayList;
import helper.MyVars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.training.aamc.R;
import com.training.aamc.GridHome.LoginTask;

import model.FileType;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import database.DataHelper;

public class CourseActivity extends CommonFunctionalityActivity {

  
/** Called when the activity is first created. */
	DataHelper dh;
	String position;
	List<FileType> downloadAllList;
	List<FileType> list = new ArrayList<FileType>();
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    // Create an array of Strings, that will be put to our ListActivity
    ArrayAdapter<FileType> adapter = new InteractiveArrayAdapter(this,readCursor());
    setContentView(R.layout.name_list_layout);
    
    ListView lw = (ListView)findViewById(R.id.listView1);
    lw.setAdapter(adapter);
    registerForContextMenu(lw);
    lw.setOnItemClickListener(new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
        	//Toast.makeText(getApplicationContext(), String.valueOf(position),Toast.LENGTH_LONG).show();
        	startModule(position);
      }
    });
    final ImageButton ib = (ImageButton) findViewById(R.id.buttonDownLoadAll);
    ib.setImageDrawable(getResources().getDrawable(R.drawable.update_blue));
    ib.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			ib.setImageDrawable(getResources().getDrawable(R.drawable.update_grey));
			updateCourse();
		}
	});
    //hideDownloadAllButton();
    setHeader("My Courses");
  }
  
  /**
   * Work by vivek
   */
  protected void updateCourse()
	{
      if(isNetworkAvailable())
		new LoginTask().execute();
		
	}
  
  public class LoginTask extends AsyncTask<String, Void, Boolean> {

		 String uName;
		 String uPass;
		 String jsonString;
		 DataHelper dataHelper;
		 public LoginTask() {
		        dialog = new ProgressDialog(getApplicationContext());
		    }

		    private ProgressDialog dialog;

		    protected void onPreExecute() {
		    	
		    	SharedPreferences myPrefs = getApplicationContext().getSharedPreferences("isLoggedIn", 0);
		    	uName = myPrefs.getString("sUname", "null");
		    	uPass = myPrefs.getString("sPassword", "null");
				showDialouge("Updating course...");
		    }

		        @Override
		    protected void onPostExecute(final Boolean success) {
		        	hideDialouge();
		    }

		    protected Boolean doInBackground(final String... args) {
		    	
				if(!uName.equalsIgnoreCase("null") && !uPass.equalsIgnoreCase("null"))
				{
					String eUname = uName;
					String eUpass = uPass;
					
					try {
						Log.e("EncryptedUname", eUname);
						Log.e("EncryptedPassword", eUpass);
						String myUrl = MyVars.loginUrl+eUname+"~"+eUpass+"~~Android~true";
						myUrl = myUrl.replaceAll("\\n","");
						Log.i("myUrl",myUrl);
						Log.i("myUrl",String.valueOf(myUrl.charAt(118)));
						jsonString = getStringFromUrl(myUrl);
						Log.i("json", jsonString);
						JSONObject myJobj = null;
						try {
							myJobj = new JSONObject(jsonString);
							if(myJobj != null)
							Log.e("JSON Parser", "Working Fine");
						} catch (JSONException e) {
							Log.e("JSON Parser", "Error parsing data " + e.toString());
						}
						JasonToArrayList jal = new JasonToArrayList();
						dataHelper = new DataHelper(getApplicationContext());
						dataHelper.open();
						if(jal.jasonToArrayList(myJobj, dataHelper,true).equalsIgnoreCase("valid"))
						{
							dataHelper.close();
							Intent intent = new Intent(CourseActivity.this, CourseActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(intent);
							finish();
						    return true;
						}
						else
						{
							Intent i = new Intent(CourseActivity.this,MainActivity.class);
							i.putExtra("loginType", "update");
							startActivity(i);
							dataHelper.close();
						//	finish();
							return false;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				else
				{
					
				}
		    	return false;
		}
	  }
  
  protected String getStringFromUrl(String url) {

		// Making HTTP request
		
	  InputStream is = null;
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
			String jsonString = sb.toString();
			return jsonString;
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		return null;
		// try parse the string to a JSON object
}
  
  protected void startModule(int i)
  {
	  Intent intent = new Intent(this,ModuleActivity.class);
	  
  	  intent.putExtra("position", list.get(i).barId);
  	  startActivity(intent);
  }
  protected List<FileType> readCursor()
  {
  	dh = new DataHelper(this);
  	dh.open();
  	list = new ArrayList<FileType>();
  	Cursor cursor = DataHelper.getCourseList();
  	Log.i("Cursor","Cursor");
  	if(cursor != null)
  	Log.i("Cursor",String.valueOf(cursor.getCount()));
  	//cursor=DataHelper.getMonthlyDetailData(1);
  	for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
  	{
		
  		FileType ft = new FileType();
  		ft.barName = cursor.getString(3);
  		ft.barId = cursor.getString(1);
  		if(cursor.getString(6).equalsIgnoreCase("1"))
  		{
  			list.add(ft);
  		}
  		
  		//position = cursor
  	}
  	cursor.close();
  	dh.close();
  	return list;
  }
  public void backButton(View v)
  {
	  Log.i("common", "Back Button");
  }
  public void downloadAll(View v)
  {
	  Log.i("common", "download All Button");
	  dh = new DataHelper(this);
	  dh.open();
	  downloadAllList = new ArrayList<FileType>();
	  Cursor cursorCourse = DataHelper.getCourseList();
	  	if(cursorCourse != null)
	  	for(cursorCourse.moveToFirst();!cursorCourse.isAfterLast();cursorCourse.moveToNext())
	  	{
			Cursor cursorModule = DataHelper.getModuleList(cursorCourse.getString(1));
	  	  	if(cursorModule != null)
	  	  	for(cursorModule.moveToFirst();!cursorModule.isAfterLast();cursorModule.moveToNext())
	  	  	{
	  	  		Cursor cursorTopicL = DataHelper.getTopicList(cursorModule.getString(1));
	  	  		if(cursorTopicL != null)
	  	  		for(cursorTopicL.moveToFirst();!cursorTopicL.isAfterLast();cursorTopicL.moveToNext())
	  	  		{
	  	  			Cursor cursorTopic = DataHelper.getTopicDetail(cursorTopicL.getString(1));
	  	  			if(cursorTopic.getCount() == 1)
	  	  			{
	  	  				if(cursorTopic.moveToFirst())
	  	  				{
		  	    	        FileType ft = new FileType();
		  	    	  		ft.barName = cursorTopic.getString(5);
		  	    	  		if(ft.barName != "null")
		  	    	  		{
		  	    	  			Log.i("cursor","pdf not null");
		  	    	  			ft.path = cursorTopic.getString(7);
		  	    	  			ft.barId = MyVars.PDF;
		  	    	  			downloadAllList.add(ft);
		  	    	  		}
		  	    	  		//Adding medias
		  	    	  		ft = new FileType();
		  	    	  		ft.barName = cursorTopic.getString(6);
		  	    	  		if(!cursorTopic.getString(6).equalsIgnoreCase("null"))
		  	    	  		{
		  	    	  			Log.i("cursorMy",ft.barName);
		  	    	  			ft.path = cursorTopic.getString(8);
		  	    	  			ft.barId = MyVars.Media;
		  	    	  			downloadAllList.add(ft);
		  	    	  		}
		  	    		}
	  	  			}
		  	    	Log.i("cursorTopic", String.valueOf(cursorTopic.getCount()));
		  	    	cursorTopic.close();
		  	    	///////
		  	    	cursorTopic = DataHelper.getSubTopicList(cursorTopicL.getString(1));
		  	    	Log.i("cursorTopic","cursorTopic");
		  	    	if(cursorTopic != null)
		  	    	Log.i("cursorTopic",String.valueOf(cursorTopic.getCount()));
		  	    	for(cursorTopic.moveToFirst();!cursorTopic.isAfterLast();cursorTopic.moveToNext())
		  	    	{
		  	          // addinf pdf
		  	          FileType ft = new FileType();
		  	          ft.barName = cursorTopic.getString(5);
		  	          if(ft.barName != "null")
		  	          {
		  	    			
	  	    			Log.i("cursor","pdf not null");
	  	    			ft.path = cursorTopic.getString(7);
	  	    			ft.barId = MyVars.PDF;
	  	    			downloadAllList.add(ft);
		  	          }
	  	    		//Adding medias
		  	          ft = new FileType();
		  	          ft.barName = cursorTopic.getString(6);
		  	          if(!cursorTopic.getString(6).equalsIgnoreCase("null"))
		  	          {
	  	    			ft.path = cursorTopic.getString(8);
	  	    			ft.barId = MyVars.Media;
	  	    			downloadAllList.add(ft);
		  	          }
		  	    	}
		  	    	cursorTopic.close();
	  	  		}
	  	  		cursorTopicL.close();
	  	  	}
	  	  	cursorModule.close();
	  	}
	  	cursorCourse.close();
	  	dh.close();
	  	Log.i("commonListLenght", String.valueOf(downloadAllList.size()));
	  	Log.i("commonList", downloadAllList.toString());
  }
  public void clickedMyView(View v)
  {
	  Log.i("hihihih", String.valueOf(v.getContentDescription()));
	  
  }
}
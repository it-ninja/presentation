package com.kleward.cis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**************************************
 * @author VIVEK
 * This class shows details of incident
 **************************************/
public class IncidentDetails extends ActionBarActivity{
	private GoogleMap map;
	LatLng HAMBURG; 
	
	String incident_city = "";
	String incident_source = "";
	String incident_rate = "";
	String incident_latitude = "";
	String incident_longitude = "";
	String incident_description = "";
	String incident_type = "";
	String date = "";
	
	ProgressBar progressBar;
	TextView source_tv, city_tv, inci_descrip,actionBar_title,action_back, show_date;
	LinearLayout action_ll;
	ImageView d_all_brand_iv;
	
	private ActionBar actionBar;
	Typeface fonticon;
	Typeface fonttext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incident_details);
		fonticon = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		fonttext = Typeface.createFromAsset( getAssets(), "login.ttf" );
		initui();
		
		Bundle extras = getIntent().getExtras();
		incident_city = extras.getString("location");
		incident_source = extras.getString("source");
		incident_rate = extras.getString("rate");
		incident_latitude = extras.getString("lati");
		incident_longitude = extras.getString("longi");
		incident_description = extras.getString("incident_description"); // Need to use this in UI
		incident_type = extras.getString("type");
		date = extras.getString("date");
		
		System.out.println("===============incident_type============ " + incident_type + " _____________________ " + incident_description);
		//d_all_brand_iv.setBackgroundResource(R.drawable.humancrisis);
		try
		{
			if(incident_type.equalsIgnoreCase("Human & Society Crisis"))
				d_all_brand_iv.setBackgroundResource(R.drawable.humancrisis);
			else if(incident_type.equalsIgnoreCase("Security & Defence"))
				d_all_brand_iv.setBackgroundResource(R.drawable.security_defence);
			else if(incident_type.equalsIgnoreCase("Terrorism"))
				d_all_brand_iv.setBackgroundResource(R.drawable.teroor);
			else if(incident_type.equalsIgnoreCase("Economics & Business Crisis"))
				d_all_brand_iv.setBackgroundResource(R.drawable.ecobusicrisis);
			else if(incident_type.equalsIgnoreCase("Criminality"))
				d_all_brand_iv.setBackgroundResource(R.drawable.crime);
			else if(incident_type.equalsIgnoreCase("International politics"))
				d_all_brand_iv.setBackgroundResource(R.drawable.inter_politics);
			else if(incident_type.equalsIgnoreCase("Domestic & Regional Politics"))
				d_all_brand_iv.setBackgroundResource(R.drawable.domes_politics);
			else 
				d_all_brand_iv.setBackgroundResource(R.drawable.image_na);
		}
		catch(Exception e){e.printStackTrace();}
		
		
		// Action bar work goes over here.
		actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.comman_action_bar);
		
		actionBar_title = (TextView) actionBar.getCustomView().findViewById(R.id.action_title);
		actionBar_title.setText("Incident Detail");
		
		action_back = (TextView) actionBar.getCustomView().findViewById(R.id.action_back);
		action_back.setTypeface(fonticon);
		
		action_ll = (LinearLayout) actionBar.getCustomView().findViewById(R.id.action_ll);
		action_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.app_header));
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		
		show_date.setTypeface(fonttext);
		source_tv.setTypeface(fonttext);
		city_tv.setTypeface(fonttext);
		inci_descrip.setTypeface(fonttext);
		
		System.out.println("============================TESTETE ============ " + incident_latitude + " ___________ " + incident_longitude);
		
		try{
			HAMBURG = new LatLng(Double.parseDouble(incident_latitude), Double.parseDouble(incident_longitude));
			
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.details_map)).getMap();
	        map.getUiSettings().setScrollGesturesEnabled(false); 
	        map.getUiSettings().setZoomControlsEnabled(false);
	        map.getUiSettings().setZoomGesturesEnabled(false);
	        Marker kiel = map.addMarker(new MarkerOptions()
			.position(HAMBURG)
			
			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(HAMBURG, 15));
		}
		catch(Exception e)
		{
			map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.details_map)).getMap();
	        map.getUiSettings().setScrollGesturesEnabled(false); 
	        map.getUiSettings().setZoomControlsEnabled(false);
	        map.getUiSettings().setZoomGesturesEnabled(false);
		}
		
	
		
		int rate = Math.round((Float.parseFloat(incident_rate) *20));
		
		
		// parse the String "29/02/2015" to a java.util.Date object
		Date date1 = null;
		try {
			date1 = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// format the java.util.Date object to the desired format
		String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(date1);

	
		
		show_date.setText("Posted on, " + formattedDate.toString());
		source_tv.setText(incident_source);
		city_tv.setText(incident_city);
		inci_descrip.setText(incident_description);
		progressBar.setProgress(rate);
	}

	private void initui() {
		show_date = (TextView) findViewById(R.id.show_date);
		source_tv = (TextView) findViewById(R.id.d_incident_description);
		city_tv = (TextView) findViewById(R.id.d_incident_city);
		inci_descrip = (TextView) findViewById(R.id.inci_descrip);
		progressBar = (ProgressBar) findViewById(R.id.d_progressBar2);
		d_all_brand_iv = (ImageView) findViewById(R.id.d_all_brand_iv);
	}
	
}

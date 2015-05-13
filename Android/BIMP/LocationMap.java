package com.kleward.bimp;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
/**
 * @author VIVEK
 * This class shows user location over map
 */
public class LocationMap extends  ActionBarActivity{
	GoogleMap myMap;
	Polyline line;
	LatLng startLatLng;
	LatLng endLatLng;
	private ActionBar actionBar;
	TextView actionBarTitle, back;
	LinearLayout actionBarBack;
	String start_lat = ""; String start_long = ""; String end_lat = ""; String end_long = ""; String store_name = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_map);
		
		try{
			Bundle extra = getIntent().getExtras();
				if (extra == null) {
					start_lat = "";
					start_long = "";
					end_lat = "";
					end_long = "";
					store_name = "";
				}else {
					start_lat = extra.getString("start_lat");
					start_long = extra.getString("start_long");
					end_lat = extra.getString("end_lat");
					end_long = extra.getString("end_long");
					store_name = extra.getString("store_name");
				}
		}
		catch(Exception e){e.printStackTrace();}
		
		Typeface font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
		Typeface fontText1 = Typeface.createFromAsset( getAssets(), "Roboto-Medium.ttf" );
		
		actionBar = getSupportActionBar();
		actionBar.setCustomView(R.layout.common_action_bar_view);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.blue));
		
		actionBarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.title_tv);
		actionBarBack = (LinearLayout) actionBar.getCustomView().findViewById(R.id.back_ll);
		back = (TextView) actionBar.getCustomView().findViewById(R.id.back_tv);
		
		back.setTypeface(font);
		actionBarTitle.setText(store_name);
		actionBarTitle.setTypeface(fontText1);
		actionBarBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		myMap.getUiSettings().setZoomControlsEnabled(true);
		myMap.getUiSettings().setMyLocationButtonEnabled(true);
		myMap.setMyLocationEnabled(true);
		myMap.setIndoorEnabled(true);
		//myMap.getUiSettings().setIndoorLevelPickerEnabled(true);
		
		myMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng point) {
				//Toast.makeText(getApplicationContext(), "" + point, Toast.LENGTH_SHORT).show();
			}
		});
		
		myMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			
			@Override
			public void onInfoWindowClick(Marker marker) {
				//Toast.makeText(getApplicationContext(), "Test 2222", Toast.LENGTH_SHORT).show();
			}
		});
		
		myMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				//Toast.makeText(getApplicationContext(), "Test 1", Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		
		/*myMap.setOnIndoorStateChangeListener(new OnIndoorStateChangeListener() {
			
			@Override
			public void onIndoorLevelActivated(IndoorBuilding building) {
				
				//Toast.makeText(getApplicationContext(), building.getDefaultLevelIndex() + " ... " + building.getActiveLevelIndex(), Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onIndoorBuildingFocused() {
				
			}
		});*/
		myMap.setBuildingsEnabled(true);
		
		if(start_lat.equalsIgnoreCase("") || start_long.equalsIgnoreCase(""))
		{
			endLatLng = new LatLng(Double.parseDouble(end_lat), Double.parseDouble(end_long));
			myMap.moveCamera(CameraUpdateFactory.newLatLng(endLatLng));
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(endLatLng, myMap.getMaxZoomLevel() - 10);
			myMap.animateCamera(cameraUpdate);
			myMap.addMarker(new MarkerOptions().title(store_name).position(endLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		}
		else
		{
			startLatLng = new LatLng(Double.parseDouble(start_lat), Double.parseDouble(start_long));
			myMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(startLatLng, myMap.getMaxZoomLevel() - 10);
			myMap.animateCamera(cameraUpdate);
			Bitmap Icon = BitmapFactory.decodeResource(getResources(),R.drawable.flag);
			myMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(start_lat), Double.parseDouble(start_long))).icon(BitmapDescriptorFactory.fromBitmap(Icon)));
			endLatLng = new LatLng(Double.parseDouble(end_lat), Double.parseDouble(end_long));
			myMap.addMarker(new MarkerOptions().title(store_name).position(endLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			String urlTopass = makeURL(startLatLng.latitude,startLatLng.longitude, endLatLng.latitude,endLatLng.longitude);
	        new connectPathAsyncTask(urlTopass).execute();
		}
	}
	
	
	
	// CONNECT PATH ASYNC TASK CLASS.. 
	private class connectPathAsyncTask extends AsyncTask<Void, Void, String> {
        String url;

        connectPathAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                drawPath(result);
            }
        }
    }

public class JSONParser {

    InputStream is = null;
    JSONObject jObj = null;
    String json = "";

    // constructor
    public JSONParser() {
    }

    public String getJSONFromUrl(String url) {

        // Making HTTP request
        try {
        	System.out.println("Anand URL is : " + url);
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            json = sb.toString();
            is.close();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;

    }
}


	public void drawPath(String result) {
	    /*if (line != null) {
	        myMap.clear();
	    }*/
	    myMap.addMarker(new MarkerOptions().position(endLatLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
	    myMap.addMarker(new MarkerOptions().position(startLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag)));
	    try {
	        // Transform the string into a JSON object
	        final JSONObject json = new JSONObject(result);
	        JSONArray routeArray = json.getJSONArray("routes");
	        JSONObject routes = routeArray.getJSONObject(0);
	        JSONObject overviewPolylines = routes
	                .getJSONObject("overview_polyline");
	        String encodedString = overviewPolylines.getString("points");
	        List<LatLng> list = decodePoly(encodedString);
	
	        PolylineOptions options = new PolylineOptions().width(5).color(getResources().getColor(R.color.blue)).geodesic(true);
	        for (int z = 0; z < list.size(); z++) {
	            LatLng point = list.get(z);
	            options.add(point);
	        }
	        line = myMap.addPolyline(options);
	
	        for (int z = 0; z < list.size() - 1; z++) {
	            LatLng src = list.get(z);
	            LatLng dest = list.get(z + 1);
	            line = myMap.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude),new LatLng(dest.latitude, dest.longitude)).width(5).color(getResources().getColor(R.color.blue)).geodesic(true));
	        }
	
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	/**
	 * @author VIVEK 
	 * @param encoded
	 * @return
	 */
	private List<LatLng> decodePoly(String encoded) {
	
	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;
	
	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;
	
	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;
	
	        LatLng p = new LatLng((((double) lat / 1E5)),
	                (((double) lng / 1E5)));
	        poly.add(p);
	    }
	
	    return poly;
	}
	
	public String makeURL(double sourcelat, double sourcelog, double destlat,
	        double destlog) {
	    StringBuilder urlString = new StringBuilder();
	    urlString.append("http://maps.googleapis.com/maps/api/directions/json");
	    urlString.append("?origin=");// from
	    urlString.append(Double.toString(sourcelat));
	    urlString.append(",");
	    urlString.append(Double.toString(sourcelog));
	    urlString.append("&destination=");// to
	    urlString.append(Double.toString(destlat));
	    urlString.append(",");
	    urlString.append(Double.toString(destlog));
	    urlString.append("&sensor=false&mode=driving&alternatives=true");
	    return urlString.toString();
	}

} // End of main class over here..

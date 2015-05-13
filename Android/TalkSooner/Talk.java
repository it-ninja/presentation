package app.drugs.talksooner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import app.drugs.talksooner.R;
import app.drugs.talksooner.adapter.TalkArrayAdapter;

import come.giantinteractive.talksooner.model.TalkModelAll;
import come.giantinteractive.talksooner.utils.BaseContainerFragment;
import come.giantinteractive.talksooner.utils.StaticApiList;

@SuppressWarnings("unused")
@SuppressLint("DefaultLocale")
public class Talk extends Fragment {

	/** Define global variables over here */
	final int scrollViewTextViewsHeight = 150;
	private ProgressDialog pDialog;
	StaticApiList sal;
	TalkModelAll tma;
	JSONObject myJasonObject = null;
	private ListView lv;
	private ArrayList<TalkModelAll> m_ArrayList = null;
	TalkArrayAdapter taa;
	Set<String> uniqueValues = new HashSet<String>();
	TextView rowTextView = null;
	boolean vivek = false;
	LinearLayout myLinearLayout;
	String[] split_unique;
	int postid;
	String title;
	String thumsrc;
	String largeimg;
	String excert;
	String description;
	String cat;
	String myUrl;
	String jsonString;
	int mCurCheckPosition;
	String check_state = null;
	String ccc;
	String get_value;
	int check_cat = 0;
	View rootView;
	boolean isClicked = true;
	LinearLayout back;
	HorizontalScrollView hs;
	TextView tv;

	private TransparentProgressDialog pd;
	private Handler h;
	private Runnable r;
	LinearLayout scroll_ll;
	
	TextView[] myTextViews;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
		h = new Handler();
		pd = new TransparentProgressDialog(getActivity(), R.drawable.spinner);
		r = new Runnable() {
			@Override
			public void run() {
				if (pd.isShowing()) {
					pd.dismiss();
				}
			}
		};

		rootView = inflater.inflate(R.layout.talk, container, false);
		scroll_ll = (LinearLayout) rootView.findViewById(R.id.hhori_scroll_ll);
		myLinearLayout = (LinearLayout) rootView.findViewById(R.id.talk_ll_uni);
		tv = (TextView) rootView.findViewById(R.id.tv);
		tv.setVisibility(View.GONE);
		hs = (HorizontalScrollView) rootView.findViewById(R.id.hrz_scroll);

		hs.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// Toast.makeText(getActivity(), "Okay in scroll",
				// Toast.LENGTH_SHORT).show();

				return false;
			}
		});

		back = (LinearLayout) rootView.findViewById(R.id.header_btn);
		back.setVisibility(View.GONE);

		TextView header_tv = (TextView) rootView.findViewById(R.id.header_text);
		header_tv.setText("Talk");
		header_tv.setGravity(Gravity.CENTER);

		if (vivek != true) {
			new TalkAsyncTask().execute();
		} else {

			Log.i("viny", myLinearLayout.getChildCount() + " ccount");
			Arrays.sort(split_unique);

			// When we need dynamic number of text view's ..
			final int N = split_unique.length; // total number of textviews to
												// add

			myTextViews = new TextView[N]; // create an empty
															// array;

			for (int i = 0; i < N; i++) {
				// create a new textview
				rowTextView = new TextView(getActivity());

				// set some properties of rowTextView or something talk_ll_uni
				rowTextView.setText((split_unique[i].replaceAll(
						"[|?*<\":>+\\[\\]/']", "") + "   ").toUpperCase());
				rowTextView.setTextColor(getActivity().getResources().getColor(
						R.color.talk_red_bottom));
				rowTextView.setTextSize(20);
				rowTextView.setId(i);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, scrollViewTextViewsHeight);
				rowTextView.setLayoutParams(params);
				myLinearLayout.addView(rowTextView);
				// get value of clicked item over here ..
				rowTextView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						TextView tv = (TextView) v;
						get_value = tv.getText().toString().replace(" ", "");
						m_ArrayList = new ArrayList<TalkModelAll>();
						if (jsonString.length() > 0) {
							if (get_value.equalsIgnoreCase("ALL")) {
								try {
									isClicked = true;
									showData(jsonString, "", isClicked);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								try {
									isClicked = false;
									showData(jsonString, get_value, isClicked);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}

					}
				});
				myTextViews[i] = rowTextView;
			}

			Log.i("viny", myLinearLayout.getChildCount() + " ccount 1");
			// saving a reference to the textview for later
			 
			try {
				if (isClicked) {
					showData(jsonString, "", true);
				} else {
					showData(jsonString, get_value, false);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.talk_ll);
		ll.setBackgroundColor(getActivity().getResources().getColor(
				R.color.talk_red_bottom));

		Log.d("track", "=================>abc " + uniqueValues.size());

		return rootView;
	}

	private class TalkAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			scroll_ll.setVisibility(View.GONE);
			/*
			 * pDialog = new ProgressDialog(getActivity());
			 * pDialog.setMessage("Please wait ...");
			 * pDialog.setIndeterminate(false); pDialog.setCancelable(false);
			 * pDialog.show();
			 */
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			sal = new StaticApiList();
			myUrl = StaticApiList.talk_api;
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(myUrl);

			try {
				HttpResponse httpResponse = httpClient.execute(httpGet);
				System.out.println("httpResponse");

				InputStream inputStream = httpResponse.getEntity().getContent();
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				StringBuilder stringBuilder = new StringBuilder();
				String bufferedStrChunk = null;
				while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
					stringBuilder.append(bufferedStrChunk);
				}
				jsonString = stringBuilder.toString();
				Log.i("talk_all_json", jsonString);
				return stringBuilder.toString();

			} catch (ClientProtocolException cpe) {
				System.out.println("Exception generates caz of httpResponse :"
						+ cpe);
				cpe.printStackTrace();
			} catch (IOException ioe) {
				System.out
						.println("Second exception generates caz of httpResponse :"
								+ ioe);
				ioe.printStackTrace();
			}

			return null;
		}

		@SuppressLint("DefaultLocale")
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			vivek = true;
			check_state = jsonString;
			try {
				m_ArrayList = new ArrayList<TalkModelAll>();
				if (jsonString.length() > 0) {
					JSONArray jArray = new JSONArray(jsonString);

					check_cat = 1; // for All cat...

					for (int i = 0; i < jArray.length(); i++) {

						JSONObject jObject = jArray.getJSONObject(i);

						title = jObject.getString("title");
						thumsrc = jObject.getString("thumsrc");
						largeimg = jObject.getString("largeimg");
						excert = jObject.getString("excert");
						description = jObject.getString("description");
						cat = jObject.getString("cat");
						postid = jObject.getInt("postid");

						if (i == 0) {
							uniqueValues.add("  All");
						}

						uniqueValues.add(jObject.getString("cat")); // Set
																	// unique
																	// elements
																	// in string
																	// array
																	// list

						ccc = uniqueValues.toString();
						Log.d("unique_value", "========================> "
								+ ccc.length());

						Log.d("talklog", "Title -> " + title + " , thumsrc -> "
								+ thumsrc + " , largeimg -> " + largeimg
								+ " , excert -> " + excert
								+ " , description -> " + description
								+ " , cat -> " + cat + " , " + "PostId "
								+ postid);
						Log.d("talklog",
								"============================= end of " + i
										+ " ===============================");

						tma = new TalkModelAll();
						tma.title = title;
						tma.thumsrc = thumsrc;
						tma.largeimg = largeimg;
						tma.excert = excert;
						tma.description = description;
						tma.cat = cat;
						tma.postid = postid;
						tma.jsonString = jsonString;

						tma.textView = ccc;

						m_ArrayList.add(tma);

					}

					split_unique = ccc.split(",");
					Arrays.sort(split_unique);

					// When we need dynamic number of text view's ..
					final int N = split_unique.length; // total number of
														// textviews to add

					myTextViews = new TextView[N]; // create an empty array

					for (int i = 0; i < N; i++) {
						// create a new textview
						rowTextView = new TextView(getActivity());

						// set some properties of rowTextView or something
						// talk_ll_uni
						String textViewText = (split_unique[i].replaceAll(
								"[|?*<\":>+\\[\\]/']", "") + "   ")
								.toUpperCase();
						rowTextView.setText(textViewText);
						rowTextView.setTextColor(getActivity().getResources()
								.getColor(R.color.talk_red_bottom));
						rowTextView.setTextSize(20);
						rowTextView.setId(i);

						// set the background color of the ALL button
						if(textViewText.replace(" ", "").equalsIgnoreCase("ALL")) {
							rowTextView.setBackgroundResource(R.color.talk_red_bottom);
							rowTextView.setTextColor(getActivity().getResources()
									.getColor(R.color.white));
						} else {
							rowTextView.setBackgroundResource(R.color.white);
							rowTextView.setTextColor(getActivity().getResources()
									.getColor(R.color.talk_red_bottom));
						}
						
						// get value of clicked item over here ..
						rowTextView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								TextView tv = (TextView) v;
								get_value = tv.getText().toString()
										.replace(" ", "");

								try {

									m_ArrayList = new ArrayList<TalkModelAll>();
									if (jsonString.length() > 0) {
										if (get_value.equalsIgnoreCase("ALL")) {
											isClicked = true;
											showData(jsonString, "", isClicked);
										} else {
											isClicked = false;
											showData(jsonString, get_value,
													isClicked);
										}

									}

								} catch (Exception vivek) {
									vivek.printStackTrace();
								}

							}
						});

						// add the textview to the linearlayout
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT, scrollViewTextViewsHeight);
						rowTextView.setLayoutParams(params);
						myLinearLayout.addView(rowTextView);

						// saving a reference to the textview for later
						myTextViews[i] = rowTextView;
					}

				}

				taa = new TalkArrayAdapter(getActivity(), m_ArrayList);
				lv = (ListView) getActivity().findViewById(R.id.talk_list);
				lv.setVisibility(View.VISIBLE);
				lv.setAdapter(taa);
				lv.setDivider(null);
				lv.setDividerHeight(0);

				lv.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						TalkDetail fragment = new TalkDetail();
						Bundle bundle = new Bundle();

						bundle.putString("title", m_ArrayList.get(arg2).title);
						bundle.putString("largeimg",
								m_ArrayList.get(arg2).largeimg);
						bundle.putString("excert", m_ArrayList.get(arg2).excert);
						bundle.putString("description",
								m_ArrayList.get(arg2).description);
						bundle.putString("cat", m_ArrayList.get(arg2).cat);
						bundle.putString("header_title", "Talk");
						// bundle.putInt("postid",
						// m_ArrayList.get(arg2).postid);

						fragment.setArguments(bundle);
						Fragment parentFragment = (BaseContainerFragment) getParentFragment();
						// ABBAS, commented out lines below and added two lines underneath
//						if(parentFragment == null) {
//							Log.e("Talk", "Parent frag is empty");
//						}
//						((BaseContainerFragment) getParentFragment())
//								.replaceFragment(fragment, true);
//						FragmentManager fragmentManager = getFragmentManager();
//						fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
						
					    FragmentTransaction transaction = getFragmentManager().beginTransaction();

					    // Replace whatever is in the fragment_container view with this fragment,
					    // and add the transaction to the back stack
					    transaction.replace(R.id.frame_container, fragment);
					    transaction.addToBackStack(null);

					    // Commit the transaction
					    transaction.commit();

					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}

			// pDialog.dismiss();
			pd.dismiss();
			scroll_ll.setVisibility(View.VISIBLE);

		}

	}

	protected void showData(String jsonString, String filter, boolean filterFlag)
			throws JSONException {
		if (filterFlag) { // ALL is selected
			for (TextView tv : myTextViews) {
				String textViewText = tv.getText().toString().replace(" ", "");
				if (textViewText.equalsIgnoreCase("ALL")) {
					tv.setBackgroundResource(R.color.talk_red_bottom);
					tv.setTextColor(getActivity().getResources()
							.getColor(R.color.white));
					Log.d("ALL again! ===>", textViewText);
				} else {
					tv.setBackgroundResource(R.color.white);
					tv.setTextColor(getActivity().getResources()
							.getColor(R.color.talk_red_bottom));
				}
			}
		} else { // it is not ALL
			for (TextView tv : myTextViews) {
				String textViewText = tv.getText().toString().replace(" ", "");
				if (textViewText.equalsIgnoreCase("ALL")) {
					tv.setBackgroundResource(R.color.white);
					tv.setTextColor(getActivity().getResources()
							.getColor(R.color.talk_red_bottom));
				} else {
					tv.setBackgroundResource(R.color.white);
					tv.setTextColor(getActivity().getResources()
							.getColor(R.color.talk_red_bottom));
					if (textViewText.equalsIgnoreCase(filter)
							&& !textViewText.equalsIgnoreCase("")) {
						tv.setBackgroundResource(R.color.talk_red_bottom);
						tv.setTextColor(getActivity().getResources()
								.getColor(R.color.white));
					} 
				}
			}
		}

		m_ArrayList = new ArrayList<TalkModelAll>();
		JSONArray jArray = new JSONArray(jsonString);
		check_cat = 2; // For other type of cat..
		for (int i = 0; i < jArray.length(); i++) {

			JSONObject jObject = jArray.getJSONObject(i);
			// cat = jObject.getString("cat").replaceAll("[|?*<\":>+\\[\\]/']",
			// "").toUpperCase();

			cat = jObject.getString("cat").replace(" ", "");

			if (cat.equalsIgnoreCase(get_value) || filterFlag) {

				Log.d("vivek", "Het IM In");
				title = jObject.getString("title");
				thumsrc = jObject.getString("thumsrc");
				largeimg = jObject.getString("largeimg");
				excert = jObject.getString("excert");
				description = jObject.getString("description");
				cat = jObject.getString("cat");
				postid = jObject.getInt("postid");

				tma = new TalkModelAll();
				tma.title = title;
				tma.thumsrc = thumsrc;
				tma.largeimg = largeimg;
				tma.excert = excert;
				tma.description = description;
				tma.cat = cat;
				tma.postid = postid;

				m_ArrayList.add(tma);
			}/*
			 * else{ Toast.makeText(getActivity(), "IM in here.. ",
			 * Toast.LENGTH_LONG).show(); }
			 */

		}
		taa = new TalkArrayAdapter(getActivity(), m_ArrayList);
		lv = (ListView) rootView.findViewById(R.id.talk_list);
		lv.setVisibility(View.VISIBLE);
		lv.setAdapter(taa);
		lv.setDivider(null);
		lv.setDividerHeight(0);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				TalkDetail fragment = new TalkDetail();
				Bundle bundle = new Bundle();

				bundle.putString("title", m_ArrayList.get(arg2).title);
				bundle.putString("largeimg", m_ArrayList.get(arg2).largeimg);
				bundle.putString("excert", m_ArrayList.get(arg2).excert);
				bundle.putString("description",
						m_ArrayList.get(arg2).description);
				bundle.putString("cat", m_ArrayList.get(arg2).cat);
				bundle.putString("header_title", "Talk");
				// bundle.putInt("postid", m_ArrayList.get(arg2).postid);

				fragment.setArguments(bundle);
//				((BaseContainerFragment) getParentFragment()).replaceFragment(
//						fragment, true);
			    FragmentTransaction transaction = getFragmentManager().beginTransaction();

			    // Replace whatever is in the fragment_container view with this fragment,
			    // and add the transaction to the back stack
			    transaction.replace(R.id.frame_container, fragment);
			    transaction.addToBackStack(null);

			    // Commit the transaction
			    transaction.commit();

			}
		});
	}

	private class TransparentProgressDialog extends Dialog {

		private ImageView iv;

		public TransparentProgressDialog(Context context, int resourceIdOfImage) {
			super(context, R.style.TransparentProgressDialog);
			WindowManager.LayoutParams wlmp = getWindow().getAttributes();
			wlmp.gravity = Gravity.CENTER;
			getWindow().setAttributes(wlmp);
			setTitle(null);
			setCancelable(false);
			setOnCancelListener(null);
			LinearLayout layout = new LinearLayout(context);
			layout.setOrientation(LinearLayout.VERTICAL);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			iv = new ImageView(context);
			iv.setImageResource(resourceIdOfImage);
			layout.addView(iv, params);
			addContentView(layout, params);
		}

		@Override
		public void show() {
			super.show();
			RotateAnimation anim = new RotateAnimation(0.0f, 360.0f,
					Animation.RELATIVE_TO_SELF, .5f,
					Animation.RELATIVE_TO_SELF, .5f);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatCount(Animation.INFINITE);
			anim.setDuration(3000);
			iv.setAnimation(anim);
			iv.startAnimation(anim);
		}
	}

}

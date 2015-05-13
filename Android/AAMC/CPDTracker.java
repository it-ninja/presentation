package com.training.aamc;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class CPDTracker extends FragmentActivity {

	private static final String TAB_1_TAG = "tab_1";
	private static final String TAB_2_TAG = "tab_2";
	private static final String TAB_3_TAG = "tab_3";
	public FragmentTabHost mTabHost;
	View view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cpd_tracker);
		initView();
	}

	private void initView() {
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_1_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.summary_selector)),
				CPDSummary.class, null);
		
		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_2_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.create_statement_selector)),
						CPDCreateStatement.class, null);
		mTabHost.addTab(
				mTabHost.newTabSpec(TAB_3_TAG).setIndicator("",
						getResources().getDrawable(R.drawable.add_course_info_selector)),
						CPDAddCourseInfo.class, null);
		
		// Inflating color for the first time.
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			mTabHost.getTabWidget().getChildAt(i)
					.setBackgroundColor(Color.parseColor("#FFFFFF"));
		}

		mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
				.setBackgroundColor(Color.parseColor("#FFFFFF"));
		// ============== End of color inflation ==================

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				// Inflating color when tab is selected.
				for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
					mTabHost.getTabWidget().getChildAt(i)
							.setBackgroundColor(Color.parseColor("#FFFFFF"));

				}

				mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
						.setBackgroundColor(Color.parseColor("#FFFFFF"));
				// ============== End of color inflation ==================

			}
		});

		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			final TextView tv = (TextView) mTabHost.getTabWidget()
					.getChildAt(i).findViewById(android.R.id.title);
			
			if (tv == null)
				continue;
			else
				tv.setTextSize(8);

		}

	}

	@Override
	public void onBackPressed() {
		boolean isPopFragment = false;
		
		if (!isPopFragment) {
			finish();
		}
	}
	
	
	/**
	 * @author vivek
	 * @param position
	 * @param context
	 * 
	 * Method to be used for changing tab position dynamically ...
	 */
	
	public void myTabChangingFunction(int position, Context context)
	{
		// For tab one ..
		if(mTabHost.getCurrentTab() == 0)
		{
			if(position == 22)
			{
				((CPDTracker)context).mTabHost.getTabWidget().setCurrentTab(1); 
				mTabHost.setCurrentTabByTag(TAB_2_TAG);
			}
			else
			{
				((CPDTracker)context).mTabHost.getTabWidget().setCurrentTab(2); 
				mTabHost.setCurrentTabByTag(TAB_3_TAG);
			}
			
		}
		// For tab two ..
		else if(mTabHost.getCurrentTab() == 1)
		{
			((CPDTracker)context).mTabHost.getTabWidget().setCurrentTab(2); 
			mTabHost.setCurrentTabByTag(TAB_3_TAG);
		}
		// For tab three ..
		else if(mTabHost.getCurrentTab() == 2)
		{
			if(position == 23)
			{
				((CPDTracker)context).mTabHost.getTabWidget().setCurrentTab(1); 
				mTabHost.setCurrentTabByTag(TAB_2_TAG);
			}
			else
			{
				((CPDTracker)context).mTabHost.getTabWidget().setCurrentTab(0); 
				mTabHost.setCurrentTabByTag(TAB_1_TAG);
			}
			
		}
		else
		{
			
		}
	}
}


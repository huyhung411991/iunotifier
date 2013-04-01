package com.iuinsider.iunotifier;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class CourseDetailsActivity extends Activity {
	
	private String courseID = null; 
	
	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_details);
		
		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (courseID == null)
			finish();
		
		
		// Show the Up button in the action bar.
		setupActionBar();
	}

	// =========================================================================================
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_details, menu);
		return true;
	}

	// =========================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	// =========================================================================================
	/** Called when the user clicks the Announcements button */
	public void openAnnouncements(View view) {
		Intent intent = new Intent(this, AnnouncementsActivity.class);
		intent.putExtra(EXTRA_COURSE, courseID);
		startActivity(intent);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}

	/** Called when the user clicks the Push Announcement button */
	public void openPushAnnouncement(View view) {
		Intent intent = new Intent(this, PushAnnouncementActivity.class);
		intent.putExtra(EXTRA_COURSE, courseID);
		startActivity(intent);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
}

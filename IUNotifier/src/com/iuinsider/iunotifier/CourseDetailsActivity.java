package com.iuinsider.iunotifier;

import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CourseDetailsActivity extends Activity {

	private ParseUser currentUser = null;

	private String courseID = null;

	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_details);

		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (courseID == null)
			finish();
		DBRetriever.courseDetailsQuery(this, courseID);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		if (!isTaskRoot()) {
			CourseDetailsActivity.this.finish();
			overridePendingTransition(0, R.anim.slide_out_right);
		} else {
			Intent newIntent = new Intent(this, MainMenuActivity.class);
			CourseDetailsActivity.this.finish();
			startActivity(newIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
		}
	}

	// =========================================================================================
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.events, menu);

		if (currentUser != null) {
			MenuItem switchButton = menu.findItem(R.id.action_login);
			switchButton.setIcon(R.drawable.sign_in);
		}
		return true;
	}

	// =========================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_refresh:
			DBRetriever.courseDetailsQuery(this, courseID);
			break;
		default:
			break;
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

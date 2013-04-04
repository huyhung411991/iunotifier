package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;

public class CourseDetailsActivity extends Activity {

	private ParseUser currentUser = null;

	private String courseID = null;

	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// These are the course columns that we will retrieve
	private static final String[] PROJECTION = new String[] {
			DB.CourseDetails.ID, DB.CourseDetails.NAME,
			DB.CourseDetails.LECTURER, DB.CourseDetails.THEORY,
			DB.CourseDetails.LAB, DB.CourseDetails.CREDIT,
			DB.CourseDetails.PREREQUISITE };

	// This is the select criteria
	private static final String SELECTION = "";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_details);

		// Check current user
		currentUser = ParseUser.getCurrentUser();
		checkUser();

		// ListView listView = (ListView)
		// this.findViewById(R.id.course_details_list);
		// ProgressBar progressBar = (ProgressBar)
		// this.findViewById(R.id.course_details_progressBar);
		// listView.setEmptyView(progressBar);

		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (courseID == null)
			finish();
		DBRetriever.courseDetailsQuery(this, courseID);

		Uri courseDetailsUri = Uri.withAppendedPath(
				DB.CourseDetails.CONTENT_URI, courseID);
		Cursor courseCursor = getContentResolver().query(courseDetailsUri,
				PROJECTION, SELECTION, null, null);

		if (courseCursor.getCount() == 0) {
			// Course not found
		} else {
			loadCourseInfo(courseCursor, DB.CourseDetails.NAME, R.id.course_details_courseName_textView);
			loadCourseInfo(courseCursor, DB.CourseDetails.ID, R.id.course_details_courseID_textView);
			loadCourseInfo(courseCursor, DB.CourseDetails.LECTURER, R.id.course_details_courseLecturer_textView);
			loadCourseInfo(courseCursor, DB.CourseDetails.THEORY, R.id.course_details_courseTheory_textView);
			loadCourseInfo(courseCursor, DB.CourseDetails.LAB, R.id.course_details_courseLab_textView);
			
			int columnIndex = courseCursor.getColumnIndex(DB.CourseDetails.CREDIT);
			long credit = courseCursor.getLong(columnIndex);
			TextView t = (TextView) findViewById(R.id.course_details_courseCredit_textView);
			t.setText(String.valueOf(credit));
		}

		// Show the Up button in the action bar.
		setupActionBar();
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		if (!isTaskRoot()) {
			Intent in = new Intent();
			setResult(1, in);
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
		Intent intent;

		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_login:
			// Logout current user before login
			if (currentUser != null) {
				intent = new Intent(this, LogoutActivity.class);
				startActivityForResult(intent, 0);
			}
			// Go to user login page
			else {
				intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 0);
			}
			break;
		case R.id.action_refresh:
			DBRetriever.courseDetailsQuery(this, courseID);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// =========================================================================================
	// Doan code nay dung de check sau khi user login thanh cong thi icon user
	// se chuyen mau xanh
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 1) {
			CourseDetailsActivity.this.invalidateOptionsMenu();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		currentUser = ParseUser.getCurrentUser();
		MenuItem switchButton = menu.findItem(R.id.action_login);
		if (currentUser != null) {
			switchButton.setIcon(R.drawable.sign_in);
		} else {
			switchButton.setIcon(R.drawable.not_sign_in);
		}
		checkUser();
		return true;
	}

	// =========================================================================================
	public void checkUser() {
		if (currentUser == null) {
			findViewById(R.id.course_details_pushAnnouncement_button)
					.setVisibility(View.INVISIBLE);
			findViewById(R.id.course_details_seperator).setVisibility(
					View.INVISIBLE);
		} else {
			String userRole = currentUser.getString(DB.UserPermission.USER_COLUMN);
			if (userRole.equals(DB.UserPermission.USER_ADMIN)) {
				findViewById(R.id.course_details_pushAnnouncement_button)
						.setVisibility(View.VISIBLE);
				findViewById(R.id.course_details_seperator).setVisibility(
						View.VISIBLE);
			} else {
				findViewById(R.id.course_details_pushAnnouncement_button)
						.setVisibility(View.INVISIBLE);
				findViewById(R.id.course_details_seperator).setVisibility(
						View.INVISIBLE);
			}
		}
	}

	// =========================================================================================
	public void loadCourseInfo(Cursor courseCursor, String item, int viewID) {
		int columnIndex = courseCursor.getColumnIndex(item);
		String value = courseCursor.getString(columnIndex);
		TextView t = (TextView) findViewById(viewID);
		t.setText(value);
	}

	// =========================================================================================
	/** Called when the user clicks the Announcements button */
	public void openAnnouncements(View view) {
		Intent intent = new Intent(this, AnnouncementsActivity.class);
		intent.putExtra(EXTRA_COURSE, courseID);
		startActivityForResult(intent, 0);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}

	// =========================================================================================
	/** Called when the user clicks the Push Announcement button */
	public void openPushAnnouncement(View view) {
		Intent intent = new Intent(this, PushAnnouncementActivity.class);
		intent.putExtra(EXTRA_COURSE, courseID);
		startActivityForResult(intent, 0);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
}

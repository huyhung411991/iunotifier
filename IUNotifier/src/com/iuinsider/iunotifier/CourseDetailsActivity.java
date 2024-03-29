package com.iuinsider.iunotifier;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.iuinsider.iunotifier.providers.UserPermission;
import com.parse.Parse;
import com.parse.ParseUser;

public class CourseDetailsActivity extends Activity {

	private ParseUser currentUser = null;
	private String courseID = null;
	ProgressDialog progressDialog = null;

	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// These are the columns that we will retrieve
	private static final String[] PROJECTION = new String[] {
			DB.CourseDetails.ID, DB.CourseDetails.NAME,
			DB.CourseDetails.LECTURER, DB.CourseDetails.THEORY,
			DB.CourseDetails.LAB, DB.CourseDetails.CREDIT,
			DB.CourseDetails.PREREQUISITE };
	// This is the select criteria
	private static final String SELECTION = "";

	// =========================================================================================
	private class RemoteDataTask extends AsyncTask<String, Void, Void> {
		protected Cursor courseCursor = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			CourseDetailsActivity.this.progressDialog = ProgressDialog.show(
					CourseDetailsActivity.this, "Course Details",
					"Loading course details", true);
		}

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected Void doInBackground(String... params) {
			Context context = CourseDetailsActivity.this;
			String courseID = params[0];
			DBRetriever.courseDetailsQuery(context, courseID, false);

			Uri courseDetailsUri = Uri.withAppendedPath(
					DB.CourseDetails.CONTENT_URI, courseID);
			courseCursor = getContentResolver().query(courseDetailsUri,
					PROJECTION, SELECTION, null, null);

			return null;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Void result) {
			if (courseCursor.getCount() == 0) {
				// Course not found
			} else {
				courseCursor.moveToFirst();
				loadCourseInfo(courseCursor, DB.CourseDetails.NAME,
						R.id.course_details_courseName_textView);
				loadCourseInfo(courseCursor, DB.CourseDetails.ID,
						R.id.course_details_courseID_textView);
				loadCourseInfo(courseCursor, DB.CourseDetails.LECTURER,
						R.id.course_details_courseLecturer_textView);
				loadCourseInfo(courseCursor, DB.CourseDetails.THEORY,
						R.id.course_details_courseTheory_textView);
				loadCourseInfo(courseCursor, DB.CourseDetails.LAB,
						R.id.course_details_courseLab_textView);
				loadCourseInfo(courseCursor, DB.CourseDetails.CREDIT,
						R.id.course_details_courseCredit_textView);
				loadCourseInfo(courseCursor, DB.CourseDetails.PREREQUISITE,
						R.id.course_details_coursePrerequisite_textView);

				courseCursor.close();
				CourseDetailsActivity.this.progressDialog.dismiss();
			}
		}
	}

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_details);

		// Check current user
		Parse.initialize(this, IUNotifierApplication.APP_ID,
				IUNotifierApplication.CLIENT_KEY);
		currentUser = ParseUser.getCurrentUser();
		checkUser();

		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (TextUtils.isEmpty(courseID))
			finish();

		if (isConnected()) {
			new RemoteDataTask().execute(courseID);
			Log.d("Network", "Network available");
		} else {
			Log.d("Network", "Network unavailable");
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
			setResult(0, in);
			CourseDetailsActivity.this.finish();
			overridePendingTransition(0, R.anim.slide_out_right);
		} else {
			Intent newIntent = new Intent(this, CoursesActivity.class);
			CourseDetailsActivity.this.finish();
			startActivity(newIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
		}
	}

	// =========================================================================================
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.course_details, menu);

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
		case android.R.id.home: // Select home button
			Intent parentActivityIntent = new Intent(this,
					MainMenuActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
			finish();
			return true;

		case R.id.action_login: // Select login button
			// Logout current user before login
			if (currentUser != null) {
				intent = new Intent(this, LogoutActivity.class);
				startActivityForResult(intent, 0);
			} else { // Go to user login page
				intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 0);
			}
			break;

		case R.id.action_refresh: // Select refresh button
			new RemoteDataTask().execute(courseID);
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

		Toast toast = null;
		switch (resultCode) {
		case 0:
			CourseDetailsActivity.this.invalidateOptionsMenu();
			return;
		case 1:
			toast = Toast.makeText(this, "Login Successfully",
					Toast.LENGTH_SHORT);
			CourseDetailsActivity.this.invalidateOptionsMenu();
			break;
		case 2:
			toast = Toast.makeText(this, "Logout Successfully",
					Toast.LENGTH_SHORT);
			CourseDetailsActivity.this.invalidateOptionsMenu();
			break;
		default:
			break;
		}

		if (toast != null) {
			toast.setGravity(Gravity.CENTER_VERTICAL
					| Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
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
		int pushVisibility = View.INVISIBLE;
		int separatorVisibility = View.INVISIBLE;

		if (currentUser == null) {
			pushVisibility = View.INVISIBLE;
			separatorVisibility = View.INVISIBLE;
		} else {
			String userRole = currentUser.getString(UserPermission.USER_COLUMN);
			String courseID = getIntent().getStringExtra(EXTRA_COURSE);
			
			if (UserPermission.hasPushPermission1(userRole)) {
				pushVisibility = View.VISIBLE;
				separatorVisibility = View.VISIBLE;
			} else if (UserPermission.hasPushPermission2(this, userRole,
					courseID)) {
				pushVisibility = View.VISIBLE;
				separatorVisibility = View.VISIBLE;
			} else {
				pushVisibility = View.INVISIBLE;
				separatorVisibility = View.INVISIBLE;
			}
		}

		findViewById(R.id.course_details_pushAnnouncement_button)
				.setVisibility(pushVisibility);
		findViewById(R.id.course_details_seperator).setVisibility(
				separatorVisibility);
	}

	// =========================================================================================
	public void loadCourseInfo(Cursor courseCursor, String item, int viewID) {
		TextView textView = (TextView) findViewById(viewID);
		int columnIndex = courseCursor.getColumnIndex(item);

		if (item.equals(DB.CourseDetails.CREDIT)) {
			long credit = courseCursor.getLong(columnIndex);
			textView.setText(String.valueOf(credit));
		} else {
			String value = courseCursor.getString(columnIndex);
			if (value == null || value.isEmpty())
				value = "None";
			textView.setText(value);
		}
	}

	// =========================================================================================
	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return (activeNetwork != null && activeNetwork.isConnected());
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

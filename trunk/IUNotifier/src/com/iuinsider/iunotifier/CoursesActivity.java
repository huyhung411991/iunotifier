package com.iuinsider.iunotifier;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.Parse;
import com.parse.ParseUser;

public class CoursesActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ParseUser currentUser = null;
	private SimpleCursorAdapter mAdapter = null;
	private String departmentID;

	private static final String EXTRA_DEPARTMENT = ".com.iuinsider.iunotifier.DEPARTMENT";
	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// These are the columns that we will retrieve
	private static final String[] COURSES_PROJECTION = new String[] {
			DB.Courses._ID, DB.Courses.ID, DB.Courses.NAME };
	private static final String[] USER_COURSES_PROJECTION = new String[] {
			DB.UserCourses._ID, DB.UserCourses.ID, DB.UserCourses.NAME };

	// This is the select criteria
	private static final String COURSES_SELECTION = DB.Courses.NAME
			+ " NOTNULL AND " + DB.Courses.NAME + " != ''";
	private static final String USER_COURSES_SELECTION = DB.UserCourses.NAME
			+ " NOTNULL AND " + DB.UserCourses.NAME + " != ''";

	// This is the sorting order
	private static final String COURSES_SORTORDER = DB.Courses.NAME + " ASC";
	private static final String USER_COURSES_SORTORDER = DB.UserCourses.NAME
			+ " ASC";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courses);

		// Get current user
		Parse.initialize(this, IUNotifierApplication.APP_ID,
				IUNotifierApplication.CLIENT_KEY);
		currentUser = ParseUser.getCurrentUser();

		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.course_progressBar);
		getListView().setEmptyView(progressBar);

		departmentID = getIntent().getStringExtra(EXTRA_DEPARTMENT);
		if (isConnected()) {
			if (departmentID == null || !departmentID.equals("USER"))
				DBRetriever.coursesQuery(this, departmentID, false);
			Log.d("Network", "Network available");
		} else {
			Log.d("Network", "Network unavailable");
		}

		// For the cursor adapter, specify which columns go into which views
		String[] fromCoursesColumns = { DB.Courses.NAME, DB.Courses.ID };
		String[] fromUserCoursesColumns = { DB.UserCourses.NAME,
				DB.UserCourses.ID };
		int[] toViews = { android.R.id.text1, android.R.id.text2 };

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		if (departmentID == null || !departmentID.equals("USER"))
			mAdapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_2, null,
					fromCoursesColumns, toViews, 0);
		else
			mAdapter = new SimpleCursorAdapter(this,
					android.R.layout.simple_list_item_2, null,
					fromUserCoursesColumns, toViews, 0);
		setListAdapter(mAdapter);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);

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
			CoursesActivity.this.finish();

			if (departmentID == null || !departmentID.equals("USER")) {
				overridePendingTransition(0, R.anim.slide_out_right);
			} else {
				overridePendingTransition(0, R.anim.push_up_out);
			}
		} else {
			Intent newIntent = new Intent(this, DepartmentsActivity.class);
			CoursesActivity.this.finish();
			startActivity(newIntent);
			overridePendingTransition(0, R.anim.slide_out_right);

			if (departmentID == null || !departmentID.equals("USER")) {
				overridePendingTransition(0, R.anim.slide_out_right);
			} else {
				overridePendingTransition(0, R.anim.push_up_out);
			}
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
		getMenuInflater().inflate(R.menu.courses, menu);

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
			DBRetriever.coursesQuery(this, departmentID, false);
			break;

		case R.id.action_reload_all: // Select reload all button
			DBRetriever.coursesQuery(this, departmentID, true);
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
			CoursesActivity.this.invalidateOptionsMenu();
			return;
		case 1:
			toast = Toast.makeText(this, "Login Successfully",
					Toast.LENGTH_SHORT);
			CoursesActivity.this.invalidateOptionsMenu();
			break;
		case 2:
			toast = Toast.makeText(this, "Logout Successfully",
					Toast.LENGTH_SHORT);
			CoursesActivity.this.invalidateOptionsMenu();
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

	// =========================================================================================
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		currentUser = ParseUser.getCurrentUser();
		MenuItem switchButton = menu.findItem(R.id.action_login);
		if (currentUser != null) {
			switchButton.setIcon(R.drawable.sign_in);
		} else {
			switchButton.setIcon(R.drawable.not_sign_in);
		}
		return true;
	}

	// =========================================================================================
	@Override
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		if (departmentID == null || departmentID.equals("ALL"))
			return new CursorLoader(this, DB.Courses.CONTENT_URI,
					COURSES_PROJECTION, COURSES_SELECTION, null,
					COURSES_SORTORDER);
		else if (!departmentID.equals("USER")) {
			String newSelection = COURSES_SELECTION + " AND "
					+ DB.Courses.DEPARTMENT_ID + " = '" + departmentID + "'";
			return new CursorLoader(this, DB.Courses.CONTENT_URI,
					COURSES_PROJECTION, newSelection, null, COURSES_SORTORDER);
		} else
			return new CursorLoader(this, DB.UserCourses.CONTENT_URI,
					USER_COURSES_PROJECTION, USER_COURSES_SELECTION, null,
					USER_COURSES_SORTORDER);
	}

	// =========================================================================================
	@Override
	// Called when a previously created loader has finished loading
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return.)
		mAdapter.swapCursor(data);
	}

	// =========================================================================================
	// Called when a previously created loader is reset, making the data
	// unavailable
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed. We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}

	// =========================================================================================
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Cursor mCursor = mAdapter.getCursor();
		mCursor.moveToPosition(position);
		String columnName = DB.Courses.ID;
		int columnIndex = mCursor.getColumnIndex(columnName);
		String courseID = mCursor.getString(columnIndex);

		Intent intent = new Intent(this, CourseDetailsActivity.class);
		intent.putExtra(EXTRA_COURSE, courseID);
		startActivityForResult(intent, 0);

		// New, more advanced and easy to use transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}

	// =========================================================================================
	public boolean isConnected() {
		ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		return (activeNetwork != null && activeNetwork.isConnected());
	}
}

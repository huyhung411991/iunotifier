package com.iuinsider.iunotifier;

import java.util.Date;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;

public class CoursesActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter mAdapter = null;
	private String departmentID;

	private static final String LAST_UPDATE = ".com.iuinsider.iunotifier.LAST_UPDATE";
	private static final String EXTRA_DEPARTMENT = ".com.iuinsider.iunotifier.DEPARTMENT";
	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// These are the Contacts rows that we will retrieve
	private static final String[] PROJECTION = new String[] { DB.Courses._ID,
			DB.Courses.ID, DB.Courses.NAME };

	// This is the select criteria
	private static final String SELECTION = DB.Courses.NAME + " NOTNULL AND "
			+ DB.Courses.NAME + " != ''";

	// This is the sorting order
	private static final String SORTORDER = DB.Courses.ID + " ASC";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_courses);

		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.course_progressBar);
		getListView().setEmptyView(progressBar);

		departmentID = getIntent().getStringExtra(EXTRA_DEPARTMENT);
		if (departmentID == null)
			departmentID = "ALL";

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEdit = pref.edit();
			String lastAllUpdate = pref.getString(LAST_UPDATE + ".ALL", null);
			String lastCourseUpdate = pref.getString(LAST_UPDATE + "."
					+ departmentID, null);

			if ((lastCourseUpdate == null && lastAllUpdate == null)
					|| (lastCourseUpdate == null)
					|| (lastAllUpdate != null && lastCourseUpdate
							.compareTo(lastAllUpdate) <= 0))
				DBRetriever.coursesQuery(this, departmentID, lastAllUpdate);
			else
				DBRetriever.coursesQuery(this, departmentID, lastCourseUpdate);

			prefEdit.putString(LAST_UPDATE + "." + departmentID,
					DBRetriever.DateToString(new Date()));
			prefEdit.commit();
			Log.d("Network", "Network available");
		} else {
			Log.d("Network", "Network unavailable");
		}

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DB.Courses.NAME, DB.Courses.ID };
		int[] toViews = { android.R.id.text1, android.R.id.text2 };

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, null, fromColumns,
				toViews, 0);
		setListAdapter(mAdapter);

		// Prepare the loader. Either re-connect with an existing one,
		// or start a new one.
		getLoaderManager().initLoader(0, null, this);

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
		getMenuInflater().inflate(R.menu.courses, menu);
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
	@Override
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		if (departmentID == null || departmentID.equals("ALL"))
			return new CursorLoader(this, DB.Courses.CONTENT_URI, PROJECTION,
					SELECTION, null, SORTORDER);
		else {
			String newSelection = SELECTION + " AND "
					+ DB.Courses.DEPARTMENT_ID + " = '" + departmentID + "'";
			return new CursorLoader(this, DB.Courses.CONTENT_URI, PROJECTION,
					newSelection, null, SORTORDER);
		}
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
		startActivity(intent);

		// New, more advanced and easy to use transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
}

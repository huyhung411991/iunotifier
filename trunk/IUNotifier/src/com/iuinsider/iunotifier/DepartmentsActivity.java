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
import com.parse.ParseUser;

public class DepartmentsActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter mAdapter = null;
	private ParseUser currentUser = null;
	private static final String EXTRA_DEPARTMENT = ".com.iuinsider.iunotifier.DEPARTMENT";

	// These are the Contacts rows that we will retrieve
	private static final String[] PROJECTION = new String[] {
			DB.Departments._ID, DB.Departments.ID, DB.Departments.NAME };

	// This is the select criteria
	private static final String SELECTION = "((" + DB.Departments.NAME
			+ " NOTNULL) AND (" + DB.Departments.NAME + " != '' ))";

	// This is the sorting order
	private static final String SORTORDER = DB.Departments.NAME + " DESC";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_department);

		currentUser = ParseUser.getCurrentUser();
		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.department_progressBar);
		getListView().setEmptyView(progressBar);

		if (isConnected()) {
			DBRetriever.departmentsQuery(this, false);
			Log.d("Network", "Network available");
		} else {
			Log.d("Network", "Network unavailable");
		}

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DB.Departments.NAME, DB.Departments.ID };
		int[] toViews = { android.R.id.text1, android.R.id.text2 };

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.custom_simple_list_item_3, null, fromColumns, toViews,
				0);
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
			DepartmentsActivity.this.finish();
			overridePendingTransition(0, R.anim.slide_out_right);
		} else {
			Intent newIntent = new Intent(this, MainMenuActivity.class);
			DepartmentsActivity.this.finish();
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
			Intent parentActivityIntent = new Intent(this, MainMenuActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
			finish();
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
			DBRetriever.departmentsQuery(this, false);
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
		
		if (resultCode == 0) {
			DepartmentsActivity.this.invalidateOptionsMenu();
			return;
		} else if (resultCode == 1) {
			toast = Toast.makeText(this, "Login Successfully", Toast.LENGTH_LONG);
			DepartmentsActivity.this.invalidateOptionsMenu();
		} else if (resultCode == 2) {
			toast = Toast.makeText(this, "Logout Successfully", Toast.LENGTH_LONG);
			DepartmentsActivity.this.invalidateOptionsMenu();
		}
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.show();
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
		return true;
	}

	// =========================================================================================
	@Override
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(this, DB.Departments.CONTENT_URI, PROJECTION,
				SELECTION, null, SORTORDER);
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
		String columnName = DB.Departments.ID;
		int columnIndex = mCursor.getColumnIndex(columnName);
		String departmentID = mCursor.getString(columnIndex);

		Intent intent = new Intent(this, CoursesActivity.class);
		intent.putExtra(EXTRA_DEPARTMENT, departmentID);
		startActivityForResult(intent, 0);

		// New, more advanced and easy to use transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
	
	// =========================================================================================
		public boolean isConnected() {
			ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			
			return (activeNetwork != null && activeNetwork.isConnected());
		}

	// =========================================================================================
	public void openMyCourse(View view) {
		if (currentUser == null) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivityForResult(intent, 0);
		} else {
			Intent intent = new Intent(this, CoursesActivity.class);
			intent.putExtra(EXTRA_DEPARTMENT, "USER");
			startActivity(intent);

			// Transition animation
			overridePendingTransition(R.anim.push_down_in, 0);
		}
	}
}

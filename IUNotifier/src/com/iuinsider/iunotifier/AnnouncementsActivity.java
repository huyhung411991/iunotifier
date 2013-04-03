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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;

public class AnnouncementsActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ParseUser currentUser = null;
	private SimpleCursorAdapter mAdapter = null;
	private String courseID;

	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// These are the Contacts rows that we will retrieve
	private static final String[] PROJECTION = new String[] { DB.Announce._ID,
			DB.Announce.MESSAGE };

	// This is the select criteria
	private static final String SELECTION = DB.Announce.MESSAGE
			+ " NOTNULL AND " + DB.Announce.MESSAGE + " != ''";

	// This is the sorting order
	private static final String SORTORDER = DB.Announce.UPDATED_AT + " DESC";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_announcements);

		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.announcements_progressBar);
		getListView().setEmptyView(progressBar);

		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (courseID == null)
			finish();

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			DBRetriever.announcementsQuery(this, courseID);
			Log.d("Network", "Network available");
		} else {
			Log.d("Network", "Network unavailable");
		}

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DB.Announce.MESSAGE };
		int[] toViews = { android.R.id.text1 };

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, null, fromColumns,
				toViews, 0);
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
			setResult(1, in);
			AnnouncementsActivity.this.finish();
			overridePendingTransition(0, R.anim.slide_out_right);
		} else {
			Intent newIntent = new Intent(this, MainMenuActivity.class);
			AnnouncementsActivity.this.finish();
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
			DBRetriever.announcementsQuery(this, courseID);
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
			AnnouncementsActivity.this.invalidateOptionsMenu();
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
		return true;
	}

	// =========================================================================================
	@Override
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		if (courseID == null || courseID.equals("ALL"))
			return new CursorLoader(this, DB.Announce.CONTENT_URI, PROJECTION,
					SELECTION, null, SORTORDER);
		else {
			String newSelection = SELECTION + " AND " + DB.Announce.COURSE_ID
					+ " = '" + courseID + "'";
			return new CursorLoader(this, DB.Announce.CONTENT_URI, PROJECTION,
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
		/*
		 * Cursor mCursor = mAdapter.getCursor();
		 * mCursor.moveToPosition(position); String columnName = DB.News.LINK;
		 * int columnIndex = mCursor.getColumnIndex(columnName); String link =
		 * mCursor.getString(columnIndex);
		 * 
		 * Intent intent = new Intent(this, WebViewActivity.class);
		 * intent.putExtra(EXTRA_LINK, link); startActivityForResult(intent, 0);
		 * // startActivity(intent);
		 * 
		 * // New, more advanced and easy to use transition animation
		 * overridePendingTransition(R.anim.slide_in_right, 0);
		 */
	}
}

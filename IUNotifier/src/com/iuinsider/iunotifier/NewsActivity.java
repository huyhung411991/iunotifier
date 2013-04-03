package com.iuinsider.iunotifier;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;

public class NewsActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ParseUser currentUser = null;
	private SimpleCursorAdapter mAdapter = null;

	private static final String EXTRA_LINK = ".com.iuinsider.iunotifier.LINK";

	// These are the Contacts rows that we will retrieve
	private static final String[] PROJECTION = new String[] { DB.News._ID,
			DB.News.TITLE, DB.News.LINK, DB.News.SOURCE, DB.News.CREATED_AT };

	// This is the select criteria
	private static final String SELECTION = "";

	// This is the sorting order
	private static final String SORTORDER = DB.News.CREATED_AT + " DESC";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.news_progressBar);
		getListView().setEmptyView(progressBar);

		currentUser = ParseUser.getCurrentUser();
		DBRetriever.allNewsQuery(this);

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DB.News.TITLE, DB.News.SOURCE,
				DB.News.CREATED_AT };
		int[] toViews = { android.R.id.text1, android.R.id.text2, R.id.text3 };

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.custom_simple_list_item_2, null, fromColumns, toViews,
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
			NewsActivity.this.finish();
			overridePendingTransition(0, R.anim.slide_out_right);
		} else {
			Intent newIntent = new Intent(this, MainMenuActivity.class);
			NewsActivity.this.finish();
			startActivity(newIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
		}
	}

	// =========================================================================================
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);

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
			DBRetriever.allNewsQuery(this);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// =========================================================================================
	@Override
	// Called when a new Loader needs to be created
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new CursorLoader(this, DB.News.CONTENT_URI, PROJECTION,
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
		String columnName = DB.News.LINK;
		int columnIndex = mCursor.getColumnIndex(columnName);
		String link = mCursor.getString(columnIndex);

		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra(EXTRA_LINK, link);
		startActivityForResult(intent, 0);
		// startActivity(intent);

		// New, more advanced and easy to use transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
}

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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.Parse;
import com.parse.ParseUser;

public class NewsActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ParseUser currentUser = null;
	private SimpleCursorAdapter mAdapter = null;
	private static String sortCondition = "HCMIU";

	private static final String EXTRA_LINK = ".com.iuinsider.iunotifier.LINK";

	// These are the columns that we will retrieve
	private static final String[] PROJECTION = new String[] { DB.News._ID,
			DB.News.TITLE, DB.News.LINK, DB.News.SOURCE, DB.News.UPDATED_AT };
	// This is the select criteria
	private static final String SELECTION = "";
	// This is the sorting order
	private static final String SORTORDER = DB.News.UPDATED_AT + " DESC";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);

		// Get current user
		Parse.initialize(this, IUNotifierApplication.APP_ID,
				IUNotifierApplication.CLIENT_KEY);
		currentUser = ParseUser.getCurrentUser();

		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.news_progressBar);
		getListView().setEmptyView(progressBar);

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DB.News.TITLE, /* DB.News.SOURCE, */
		DB.News.UPDATED_AT };
		int[] toViews = { android.R.id.text1, /* android.R.id.text2, */R.id.text3 };

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.custom_simple_list_item_3, null, fromColumns, toViews,
				0);
		setListAdapter(mAdapter);

		// Set up new sources spinner
		Spinner spinner = (Spinner) findViewById(R.id.news_newsSources_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.news_sources,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				sortCondition = (String) parent.getItemAtPosition(pos);
				DBRetriever.newsQuery(NewsActivity.this, sortCondition);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Prepare the loader. Either re-connect with an existing one, or start
		// a new one.
		getLoaderManager().initLoader(0, null, this);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		if (!isTaskRoot()) {
			Intent intent = new Intent();
			setResult(0, intent);
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
			DBRetriever.newsQuery(this, sortCondition);
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
			NewsActivity.this.invalidateOptionsMenu();
			return;
		case 1:
			toast = Toast.makeText(this, "Login Successfully",
					Toast.LENGTH_SHORT);
			NewsActivity.this.invalidateOptionsMenu();
			break;
		case 2:
			toast = Toast.makeText(this, "Logout Successfully",
					Toast.LENGTH_SHORT);
			NewsActivity.this.invalidateOptionsMenu();
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

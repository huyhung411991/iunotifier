package com.iuinsider.iunotifier;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.ToggleButton;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;

public class EventsActivity extends ListActivity implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ParseUser currentUser = null;
	private SimpleCursorAdapter mAdapter = null;
	private Context context;
	private String sortCondition = "All";

	// These are the Contacts rows that we will retrieve
	private static final String[] PROJECTION = new String[] { DB.Events._ID,
			DB.Events.TITLE, DB.Events.DESCRIPTION, DB.Events.DATE, DB.Events.PLACE,
			DB.Events.CREATED_AT};

	private static final String SELECTION = "";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);

		ProgressBar progressBar = (ProgressBar) this
				.findViewById(R.id.events_progressBar);
		getListView().setEmptyView(progressBar);

		currentUser = ParseUser.getCurrentUser();
		context = this;

		DBRetriever.allEventsQuery(this, sortCondition);

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DB.Events.TITLE, DB.Events.CREATED_AT };
		int[] toViews = { android.R.id.text1, android.R.id.text2 }; // The TextView in
												// simple_list_item_1

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(this,
				R.layout.custom_simple_list_item_2, null, fromColumns,
				toViews, 0);
		setListAdapter(mAdapter);

		// Check the toogle buttons
		final ToggleButton tg_all = (ToggleButton) findViewById(R.id.events_allEvents_button);
		final ToggleButton tg_today = (ToggleButton) findViewById(R.id.events_todayEvents_button);
		final ToggleButton tg_upcoming = (ToggleButton) findViewById(R.id.events_upcomingEvents_button);

		tg_all.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sortCondition = (String) tg_all.getText();
				tg_today.setChecked(false);
				tg_upcoming.setChecked(false);
				DBRetriever.allEventsQuery(context, sortCondition);
			}
		});

		tg_today.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sortCondition = (String) tg_today.getText();
				tg_all.setChecked(false);
				tg_upcoming.setChecked(false);
				DBRetriever.allEventsQuery(context, sortCondition);
			}
		});

		tg_upcoming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sortCondition = (String) tg_upcoming.getText();
				tg_today.setChecked(false);
				tg_all.setChecked(false);
				DBRetriever.allEventsQuery(context, sortCondition);
			}
		});

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
			EventsActivity.this.finish();
			overridePendingTransition(0, R.anim.slide_out_right);
		} else {
			Intent newIntent = new Intent(this, MainMenuActivity.class);
			EventsActivity.this.finish();
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
			DBRetriever.allEventsQuery(this, sortCondition);
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
		return new CursorLoader(this, DB.Events.CONTENT_URI, PROJECTION,
				SELECTION, null, null);
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

		String eventTitle, eventDescription, eventPlace, eventDateTime;

		int columnIndex = mCursor.getColumnIndex(DB.Events.TITLE);
		eventTitle = mCursor.getString(columnIndex);

		columnIndex = mCursor.getColumnIndex(DB.Events.DESCRIPTION);
		eventDescription = mCursor.getString(columnIndex);

		columnIndex = mCursor.getColumnIndex(DB.Events.PLACE);
		eventPlace = mCursor.getString(columnIndex);

		columnIndex = mCursor.getColumnIndex(DB.Events.DATE);
		eventDateTime = mCursor.getString(columnIndex);

		Bundle bundle = new Bundle();
		bundle.putString("EventTitle", eventTitle);
		bundle.putString("EventDescription", eventDescription);
		bundle.putString("EventDateTime", eventDateTime);
		bundle.putString("EventPlace", eventPlace);

		Intent intent = new Intent(this, EventDetailsActivity.class);
		intent.putExtras(bundle);
		startActivity(intent);

		// New, more advanced and easy to use transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
}

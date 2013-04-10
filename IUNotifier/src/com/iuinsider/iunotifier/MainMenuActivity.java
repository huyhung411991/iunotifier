package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseUser;

public class MainMenuActivity extends Activity {

	ParseUser currentUser = null;

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		Parse.initialize(this, IUNotifierApplication.APPLICATION_ID, IUNotifierApplication.CLIENT_KEY);
		currentUser = ParseUser.getCurrentUser();

		// Load icon
		loadGridViewIcons();
	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);

		// Check current user is existed
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
		case R.id.action_login:

			// Logout current user before login
			if (currentUser != null) {
				intent = new Intent(this, LogoutActivity.class);
				startActivityForResult(intent, 0);
			} else { // Go to user login page
				intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, 0);
			}
			break;

		case R.id.action_reset:
			intent = new Intent(this, ResetDataActivity.class);
			startActivityForResult(intent, 0);
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
			MainMenuActivity.this.invalidateOptionsMenu();
			return;
		} else if (resultCode == 1) {
			toast = Toast.makeText(this, "Login Successfully",
					Toast.LENGTH_LONG);
			MainMenuActivity.this.invalidateOptionsMenu();
		} else if (resultCode == 2) {
			toast = Toast.makeText(this, "Logout Successfully",
					Toast.LENGTH_LONG);
			MainMenuActivity.this.invalidateOptionsMenu();
		} else if (resultCode == 3) {
			toast = Toast.makeText(this, "All Data Is Deleted",
					Toast.LENGTH_LONG);
		}
		toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL,
				0, 0);
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

	// LOAD MAIN MENU ICONS WHEN THE APP
	// RUNS===========================================================
	private void loadGridViewIcons() {

		GridView gridview = (GridView) findViewById(R.id.main_menu_gridView);
		gridview.setAdapter(new MainMenuActivity_ImageAdapter(this));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Use image position to invoke News Activity
				if (position == 0)
					openNews(view);
				// Use image position to invoke Events Activity
				if (position == 1)
					openEvents(view);
				// Use image position to invoke Course Activity
				if (position == 2)
					openCourse(view);
			}
		});
	}

	// =========================================================================================
	/** Called when the user clicks the News button */
	public void openNews(View view) {
		Intent intent = new Intent(this, NewsActivity.class);
		startActivityForResult(intent, 0);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}

	/** Called when the user clicks the Events button */
	public void openEvents(View view) {
		Intent intent = new Intent(this, EventsActivity.class);
		startActivityForResult(intent, 0);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}

	/** Called when the user clicks the Course button */
	public void openCourse(View view) {
		Intent intent = new Intent(this, DepartmentsActivity.class);
		startActivityForResult(intent, 0);

		// Transition animation
		overridePendingTransition(R.anim.slide_in_right, 0);
	}
}

package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.Parse;

public class PushAnnouncementActivity extends Activity {

//	private ParseUser currentUser = null;
	private EditText announcementContent;
	private Button announceButton;
	private String courseID;
	private Context context;
	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_announcement);
		Parse.initialize(this, IUNotifierApplication.APPLICATION_ID, IUNotifierApplication.CLIENT_KEY);

		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (courseID == null) {
			finish();
		}

		context = this;
		announcementContent = (EditText) findViewById(R.id.announcement_content);
		announceButton = (Button) findViewById(R.id.announce_button);
		announceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String message = announcementContent.getText().toString();
				DBRetriever.pushAnnouncement(courseID, message, context);
			}
		});
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		PushAnnouncementActivity.this.finish();
		overridePendingTransition(0, R.anim.slide_out_right);
	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.push_announcement, menu);
		return true;
	}

	// =========================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(this,
					MainMenuActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
			finish();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

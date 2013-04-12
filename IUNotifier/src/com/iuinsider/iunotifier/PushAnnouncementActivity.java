package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.iuinsider.iunotifier.providers.DBRetriever;

public class PushAnnouncementActivity extends Activity {

	private EditText announcementContent;
	private Button announceButton;
	private String courseID;

	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_announcement);

		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (TextUtils.isEmpty(courseID))
			finish();

		announcementContent = (EditText) findViewById(R.id.announcement_content);
		announceButton = (Button) findViewById(R.id.announce_button);
		announceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String message = announcementContent.getText().toString();
				DBRetriever.pushAnnouncement(PushAnnouncementActivity.this,
						courseID, message);
			}
		});

		// Show the Up button in the action bar.
		setupActionBar();
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		PushAnnouncementActivity.this.finish();
		overridePendingTransition(0, R.anim.slide_out_right);
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
		getMenuInflater().inflate(R.menu.push_announcement, menu);
		return true;
	}

	// =========================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

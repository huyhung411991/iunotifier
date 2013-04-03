package com.iuinsider.iunotifier;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;

public class PushAnnouncementActivity extends Activity {

	private ParseUser currentUser = null;
	private EditText announcementContent;
	private TextView stateMessage;
	private Button announceButton;
	private String courseID;
	
	private static final String EXTRA_COURSE = ".com.iuinsider.iunotifier.COURSE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_announcement);
		
		courseID = getIntent().getStringExtra(EXTRA_COURSE);
		if (courseID == null)
			finish();

		announcementContent = (EditText) findViewById(R.id.announcement_content);
		stateMessage = (TextView) findViewById(R.id.state_message);
		announceButton = (Button) findViewById(R.id.announce_button);
		announceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String message = announcementContent.getText().toString();
				DBRetriever.pushAnnouncement(courseID, message, stateMessage);
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
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

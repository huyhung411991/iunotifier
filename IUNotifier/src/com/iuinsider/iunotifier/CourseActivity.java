package com.iuinsider.iunotifier;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

public class CourseActivity extends Activity {

	private EditText announcementContent;
	private TextView stateMessage;
	private Button announceButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course);

		announcementContent = (EditText) findViewById(R.id.announcement_content);
		stateMessage = (TextView) findViewById(R.id.state_message);
		announceButton = (Button) findViewById(R.id.announce_button);
		announceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				String message = announcementContent.getText().toString();
				// ParsePush push = new ParsePush();
				// push.setChannel("CourseAnnouncement");
				// push.setMessage(message);
				// push.sendInBackground(new SendCallback() {
				//
				// @Override
				// public void done(ParseException e) {
				// // TODO Auto-generated method stub
				// stateMessage.setText("Complete Announcement");
				// }
				// });
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("message", message);
				ParseCloud.callFunctionInBackground("makeCourseAnnouncement",
						params, new FunctionCallback<String>() {
							public void done(String messageReturned,
									ParseException e) {
								if (e == null) {
									stateMessage.setText(messageReturned);
								}
							}
						});
			}
		});
	}

	// =========================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_refresh:
			// DBRetriever.allEventsQuery(contentResolver, sortCondition);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		CourseActivity.this.finish();
		overridePendingTransition(0, R.anim.slide_out_right);
	}
}

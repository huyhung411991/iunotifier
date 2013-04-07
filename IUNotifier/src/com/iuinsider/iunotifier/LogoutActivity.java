package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iuinsider.iunotifier.providers.DB;
import com.parse.ParseUser;
import com.parse.PushService;

public class LogoutActivity extends Activity {

	protected final String[] PROJECTION = new String[] { DB.UserCourses.ID };

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);

		findViewById(R.id.logout_yes_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						ParseUser user = ParseUser.getCurrentUser();
						courseUnsubscribe(user);
						ParseUser.logOut();
						Intent in = new Intent();
						setResult(2, in);
						LogoutActivity.this.finish();
					}
				});

		findViewById(R.id.logout_no_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LogoutActivity.this.finish();
					}
				});
	}

	public void courseUnsubscribe(ParseUser user) {
		Cursor userCoursesCursor = getContentResolver().query(
				DB.UserCourses.CONTENT_URI, PROJECTION, null, null, null);

		while (userCoursesCursor.moveToNext()) {
			String courseID = userCoursesCursor.getString(0);
			PushService.unsubscribe(this, courseID);
			Log.d("CourseUnsubsribe", courseID);
		}
		userCoursesCursor.close();
		
		getContentResolver().delete(DB.UserCourses.CONTENT_URI, null, null);
	}

}

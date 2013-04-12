package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iuinsider.iunotifier.providers.DB;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.PushService;

public class LogoutActivity extends Activity {

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);
		Parse.initialize(this, IUNotifierApplication.APP_ID,
				IUNotifierApplication.CLIENT_KEY);

		// Listen when Yes button is clicked
		findViewById(R.id.logout_yes_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						courseUnsubscribe();
						ParseUser.logOut();

						Intent in = new Intent();
						setResult(2, in);
						LogoutActivity.this.finish();
					}
				});

		// Listen when No button is clicked
		findViewById(R.id.logout_no_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						LogoutActivity.this.finish();
					}
				});
	}

	// =========================================================================================
	public void courseUnsubscribe() {
		String[] projection = new String[] { DB.UserCourses.ID };
		Cursor userCoursesCursor = getContentResolver().query(
				DB.UserCourses.CONTENT_URI, projection, null, null, null);

		while (userCoursesCursor.moveToNext()) {
			String courseID = userCoursesCursor.getString(0);
			PushService.unsubscribe(this, courseID);

			Log.d("CourseUnsubsribe", courseID);
		}
		userCoursesCursor.close();
		getContentResolver().delete(DB.UserCourses.CONTENT_URI, null, null);
	}
}

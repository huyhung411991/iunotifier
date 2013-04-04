package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import com.iuinsider.iunotifier.providers.DB;
import com.parse.ParseUser;
import com.parse.PushService;

public class LogoutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);
		
		findViewById(R.id.logout_yes_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						ParseUser user = ParseUser.getCurrentUser();
						courseSubscribe(user);
						ParseUser.logOut();
						Intent in = new Intent();
				        setResult(1, in);
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

	public void courseSubscribe(ParseUser user) {
		String[] projection = new String[] { DB.UserCourses.ID };
		Cursor courseCursor = getContentResolver().query(
				DB.UserCourses.CONTENT_URI, projection, null, null, null);

		while (courseCursor.moveToNext()) {
			String courseID = courseCursor.getString(0);
			PushService.unsubscribe(this, courseID);
		}
		
		courseCursor.close();
		getContentResolver().delete(DB.UserCourses.CONTENT_URI, null, null);
		
	}

}

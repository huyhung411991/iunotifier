package com.iuinsider.iunotifier;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.parse.ParseUser;
import com.parse.PushService;

public class LogoutActivity extends Activity {

	private class CourseUnsubscribe extends AsyncTask<ParseUser, Void, Void> {

		protected Cursor userCoursesCursor = null;
		protected Context context = null;
		protected final String[] PROJECTION = new String[] { DB.UserCourses.ID };

		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected Void doInBackground(ParseUser... params) {
			context = LogoutActivity.this;
			ParseUser user = params[0];
			DBRetriever.userCoursesQuery(context, user);

			userCoursesCursor = getContentResolver().query(
					DB.UserCourses.CONTENT_URI, PROJECTION, null, null, null);

			return null;
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Void result) {
			while (userCoursesCursor.moveToNext()) {
				String courseID = userCoursesCursor.getString(0);
				PushService.unsubscribe(context, courseID);
			}
			userCoursesCursor.close();
		}
	}

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
						//new CourseUnsubscribe().execute(user);
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

	public void courseUnsubscribe(ParseUser user) {
		JSONArray courses = user.getJSONArray("courses");
		try {
			for (int index = 0; index < courses.length(); index++) {
				String courseID = courses.getString(index);
				PushService.unsubscribe(this, courseID);
			}
			Log.d("ParsePush", "Unsubscribe to " + courses.length()
					+ " channels");
		} catch (JSONException e) {
			Log.d("ParsePush", "Error: " + e.getMessage());
		}
	}

}

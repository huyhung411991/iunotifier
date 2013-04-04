package com.iuinsider.iunotifier;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
		JSONArray courses = user.getJSONArray("courses");
		try {
			for (int index = 0; index < courses.length(); index++) {
				String courseID = courses.getString(index);
				PushService.unsubscribe(this, courseID);
			}
			Log.d("ParsePush", "Unsubscribe to " + courses.length() + " channels");
		} catch (JSONException e) {
			Log.d("ParsePush", "Error: " + e.getMessage());
		}
	}

}

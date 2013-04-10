package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iuinsider.iunotifier.providers.DB;

public class ResetDataActivity extends Activity {

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetdata);

		findViewById(R.id.resetData_yes_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						getContentResolver().delete(DB.Departments.CONTENT_URI,	null, null);
						getContentResolver().delete(DB.Courses.CONTENT_URI, null, null);
						getContentResolver().delete(DB.Announce.CONTENT_URI, null, null);
						
						Intent in = new Intent();
						setResult(3, in);
						ResetDataActivity.this.finish();
					}
				});

		findViewById(R.id.resetData_no_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						ResetDataActivity.this.finish();
					}
				});
	}
}

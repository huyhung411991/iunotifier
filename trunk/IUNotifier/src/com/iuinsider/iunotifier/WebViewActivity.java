package com.iuinsider.iunotifier;

import com.parse.ParseUser;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private static final String EXTRA_LINK = ".com.iuinsider.iunotifier.LINK";

	private ParseUser currentUser = ParseUser.getCurrentUser();
	private static WebView myWebView = null;
	private static String link = "";

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Set animation when opening or closing activity
		// getWindow().getAttributes().windowAnimations = R.style.Slide;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);

		Intent intent = getIntent();
		link = intent.getStringExtra(EXTRA_LINK);

		myWebView = (WebView) findViewById(R.id.webView);
		myWebView.setWebViewClient(new WebViewClient());
		myWebView.loadUrl(link);

		// Show the Up button in the action bar.
		setupActionBar();
	}

	// =========================================================================================
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.news, menu);

		if (currentUser != null) {
			MenuItem switchButton = menu.findItem(R.id.action_login);
			switchButton.setIcon(R.drawable.sign_in);
		}
		return true;
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		if (myWebView.canGoBack()) {
			myWebView.goBack();
			return;
		}
		super.onBackPressed();
		overridePendingTransition(0, R.anim.slide_out_right);
	}

	// =========================================================================================
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent parentActivityIntent = new Intent(this, MainMenuActivity.class);
			parentActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(parentActivityIntent);
			overridePendingTransition(0, R.anim.slide_out_right);
			finish();
			return true;
		case R.id.action_refresh:
			myWebView.loadUrl(link);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// =========================================================================================
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// =========================================================================================
}

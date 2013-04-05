package com.iuinsider.iunotifier;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashScreenActivity extends Activity {

	/**
	 * The thread to process splash screen events
	 */
	private Thread mSplashThread = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Splash screen orientation (landscape or potrait)
		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_splashscreen_landscape);
		} else {
			setContentView(R.layout.activity_splashscreen_potrait);
		}

		final SplashScreenActivity sPlashScreen = this;

		// The thread to wait for splash screen events
		mSplashThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						// Wait given period of time or exit on touch
						wait(1000);
					}
				} catch (InterruptedException ex) {
				}

				finish();

				// Run next activity
				Intent intent = new Intent();
				intent.setClass(sPlashScreen, MainMenuActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, 0);
			}
		};

		mSplashThread.start();
	}

	// ==================================================================================
	/**
	 * Processes splash screen touch events
	 */
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (mSplashThread) {
				mSplashThread.notifyAll();
			}
		}
		return true;
	}
}
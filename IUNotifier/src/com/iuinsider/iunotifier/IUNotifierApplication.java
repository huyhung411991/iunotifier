package com.iuinsider.iunotifier;

import android.app.Application;

import com.parse.Parse;
import com.parse.PushService;

public class IUNotifierApplication extends Application {

	public static final String APPLICATION_ID = "aOLPYGKDCQbJLA4QuW7vMGYXZUBtsTyXgVtVKpdL";
	public static final String CLIENT_KEY = "ZnwcYv8Om99dwbgdoLwMboLrbQjYBHwF0TDDXslf";

	@Override
	public void onCreate() {
		super.onCreate();

		// Initialize Parse Cloud ID
		Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
		/*
		 * ParseUser.enableAutomaticUser(); ParseACL defaultACL = new
		 * ParseACL(); // Optionally enable public read access while disabling
		 * public write access. // defaultACL.setPublicReadAccess(true);
		 * ParseACL.setDefaultACL(defaultACL, true);
		 */

		PushService.subscribe(this, "news", NewsActivity.class);
		PushService.subscribe(this, "events", EventsActivity.class);
	}
}
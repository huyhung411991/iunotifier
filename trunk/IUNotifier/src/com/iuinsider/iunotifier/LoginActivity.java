package com.iuinsider.iunotifier;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iuinsider.iunotifier.providers.DB;
import com.iuinsider.iunotifier.providers.DBRetriever;
import com.iuinsider.iunotifier.providers.UserPermission;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;

public class LoginActivity extends Activity {

	// Values for user name and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;
	private boolean error;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private Button mSignInButton;
	private Button mCancelButton;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// =========================================================================================
	private class UserLoginTask extends AsyncTask<String, Void, Boolean> {
		/**
		 * The system calls this to perform work in a worker thread and delivers
		 * it the parameters given to AsyncTask.execute()
		 */
		@Override
		protected Boolean doInBackground(String... params) {
			String username = params[0];
			String password = params[1];
			ParseUser user = null;

			try {
				user = ParseUser.logIn(username, password);
			} catch (ParseException e) {
				return false;
			}

			if (user != null) {
				// Login successful
				String userRole = user.getString(UserPermission.USER_COLUMN);
				if (UserPermission.hasUserCourses(userRole))
					DBRetriever.userCoursesQuery(LoginActivity.this, user);
				if (UserPermission.hasCourseSubscribe(userRole))
					courseSubscribe();

				return true;
			} else {
				return false;
			}
		}

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Intent in = new Intent();
				setResult(1, in);
				finish();
			} else {
				// Login failed.
				error = true;
				mPasswordView
						.setError(getString(R.string.login_incorrectPassword));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Parse.initialize(this, IUNotifierApplication.APP_ID,
				IUNotifierApplication.CLIENT_KEY);

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.login_username_editText);
		mPasswordView = (EditText) findViewById(R.id.login_password_editText);
		mLoginFormView = findViewById(R.id.login_form_tableLayout);
		mLoginStatusView = findViewById(R.id.login_status_tableLayout);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_textView);
		mSignInButton = (Button) findViewById(R.id.login_signIn_button);
		mCancelButton = (Button) findViewById(R.id.login_cancel_button);

		mUsernameView.setText(mUsername);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});
		mSignInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginActivity.this.finish();
			}
		});
	}

	// =========================================================================================
	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid user name, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		error = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.login_fieldRequired));
			focusView = mPasswordView;
			error = true;
		}

		// Check for a valid user name address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.login_fieldRequired));
			focusView = mUsernameView;
			error = true;
		} else if (mUsername.contains(" ")) {
			mUsernameView.setError(getString(R.string.login_invalidUsername));
			focusView = mUsernameView;
			error = true;
		}

		if (error) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progressSigningIn);
			showProgress(true);

			mAuthTask = new UserLoginTask();
			mAuthTask.execute(mUsername, mPassword);
			
//			try {
//				mAuthTask.get();
//			} catch (Exception e) {
//				Log.d("Login", "Error: " + e.getMessage());
//			}
		}
	}

	// =========================================================================================
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	// =========================================================================================
	public void courseSubscribe() {
		String[] projection = new String[] { DB.UserCourses.ID };
		Cursor userCoursesCursor = getContentResolver().query(
				DB.UserCourses.CONTENT_URI, projection, null, null, null);

		while (userCoursesCursor.moveToNext()) {
			String courseID = userCoursesCursor.getString(0);
			PushService.subscribe(this, courseID, AnnouncementsActivity.class);

			Log.d("CourseSubsribe", courseID);
		}
		userCoursesCursor.close();
	}
}
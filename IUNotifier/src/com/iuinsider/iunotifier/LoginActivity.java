package com.iuinsider.iunotifier;

import org.json.JSONArray;
import org.json.JSONException;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
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

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.login_username_editText);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.login_password_editText);
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

		mLoginFormView = findViewById(R.id.login_form_tableLayout);
		mLoginStatusView = findViewById(R.id.login_status_tableLayout);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_textView);

		findViewById(R.id.login_signIn_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		findViewById(R.id.login_cancel_button).setOnClickListener(
				new View.OnClickListener() {
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

			// /////////////
			// Parse code //
			// /////////////
			ParseUser.logInInBackground(mUsername, mPassword,
					new LogInCallback() {
						@Override
						public void done(ParseUser user, ParseException e) {
							if (e == null && user != null) {
								// Login successful
								// if (user.getString(
								// DB.UserPermission.USER_COLUMN).equals(
								// DB.UserPermission.USER_STUDENT))
								courseSubscribe(user);
								Intent in = new Intent(); // Intent in =
															// getIntent();
								setResult(1, in);
								LoginActivity.this.finish();
							} else {
								// Login failed.
								showProgress(false);
								mPasswordView
										.setError(getString(R.string.login_incorrectPassword));
								mPasswordView.requestFocus();
								error = true;
							}
						}
					});
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

	public void courseSubscribe(ParseUser user) {
		JSONArray courses = user.getJSONArray("courses");
		try {
			for (int index = 0; index < courses.length(); index++) {
				String courseID = courses.getString(index);
				PushService.subscribe(this, courseID, MainMenuActivity.class);
			}
			Log.d("ParsePush", "Subscribe to " + courses.length() + " channels");
		} catch (JSONException e) {
			Log.d("ParsePush", "Error: " + e.getMessage());
		}
	}
}

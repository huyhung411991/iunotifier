/**
 * 
 */
package com.iuinsider.iunotifier.test;

import java.lang.reflect.Field;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;

import com.iuinsider.iunotifier.LoginActivity;
import com.iuinsider.iunotifier.R;

/**
 * @author HuyHung
 * 
 * In order for the test to work, enable the code from line 208 to 212 in LoginActivity.java
 * 
 */
public class LoginActivityTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	boolean error;
	EditText mUserNameView;
	EditText mPasswordView;
	Button mSignInButton;

	public LoginActivityTest() {
		super(LoginActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		mUserNameView = (EditText) getActivity().findViewById(
				R.id.login_username_editText);
		mPasswordView = (EditText) getActivity().findViewById(
				R.id.login_password_editText);
		mSignInButton = (Button) getActivity().findViewById(
				R.id.login_signIn_button);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SmallTest
	public void testCase1() {// --> Success
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mUserNameView.setText("itiu09008");
				mPasswordView.setText("12345");
				mSignInButton.performClick();
			}
		});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		error = this.retrieveHiddenMember("error", error, getActivity());
		assertFalse("Login successful", error);
	}

	public void testCase2() {// --> Fail
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mUserNameView.setText("abcd");
				mPasswordView.setText("12345");
				mSignInButton.performClick();
			}
		});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		error = this.retrieveHiddenMember("error", error, getActivity());
		assertTrue("Login Failed", error);
	}

	public void testCase3() {// --> Fail
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mUserNameView.setText("itiu09008");
				mPasswordView.setText("aaaa");
				mSignInButton.performClick();
			}
		});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		error = this.retrieveHiddenMember("error", error, getActivity());
		assertTrue("Login Failed", error);
	}

	public void testCase4() {// --> Fail
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				mUserNameView.setText("abcd");
				mPasswordView.setText("aaaa");
				mSignInButton.performClick();
			}
		});
		// wait for the request to go through
		getInstrumentation().waitForIdleSync();
		error = this.retrieveHiddenMember("error", error, getActivity());
		assertTrue("Login Failed", error);
	}

	@SuppressWarnings("unchecked")
	public <T> T retrieveHiddenMember(String memberName, T type,
			Object sourceObj) {
		Field field = null;
		T returnVal = null;
		// Test for proper existence
		try {
			field = sourceObj.getClass().getDeclaredField(memberName);
		} catch (NoSuchFieldException e) {
			fail("The field \""
					+ memberName
					+ "\" was renamed or removed. Do not rename or remove this member variable.");
		}
		field.setAccessible(true);

		// Test for proper type
		try {
			returnVal = (T) field.get(sourceObj);
		} catch (ClassCastException exc) {
			fail("The field \""
					+ memberName
					+ "\" had its type changed. Do not change the type on this member variable.");
		}

		// Boiler Plate Exception Checking. If any of these Exceptions are
		// throw it was because this method was called improperly.
		catch (IllegalArgumentException e) {
			fail("This is an Error caused by the UnitTest!\n Improper user of retrieveHiddenMember(...) -- IllegalArgumentException:\n Passed in the wrong object to Field.get(...)");
		} catch (IllegalAccessException e) {
			fail("This is an Error caused by the UnitTest!\n Improper user of retrieveHiddenMember(...) -- IllegalAccessException:\n Field.setAccessible(true) should be called.");
		}
		return returnVal;
	}
}

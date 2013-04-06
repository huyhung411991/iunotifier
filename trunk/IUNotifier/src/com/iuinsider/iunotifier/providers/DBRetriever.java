package com.iuinsider.iunotifier.providers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DBRetriever {
	private static Context context = null;
	private static String[] formats = new String[] {
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss.SSS",
			"yyyy-MM-dd" };

	public static String DateToString(Date date, int format) {
		if (date == null || format > formats.length || format < 0)
			return "";

		SimpleDateFormat sdf = new SimpleDateFormat(formats[format], Locale.US);
		String string = sdf.format(date);

		return string;
	}

	public static Date StringToDate(String string, int format) {
		if (TextUtils.isEmpty(string) || format > formats.length || format < 0)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(formats[format], Locale.US);
		Date date = null;
		try {
			date = sdf.parse(string);
		} catch (java.text.ParseException e) {
			Log.e("eventDetails", e.toString());
		}

		return date;
	}

	// --------------------------------------------------------------------------------
	public static String getLastUpdate(Context c, Uri uri,
			String projectedColumn, String selectedColumn, String selectedValue) {
		if (c == null || uri == null || TextUtils.isEmpty(projectedColumn))
			return null;

		Cursor cursor = null;
		if (TextUtils.isEmpty(selectedValue) || selectedValue.equals("ALL"))
			cursor = c.getContentResolver().query(uri,
					new String[] { "MAX(" + projectedColumn + ")" }, null,
					null, null);
		else
			cursor = c.getContentResolver().query(uri,
					new String[] { "MAX(" + projectedColumn + ")" },
					"(" + selectedColumn + " = '" + selectedValue + "')", null,
					null);

		String lastUpdate = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			lastUpdate = cursor.getString(0);
		}
		cursor.close();
		return lastUpdate;
	}

	// --------------------------------------------------------------------------------
	public static void allNewsQuery(Context c, String sortCondition) {
		context = c;

		// Clear local database table
		context.getContentResolver().delete(DB.News.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.News.TABLE_NAME);

		if (!sortCondition.isEmpty()) {
			query.whereEqualTo(DB.News.SOURCE, sortCondition);
		}
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues news = new ContentValues();
						news.put(DB.News.TITLE,
								parseObject.getString(DB.News.TITLE));
						news.put(DB.News.LINK,
								parseObject.getString(DB.News.LINK));
						news.put(DB.News.SOURCE,
								parseObject.getString(DB.News.SOURCE));

						Date date = parseObject.getCreatedAt();
						news.put(DB.News.CREATED_AT, DateToString(date, 2));
						context.getContentResolver().insert(
								DB.News.CONTENT_URI, news);
					}

					Log.d(DB.News.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.News.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	// --------------------------------------------------------------------------------
	public static void allEventsQuery(Context c, String sortCondition) {
		context = c;

		// Clear local database table
		context.getContentResolver().delete(DB.Events.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.Events.TABLE_NAME);

		if (!TextUtils.isEmpty(sortCondition)) {
			if (sortCondition.equals("Today")) {
				long offset = System.currentTimeMillis();
				long start = (offset / 86400000l) * 86400000l;
				long end = start + 86340000;

				query.whereGreaterThanOrEqualTo("eventDate", new Date(start));
				query.whereLessThanOrEqualTo("eventDate", new Date(end));
			} else if (sortCondition.equals("Upcoming")) {
				query.whereGreaterThan("eventDate", new Date());
			}
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues event = new ContentValues();
						event.put(DB.Events.TITLE,
								parseObject.getString(DB.Events.TITLE));
						event.put(DB.Events.DESCRIPTION,
								parseObject.getString(DB.Events.DESCRIPTION));
						event.put(DB.Events.PLACE,
								parseObject.getString(DB.Events.PLACE));

						Date date = parseObject.getDate(DB.Events.DATE);
						event.put(DB.Events.DATE, DateToString(date, 1));

						date = parseObject.getCreatedAt();
						event.put(DB.Events.CREATED_AT, "Created on: "
								+ DateToString(date, 2));
						context.getContentResolver().insert(
								DB.Events.CONTENT_URI, event);
					}

					Log.d(DB.Events.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Events.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	// --------------------------------------------------------------------------------
	public static void departmentsQuery(Context c, boolean reloadAll) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Departments.TABLE_NAME);

		if (reloadAll) {
			// Clear local database table
			context.getContentResolver().delete(DB.Departments.CONTENT_URI,
					null, null);
		} else {
			String lastUpdate = getLastUpdate(context,
					DB.Departments.CONTENT_URI, DB.Departments.UPDATED_AT,
					null, null);
			if (!TextUtils.isEmpty(lastUpdate)) {
				Date date = StringToDate(lastUpdate, 1);
				if (date != null)
					query.whereGreaterThan(DB.Departments.UPDATED_AT, date);
			}
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues department = new ContentValues();
						department.put(DB.Departments.ID,
								parseObject.getString(DB.Departments.ID));
						department.put(DB.Departments.NAME,
								parseObject.getString(DB.Departments.NAME));
						Date date = parseObject.getUpdatedAt();
						department.put(DB.Departments.UPDATED_AT,
								DateToString(date, 1));
						context.getContentResolver().insert(
								DB.Departments.CONTENT_URI, department);
					}

					Log.d(DB.Departments.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Departments.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	// --------------------------------------------------------------------------------
	public static void coursesQuery(Context c, String departmentID,
			boolean reloadAll) {
		context = c;

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("departmentID", departmentID);

		if (reloadAll) {
			// Clear local database table
			context.getContentResolver().delete(DB.Courses.CONTENT_URI, null,
					null);
		} else {
			String lastUpdate = getLastUpdate(context, DB.Courses.CONTENT_URI,
					DB.Courses.UPDATED_AT, DB.Courses.DEPARTMENT_ID,
					departmentID);
			params.put("lastUpdate", lastUpdate);
		}

		ParseCloud.callFunctionInBackground("getCourses", params,
				new FunctionCallback<JSONArray>() {
					@Override
					public void done(JSONArray list, ParseException e) {
						if (e == null) {
							for (int index = 0; index < list.length(); index++) {
								JSONObject object = null;
								try {
									object = list.getJSONObject(index);
									ContentValues course = new ContentValues();
									course.put(DB.Courses.ID,
											object.getString(DB.Courses.ID));
									course.put(DB.Courses.NAME,
											object.getString(DB.Courses.NAME));
									course.put(
											DB.Courses.DEPARTMENT_ID,
											object.getString(DB.Courses.DEPARTMENT_ID));
									String string = object.getJSONObject(
											DB.Courses.UPDATED_AT).getString(
											"iso");
									Date date = StringToDate(string, 0);
									course.put(DB.Courses.UPDATED_AT,
											DateToString(date, 1));
									context.getContentResolver().insert(
											DB.Courses.CONTENT_URI, course);
								} catch (JSONException e1) {
									Log.d(DB.Courses.TABLE_NAME,
											"Error: " + e1.getMessage());
								}

							}
							Log.d(DB.Courses.TABLE_NAME,
									"Retrieved " + list.length() + " items");
						} else {
							Log.d(DB.Courses.TABLE_NAME,
									"Error: " + e.getMessage());
						}
					}
				});
	}

	// --------------------------------------------------------------------------------
	public static void courseDetailsQuery(Context c, String courseID,
			boolean reloadAll) {
		if (TextUtils.isEmpty(courseID) || courseID == null)
			return;

		context = c;

		// Query on Courses table, not CourseDetails
		ParseQuery query = new ParseQuery(DB.Courses.TABLE_NAME);

		query.whereEqualTo(DB.CourseDetails.ID, courseID);

		if (reloadAll) {
			// Clear local database course row
			Uri uri = Uri.withAppendedPath(DB.Courses.CONTENT_URI, courseID);
			context.getContentResolver().delete(uri, null, null);
		} else {
			String lastUpdate = getLastUpdate(context,
					DB.CourseDetails.CONTENT_URI, DB.CourseDetails.UPDATED_AT,
					DB.CourseDetails.ID, courseID);
			if (!TextUtils.isEmpty(lastUpdate)) {
				Date date = StringToDate(lastUpdate, 1);
				if (date != null)
					query.whereGreaterThan(DB.CourseDetails.UPDATED_AT, date);
			}
		}

		try {
			List<ParseObject> list = query.find();
			ListIterator<ParseObject> li = list.listIterator();
			while (li.hasNext()) {
				ParseObject parseObject = li.next();
				ContentValues courseDetails = new ContentValues();
				courseDetails.put(DB.CourseDetails.ID,
						parseObject.getString(DB.CourseDetails.ID));
				courseDetails.put(DB.CourseDetails.NAME,
						parseObject.getString(DB.CourseDetails.NAME));
				courseDetails.put(DB.CourseDetails.LECTURER,
						parseObject.getString(DB.CourseDetails.LECTURER));
				courseDetails.put(DB.CourseDetails.THEORY,
						parseObject.getString(DB.CourseDetails.THEORY));
				courseDetails.put(DB.CourseDetails.LAB,
						parseObject.getString(DB.CourseDetails.LAB));
				courseDetails.put(DB.CourseDetails.CREDIT,
						parseObject.getLong(DB.CourseDetails.CREDIT));
				courseDetails.put(DB.CourseDetails.PREREQUISITE,
						parseObject.getString(DB.CourseDetails.PREREQUISITE));
				Date date = parseObject.getUpdatedAt();
				courseDetails.put(DB.CourseDetails.UPDATED_AT,
						DateToString(date, 1));
				context.getContentResolver().insert(
						DB.CourseDetails.CONTENT_URI, courseDetails);
			}

			Log.d(DB.CourseDetails.TABLE_NAME, "Retrieved " + list.size()
					+ " items");
		} catch (ParseException e) {
			Log.d(DB.CourseDetails.TABLE_NAME, "Error: " + e.getMessage());
		}
	}

	// --------------------------------------------------------------------------------
	public static void announcementsQuery(Context c, String courseID,
			boolean reloadAll) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Announce.TABLE_NAME);

		if (!TextUtils.isEmpty(courseID) && !courseID.equals("ALL"))
			query.whereEqualTo(DB.Announce.COURSE_ID, courseID);

		if (reloadAll) {
			// Clear local database table
			context.getContentResolver().delete(DB.Announce.CONTENT_URI, null,
					null);
		} else {
			String lastUpdate = getLastUpdate(context, DB.Announce.CONTENT_URI,
					DB.Announce.UPDATED_AT, DB.Announce.COURSE_ID, courseID);
			if (!TextUtils.isEmpty(lastUpdate)) {
				Date date = StringToDate(lastUpdate, 1);
				if (date != null)
					query.whereGreaterThan(DB.Announce.UPDATED_AT, date);
			}
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues announce = new ContentValues();
						announce.put(DB.Announce.ID,
								parseObject.getString(DB.Announce.ID));
						announce.put(DB.Announce.COURSE_ID,
								parseObject.getString(DB.Announce.COURSE_ID));
						announce.put(DB.Announce.MESSAGE,
								parseObject.getString(DB.Announce.MESSAGE));
						Date date = parseObject.getUpdatedAt();
						announce.put(DB.Announce.UPDATED_AT,
								DateToString(date, 1));
						context.getContentResolver().insert(
								DB.Announce.CONTENT_URI, announce);
					}

					Log.d(DB.Announce.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Announce.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	// --------------------------------------------------------------------------------
	public static void userCoursesQuery(Context c, ParseUser parseUser) {
		context = c;

		if (parseUser == null)
			return;

		// Clear local database table
		context.getContentResolver().delete(DB.UserCourses.CONTENT_URI, null,
				null);

		try {
			JSONArray list = ParseCloud.callFunction("getUserCourses", null);
			for (int index = 0; index < list.length(); index++) {
				JSONObject object = null;
				try {
					object = list.getJSONObject(index);
					ContentValues course = new ContentValues();
					course.put(DB.UserCourses.ID,
							object.getString(DB.UserCourses.ID));
					course.put(DB.UserCourses.NAME,
							object.getString(DB.UserCourses.NAME));
					context.getContentResolver().insert(
							DB.UserCourses.CONTENT_URI, course);
				} catch (JSONException e1) {
					Log.d(DB.UserCourses.TABLE_NAME,
							"Error: " + e1.getMessage());
				}

			}
			Log.d(DB.UserCourses.TABLE_NAME, "Retrieved " + list.length()
					+ " items");
		} catch (ParseException e) {
			Log.d(DB.UserCourses.TABLE_NAME, "Error: " + e.getMessage());
		}
	}

	// --------------------------------------------------------------------------------
	public static void pushAnnouncement(String courseID, String message,
			TextView state) {
		final TextView stateMessage = state;

		HashMap<String, Object> params = new HashMap<String, Object>();

		params.put("courseid", courseID);
		params.put("message", message);
		ParseCloud.callFunctionInBackground("pushAnnouncement", params,
				new FunctionCallback<String>() {
					public void done(String returnMsg, ParseException e) {
						if (e == null) {
							stateMessage.setText(returnMsg);
						} else {
							Log.d(DB.Announce.TABLE_NAME,
									"Error: " + e.getMessage());
						}
					}
				});
	}
}

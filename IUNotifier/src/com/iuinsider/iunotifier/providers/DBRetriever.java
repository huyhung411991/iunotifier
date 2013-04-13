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
import android.view.Gravity;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class DBRetriever {
	private static Context context = null;
	private static final String[] formats = new String[] {
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd HH:mm:ss.SSS",
			"yyyy-MM-dd" };
	private static final int MILLISECONDS_OF_DAY = 24 * 60 * 60 * 1000;
	private static final int MILLISECONDS_OF_DAY_2 = (23 * 60 + 59) * 60 * 1000;

	// --------------------------------------------------------------------------------
	public static String DateToString(Date date, int format) {
		if (date == null || format > formats.length || format < 0)
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat(formats[format], Locale.US);
		String string = sdf.format(date);

		return string;
	}

	// --------------------------------------------------------------------------------
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

		String[] projection = new String[] { "MAX(" + projectedColumn + ")" };

		String selection = "";
		if (!TextUtils.isEmpty(selectedValue) && !selectedValue.equals("ALL"))
			selection = "(" + selectedColumn + " = '" + selectedValue + "')";

		Cursor cursor = c.getContentResolver().query(uri, projection,
				selection, null, null);

		String lastUpdate = null;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			lastUpdate = cursor.getString(0);
		}
		cursor.close();
		return lastUpdate;
	}

	// --------------------------------------------------------------------------------
	// Run on background task
	public static int newsQuery(Context c, String sortCondition) {
		if (c == null)
			return -1;
		context = c;

		// Clear local database table
		context.getContentResolver().delete(DB.News.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.News.TABLE_NAME);
		if (!TextUtils.isEmpty(sortCondition)) {
			query.whereEqualTo(DB.News.SOURCE, sortCondition);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues news = getNewsValues(parseObject);
						context.getContentResolver().insert(
								DB.News.CONTENT_URI, news);
					}

					Log.d(DB.News.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.e(DB.News.TABLE_NAME, "Error: " + e.getMessage());
				}
			}

			private ContentValues getNewsValues(ParseObject parseObject) {
				ContentValues news = new ContentValues();
				news.put(DB.News.ID, parseObject.getString(DB.News.ID));
				news.put(DB.News.TITLE, parseObject.getString(DB.News.TITLE));
				news.put(DB.News.LINK, parseObject.getString(DB.News.LINK));
				news.put(DB.News.SOURCE, parseObject.getString(DB.News.SOURCE));
				Date date = parseObject.getUpdatedAt();
				news.put(DB.News.UPDATED_AT, DateToString(date, 2));
				return news;
			}
		});
		return 0;
	}

	// --------------------------------------------------------------------------------
	// Run on background thread
	public static int eventsQuery(Context c, String sortCondition) {
		if (c == null)
			return -1;
		context = c;

		// Clear local database table
		context.getContentResolver().delete(DB.Events.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.Events.TABLE_NAME);
		if (!TextUtils.isEmpty(sortCondition)) {
			if (sortCondition.equals("Today")) {
				long offset = System.currentTimeMillis();
				long start = (offset / MILLISECONDS_OF_DAY)
						* MILLISECONDS_OF_DAY;
				long end = start + MILLISECONDS_OF_DAY_2;

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
						ContentValues event = getEventValues(parseObject);
						context.getContentResolver().insert(
								DB.Events.CONTENT_URI, event);
					}

					Log.d(DB.Events.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.e(DB.Events.TABLE_NAME, "Error: " + e.getMessage());
				}
			}

			private ContentValues getEventValues(ParseObject parseObject) {
				ContentValues event = new ContentValues();
				event.put(DB.Events.ID, parseObject.getString(DB.Events.ID));
				event.put(DB.Events.TITLE,
						parseObject.getString(DB.Events.TITLE));
				event.put(DB.Events.DESCRIPTION,
						parseObject.getString(DB.Events.DESCRIPTION));
				event.put(DB.Events.PLACE,
						parseObject.getString(DB.Events.PLACE));
				Date date = parseObject.getDate(DB.Events.DATE);
				event.put(DB.Events.DATE, DateToString(date, 1));
				date = parseObject.getUpdatedAt();
				event.put(DB.Events.UPDATED_AT,
						"Created on: " + DateToString(date, 2));
				return event;
			}
		});
		return 0;
	}

	// --------------------------------------------------------------------------------
	// Run on background thread
	public static int departmentsQuery(Context c, boolean reloadAll) {
		if (c == null)
			return -1;
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
						ContentValues department = getDepartmentValues(parseObject);
						context.getContentResolver().insert(
								DB.Departments.CONTENT_URI, department);
					}

					Log.d(DB.Departments.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.e(DB.Departments.TABLE_NAME, "Error: " + e.getMessage());
				}
			}

			private ContentValues getDepartmentValues(ParseObject parseObject) {
				ContentValues department = new ContentValues();
				department.put(DB.Departments.ID,
						parseObject.getString(DB.Departments.ID));
				department.put(DB.Departments.NAME,
						parseObject.getString(DB.Departments.NAME));
				Date date = parseObject.getUpdatedAt();
				department
						.put(DB.Departments.UPDATED_AT, DateToString(date, 1));
				return department;
			}
		});
		return 0;
	}

	// --------------------------------------------------------------------------------
	// Run on background thread
	public static int coursesQuery(Context c, String departmentID,
			boolean reloadAll) {
		if (c == null || TextUtils.isEmpty(departmentID))
			return -1;
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
									ContentValues course = getCourseValues(object);
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
							Log.e(DB.Courses.TABLE_NAME,
									"Error: " + e.getMessage());
						}
					}

					private ContentValues getCourseValues(JSONObject object)
							throws JSONException {
						ContentValues course = new ContentValues();
						course.put(DB.Courses.ID,
								object.getString(DB.Courses.ID));
						course.put(DB.Courses.NAME,
								object.getString(DB.Courses.NAME));
						course.put(DB.Courses.DEPARTMENT_ID,
								object.getString(DB.Courses.DEPARTMENT_ID));
						String string = object.getJSONObject(
								DB.Courses.UPDATED_AT).getString("iso");
						Date date = StringToDate(string, 0);
						course.put(DB.Courses.UPDATED_AT, DateToString(date, 1));
						return course;
					}
				});
		return 0;
	}

	// --------------------------------------------------------------------------------
	// Run on calling thread
	public static int courseDetailsQuery(Context c, String courseID,
			boolean reloadAll) {
		if (c == null || TextUtils.isEmpty(courseID))
			return -1;
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
				ContentValues courseDetails = getCourseDetailsValues(parseObject);
				context.getContentResolver().insert(
						DB.CourseDetails.CONTENT_URI, courseDetails);
			}

			Log.d(DB.CourseDetails.TABLE_NAME, "Retrieved " + list.size()
					+ " items");
		} catch (ParseException e) {
			Log.e(DB.CourseDetails.TABLE_NAME, "Error: " + e.getMessage());
		}
		return 0;
	}

	private static ContentValues getCourseDetailsValues(ParseObject parseObject) {
		ContentValues courseDetails = new ContentValues();
		courseDetails.put(DB.CourseDetails.ID,
				parseObject.getString(DB.CourseDetails.ID));
		courseDetails.put(DB.CourseDetails.NAME,
				parseObject.getString(DB.CourseDetails.NAME));
		courseDetails.put(DB.CourseDetails.LECTURER,
				parseObject.getString(DB.CourseDetails.LECTURER));
		JSONArray theoryList = parseObject
				.getJSONArray(DB.CourseDetails.THEORY);
		JSONArray labList = parseObject.getJSONArray(DB.CourseDetails.LAB);
		String theory = "";
		String lab = "";
		if (theoryList != null) {
			for (int index = 0; index < theoryList.length(); index++) {
				try {
					String t = theoryList.getString(index);
					theory += t;
					if (index < theoryList.length() - 1)
						theory += "\n";
				} catch (JSONException e1) {
					Log.e(DB.CourseDetails.TABLE_NAME,
							"Error: " + e1.getMessage());
				}
			}
		}
		if (labList != null) {
			for (int index = 0; index < labList.length(); index++) {
				try {
					String l = labList.getString(index);
					lab += l;
					if (index < labList.length() - 1)
						lab += "\n";
				} catch (JSONException e1) {
					Log.e(DB.CourseDetails.TABLE_NAME,
							"Error: " + e1.getMessage());
				}
			}
		}
		courseDetails.put(DB.CourseDetails.THEORY, theory);
		courseDetails.put(DB.CourseDetails.LAB, lab);
		courseDetails.put(DB.CourseDetails.CREDIT,
				parseObject.getLong(DB.CourseDetails.CREDIT));
		courseDetails.put(DB.CourseDetails.PREREQUISITE,
				parseObject.getString(DB.CourseDetails.PREREQUISITE));
		Date date = parseObject.getUpdatedAt();
		courseDetails.put(DB.CourseDetails.UPDATED_AT, DateToString(date, 1));
		return courseDetails;
	}

	// --------------------------------------------------------------------------------
	// Run on background thread
	public static int announcementsQuery(Context c, String courseID,
			boolean reloadAll) {
		if (c == null || TextUtils.isEmpty(courseID))
			return -1;
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
						ContentValues announce = getAnnouncementValues(parseObject);
						context.getContentResolver().insert(
								DB.Announce.CONTENT_URI, announce);
					}

					Log.d(DB.Announce.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.e(DB.Announce.TABLE_NAME, "Error: " + e.getMessage());
				}
			}

			private ContentValues getAnnouncementValues(ParseObject parseObject) {
				ContentValues announce = new ContentValues();
				announce.put(DB.Announce.ID,
						parseObject.getString(DB.Announce.ID));
				announce.put(DB.Announce.COURSE_ID,
						parseObject.getString(DB.Announce.COURSE_ID));
				announce.put(DB.Announce.MESSAGE,
						parseObject.getString(DB.Announce.MESSAGE));

				Date date = parseObject.getUpdatedAt();
				announce.put(DB.Announce.UPDATED_AT, DateToString(date, 1));
				announce.put(DB.Announce.CREATED_AT, DateToString(date, 2));
				return announce;
			}
		});
		return 0;
	}

	// --------------------------------------------------------------------------------
	// Run on calling thread
	public static int userCoursesQuery(Context c, ParseUser parseUser) {
		if (c == null || parseUser == null)
			return -1;
		context = c;

		// Clear local database table
		context.getContentResolver().delete(DB.UserCourses.CONTENT_URI, null,
				null);

		try {
			JSONArray list = ParseCloud.callFunction("getUserCourses", null);
			for (int index = 0; index < list.length(); index++) {
				JSONObject object = null;
				try {
					object = list.getJSONObject(index);
					ContentValues course = getUserCourseValues(object);
					context.getContentResolver().insert(
							DB.UserCourses.CONTENT_URI, course);
				} catch (JSONException e1) {
					Log.e(DB.UserCourses.TABLE_NAME,
							"Error: " + e1.getMessage());
				}

			}
			Log.d(DB.UserCourses.TABLE_NAME, "Retrieved " + list.length()
					+ " items");
		} catch (ParseException e) {
			Log.e(DB.UserCourses.TABLE_NAME, "Error: " + e.getMessage());
		}
		return 0;
	}

	private static ContentValues getUserCourseValues(JSONObject object)
			throws JSONException {
		ContentValues course = new ContentValues();
		course.put(DB.UserCourses.ID, object.getString(DB.UserCourses.ID));
		course.put(DB.UserCourses.NAME, object.getString(DB.UserCourses.NAME));
		return course;
	}

	// --------------------------------------------------------------------------------
	// Run on background thread
	public static int pushAnnouncement(Context c, String courseID,
			String message) {
		if (TextUtils.isEmpty(courseID) || TextUtils.isEmpty(message)
				|| c == null)
			return -1;
		context = c;

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("courseid", courseID);
		params.put("message", message);

		ParseCloud.callFunctionInBackground("pushAnnouncement", params,
				new FunctionCallback<String>() {
					public void done(String returnMsg, ParseException e) {
						if (e == null) {
							Toast toast = Toast.makeText(context, returnMsg,
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER_VERTICAL
									| Gravity.CENTER_HORIZONTAL, 0, 0);
							toast.show();
						} else {
							Toast toast = Toast.makeText(context,
									"Something goes wrong. Please try again!",
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER_VERTICAL
									| Gravity.CENTER_HORIZONTAL, 0, 0);
							toast.show();
							Log.e(DB.Announce.TABLE_NAME,
									"Error: " + e.getMessage());
						}
					}
				});
		return 0;
	}
}

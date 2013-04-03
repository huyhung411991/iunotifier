package com.iuinsider.iunotifier.providers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

////////////////////////////////////////////////////////////////////////////
public class DBRetriever {
	private static Context context = null;

	public static String DateToString(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
				Locale.US);
		String string = sdf.format(date);

		return string;
	}

	public static String DateToString2(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		String string = sdf2.format(date);

		return string;
	}

	public static Date StringToDate(String string) {
		if (TextUtils.isEmpty(string))
			return null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
				Locale.US);
		Date date = null;
		try {
			date = sdf.parse(string);
		} catch (java.text.ParseException e) {
			Log.e("eventDetails", e.toString());
		}

		return date;
	}

	// //////////////////////////////////////////////////////////////////////////
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

	// //////////////////////////////////////////////////////////////////////////
	public static void allNewsQuery(Context c) {
		context = c;

		// Clear local database table
		context.getContentResolver().delete(DB.News.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.News.TABLE_NAME);

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
						news.put(DB.News.CREATED_AT, DateToString2(date));
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

	// //////////////////////////////////////////////////////////////////////////
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
						event.put(DB.Events.DATE, DateToString(date));

						date = parseObject.getCreatedAt();
						event.put(DB.Events.CREATED_AT, "Created on: "
								+ DateToString2(date));
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

	// //////////////////////////////////////////////////////////////////////////
	public static void departmentsQuery(Context c) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Departments.TABLE_NAME);
		String lastUpdate = getLastUpdate(context, DB.Departments.CONTENT_URI,
				DB.Departments.UPDATED_AT, null, null);

		if (!TextUtils.isEmpty(lastUpdate)) {
			Date date = StringToDate(lastUpdate);
			if (date != null)
				query.whereGreaterThan(DB.Departments.UPDATED_AT, date);
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
								DateToString(date));
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

	// //////////////////////////////////////////////////////////////////////////
	public static void coursesQuery(Context c, String departmentID) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Courses.TABLE_NAME);
		String lastUpdate = getLastUpdate(context, DB.Courses.CONTENT_URI,
				DB.Courses.UPDATED_AT, DB.Courses.DEPARTMENT_ID, departmentID);

		if (!TextUtils.isEmpty(departmentID) && !departmentID.equals("ALL"))
			query.whereEqualTo(DB.Courses.DEPARTMENT_ID, departmentID);

		if (!TextUtils.isEmpty(lastUpdate)) {
			Date date = StringToDate(lastUpdate);
			if (date != null)
				query.whereGreaterThan(DB.Courses.UPDATED_AT, date);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues course = new ContentValues();
						course.put(DB.Courses.ID,
								parseObject.getString(DB.Courses.ID));
						course.put(DB.Courses.NAME,
								parseObject.getString(DB.Courses.NAME));
						course.put(DB.Courses.DEPARTMENT_ID,
								parseObject.getString(DB.Courses.DEPARTMENT_ID));
						Date date = parseObject.getUpdatedAt();
						course.put(DB.Courses.UPDATED_AT, DateToString(date));
						context.getContentResolver().insert(
								DB.Courses.CONTENT_URI, course);
					}

					Log.d(DB.Courses.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Courses.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	// //////////////////////////////////////////////////////////////////////////
	public static void courseDetailsQuery(Context c, String courseID) {
		if (TextUtils.isEmpty(courseID) || courseID == null)
			return;

		context = c;

		ParseQuery query = new ParseQuery(DB.CourseDetails.TABLE_NAME);
		String lastUpdate = getLastUpdate(context,
				DB.CourseDetails.CONTENT_URI, DB.CourseDetails.UPDATED_AT,
				DB.CourseDetails.ID, courseID);

		query.whereEqualTo(DB.CourseDetails.ID, courseID);
		if (!TextUtils.isEmpty(lastUpdate)) {
			Date date = StringToDate(lastUpdate);
			if (date != null)
				query.whereGreaterThan(DB.CourseDetails.UPDATED_AT, date);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues courseDetails = new ContentValues();
						courseDetails.put(DB.CourseDetails.ID,
								parseObject.getString(DB.CourseDetails.ID));
						courseDetails.put(DB.CourseDetails.NAME,
								parseObject.getString(DB.CourseDetails.NAME));
						courseDetails.put(DB.CourseDetails.LECTURER,
								parseObject
										.getString(DB.CourseDetails.LECTURER));
						courseDetails.put(DB.CourseDetails.THEORY,
								parseObject.getString(DB.CourseDetails.THEORY));
						courseDetails.put(DB.CourseDetails.LAB,
								parseObject.getString(DB.CourseDetails.LAB));
						courseDetails.put(DB.CourseDetails.CREDIT,
								parseObject.getLong(DB.CourseDetails.CREDIT));
						courseDetails.put(
								DB.CourseDetails.PREREQUISITE,
								parseObject
										.getString(DB.CourseDetails.PREREQUISITE));
						Date date = parseObject.getUpdatedAt();
						courseDetails.put(DB.CourseDetails.UPDATED_AT,
								DateToString(date));
						context.getContentResolver().insert(
								DB.CourseDetails.CONTENT_URI, courseDetails);
					}

					Log.d(DB.CourseDetails.TABLE_NAME,
							"Retrieved " + list.size() + " items");
				} else {
					Log.d(DB.CourseDetails.TABLE_NAME,
							"Error: " + e.getMessage());
				}
			}
		});
	}

	// //////////////////////////////////////////////////////////////////////////
	public static void announcementsQuery(Context c, String courseID) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Announce.TABLE_NAME);
		String lastUpdate = getLastUpdate(context, DB.Announce.CONTENT_URI,
				DB.Announce.UPDATED_AT, DB.Announce.COURSE_ID, courseID);

		if (!TextUtils.isEmpty(courseID) && !courseID.equals("ALL"))
			query.whereEqualTo(DB.Announce.COURSE_ID, courseID);
		if (!TextUtils.isEmpty(lastUpdate)) {
			Date date = StringToDate(lastUpdate);
			if (date != null)
				query.whereGreaterThan(DB.Announce.UPDATED_AT, date);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject parseObject = li.next();
						ContentValues announce = new ContentValues();
						announce.put(DB.Announce.COURSE_ID,
								parseObject.getString(DB.Announce.COURSE_ID));
						announce.put(DB.Announce.MESSAGE,
								parseObject.getString(DB.Announce.MESSAGE));
						Date date = parseObject.getUpdatedAt();
						announce.put(DB.Announce.UPDATED_AT, DateToString(date));
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
}
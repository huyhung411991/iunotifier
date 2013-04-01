package com.iuinsider.iunotifier.providers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DBRetriever {
	private static ContentResolver contentResolver = null;
	private static Context context = null;

	public static String DateToString(Date date) {
		if (date == null)
			return "";
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.US);
		String string = sdf.format(date);

		return string;
	}

	public static Date StringToDate(String string) {
		if (TextUtils.isEmpty(string))
			return null;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.US);
		Date date = null;
		try {
			date = sdf.parse(string);
		} catch (java.text.ParseException e) {
			Log.e("eventDetails", e.toString());
		}

		return date;
	}

	public static void allNewsQuery(ContentResolver resolver) {
		contentResolver = resolver;

		// Clear local database table
		contentResolver.delete(DB.News.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.News.TABLE_NAME);
		// query.orderByDescending(DB.News.PARSE_ID);
		query.orderByDescending(DB.News.CREATED_AT);

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject newsObject = li.next();
						ContentValues news = new ContentValues();
						news.put(DB.News.TITLE,
								newsObject.getString(DB.News.TITLE));
						news.put(DB.News.LINK,
								newsObject.getString(DB.News.LINK));
						contentResolver.insert(DB.News.CONTENT_URI, news);
					}

					Log.d(DB.News.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.News.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static void allEventsQuery(ContentResolver resolver,
			String sortCondition) {
		contentResolver = resolver;

		// Clear local database table
		contentResolver.delete(DB.Events.CONTENT_URI, null, null);

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

		query.orderByAscending(DB.Events.DATE);
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject eventObject = li.next();
						ContentValues event = new ContentValues();
						event.put(DB.Events.TITLE,
								eventObject.getString(DB.Events.TITLE));
						event.put(DB.Events.DESCRIPTION,
								eventObject.getString(DB.Events.DESCRIPTION));
						event.put(DB.Events.PLACE,
								eventObject.getString(DB.Events.PLACE));
						Date date = eventObject.getDate(DB.Events.DATE);
						event.put(DB.Events.DATE, DateToString(date));
						contentResolver.insert(DB.Events.CONTENT_URI, event);
					}

					Log.d(DB.Events.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Events.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static void departmentsQuery(Context c,
			String timeCondition) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Departments.TABLE_NAME);

		if (!TextUtils.isEmpty(timeCondition)) {
			Date date = StringToDate(timeCondition);
			if (date != null)
				query.whereGreaterThan(DB.Departments.UPDATED_AT, date);
		} else {
			context.getContentResolver().delete(DB.Departments.CONTENT_URI, null, null);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject departmentObject = li.next();
						ContentValues department = new ContentValues();
						department.put(DB.Departments.ID, departmentObject
								.getString(DB.Departments.ID));
						department.put(DB.Departments.NAME,
								departmentObject.getString(DB.Departments.NAME));
						Date date = departmentObject.getUpdatedAt();
						department.put(DB.Departments.UPDATED_AT,
								DateToString(date));
						context.getContentResolver().insert(DB.Departments.CONTENT_URI,
								department);
					}

					Log.d(DB.Departments.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Departments.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static void coursesQuery(Context c,
			String departmentID, String timeAfter) {
		context = c;

		ParseQuery query = new ParseQuery(DB.Courses.TABLE_NAME);
		
		if (!TextUtils.isEmpty(departmentID) && !departmentID.equals("ALL"))
			query.whereEqualTo(DB.Courses.DEPARTMENT_ID, departmentID);

		if (!TextUtils.isEmpty(timeAfter)) {
			Date date = StringToDate(timeAfter);
			if (date != null)
				query.whereGreaterThan(DB.Courses.UPDATED_AT, date);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject courseObject = li.next();
						ContentValues course = new ContentValues();
						course.put(DB.Courses.ID,
								courseObject.getString(DB.Courses.ID));
						course.put(DB.Courses.NAME,
								courseObject.getString(DB.Courses.NAME));
						course.put(DB.Courses.DEPARTMENT_ID,
								courseObject.getString(DB.Courses.DEPARTMENT_ID));
						Date date = courseObject.getUpdatedAt();
						course.put(DB.Courses.UPDATED_AT, DateToString(date));
						context.getContentResolver().insert(DB.Courses.CONTENT_URI, course);
					}

					Log.d(DB.Courses.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Courses.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}
}

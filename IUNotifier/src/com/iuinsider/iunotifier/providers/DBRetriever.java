package com.iuinsider.iunotifier.providers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DBRetriever {
	private static ContentResolver contentResolver = null;

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
		contentResolver.delete(DB.Event.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.Event.TABLE_NAME);

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

		query.orderByAscending(DB.Event.DATE);
		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject eventObject = li.next();
						ContentValues event = new ContentValues();
						event.put(DB.Event.TITLE,
								eventObject.getString(DB.Event.TITLE));
						event.put(DB.Event.DESCRIPTION,
								eventObject.getString(DB.Event.DESCRIPTION));
						event.put(DB.Event.PLACE,
								eventObject.getString(DB.Event.PLACE));
						Date date = eventObject.getDate(DB.Event.DATE);
						event.put(DB.Event.DATE, DateToString(date));
						contentResolver.insert(DB.Event.CONTENT_URI, event);
					}

					Log.d(DB.Event.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Event.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static void DepartmentQuery(ContentResolver resolver,
			String timeCondition) {
		contentResolver = resolver;

		ParseQuery query = new ParseQuery(DB.Department.TABLE_NAME);

		if (!TextUtils.isEmpty(timeCondition)) {
			Date date = StringToDate(timeCondition);
			if (date != null)
				query.whereGreaterThan(DB.Department.UPDATED_AT, date);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject departmentObject = li.next();
						ContentValues department = new ContentValues();
						department.put(DB.Department.ID, departmentObject
								.getString(DB.Department.ID));
						department.put(DB.Department.NAME,
								departmentObject.getString(DB.Department.NAME));
						Date date = departmentObject
								.getDate(DB.Department.UPDATED_AT);
						department.put(DB.Department.UPDATED_AT,
								DateToString(date));
						contentResolver.insert(DB.Department.CONTENT_URI,
								department);
					}

					Log.d(DB.Department.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Department.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}

	public static void CourseQuery(ContentResolver resolver,
			String departmentID, String timeAfter) {
		contentResolver = resolver;

		ParseQuery query = new ParseQuery(DB.Course.TABLE_NAME);
		if (!TextUtils.isEmpty(departmentID))
			query.whereEqualTo(DB.Course.DEPARTMENT_ID, departmentID);

		if (!TextUtils.isEmpty(timeAfter)) {
			Date date = StringToDate(timeAfter);
			if (date != null)
				query.whereGreaterThan(DB.Course.UPDATED_AT, date);
		}

		query.findInBackground(new FindCallback() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ListIterator<ParseObject> li = list.listIterator();
					while (li.hasNext()) {
						ParseObject courseObject = li.next();
						ContentValues course = new ContentValues();
						course.put(DB.Course.ID,
								courseObject.getString(DB.Course.ID));
						course.put(DB.Course.NAME,
								courseObject.getString(DB.Course.NAME));
						Date date = courseObject.getDate(DB.Course.UPDATED_AT);
						course.put(DB.Course.UPDATED_AT, DateToString(date));
						contentResolver.insert(DB.Course.CONTENT_URI, course);
					}

					Log.d(DB.Course.TABLE_NAME, "Retrieved " + list.size()
							+ " items");
				} else {
					Log.d(DB.Course.TABLE_NAME, "Error: " + e.getMessage());
				}
			}
		});
	}
}

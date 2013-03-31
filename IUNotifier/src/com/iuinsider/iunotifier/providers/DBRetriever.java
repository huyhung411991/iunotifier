package com.iuinsider.iunotifier.providers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class DBRetriever {
	private static ContentResolver contentResolver = null;

	public static void allNewsQuery(ContentResolver resolver) {
		contentResolver = resolver;

		// Clear local database table
		contentResolver.delete(DB.News.CONTENT_URI, null, null);

		ParseQuery query = new ParseQuery(DB.News.TABLE_NAME);
		//query.orderByDescending(DB.News.PARSE_ID);
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

					Log.d("news", "Retrieved " + list.size() + " news");
				} else {
					Log.d("news", "Error: " + e.getMessage());
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

		if (sortCondition.equals("Today")) {
			long offset = System.currentTimeMillis();
			long start = (offset / 86400000l) * 86400000l;
			long end = start + 86340000;

			query.whereGreaterThanOrEqualTo("eventDate", new Date(start));
			query.whereLessThanOrEqualTo("eventDate", new Date(end));
		} else if (sortCondition.equals("Upcoming")) {
			query.whereGreaterThan("eventDate", new Date());
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
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss", Locale.US);
						event.put(DB.Event.DATE, sdf.format(date));
						// DateFormat df = DateFormat.getDateTimeInstance();
						// event.put(DB.Event.DATE, df.format(date));
						contentResolver.insert(DB.Event.CONTENT_URI, event);
					}

					Log.d("events", "Retrieved " + list.size() + " news");
				} else {
					Log.d("events", "Error: " + e.getMessage());
				}
			}
		});
	}
}

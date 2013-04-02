package com.iuinsider.iunotifier.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class IUContentProvider extends ContentProvider {
	// Used for the UriMacher
	private static final UriMatcher sUriMatcher;
	private static final int NEWS = 1;
	private static final int NEWS_ID = 2;
	private static final int EVENT = 3;
	private static final int EVENT_ID = 4;
	private static final int DEPARTMENT = 5;
	private static final int DEPARTMENT_ID = 6;
	private static final int COURSE = 7;
	private static final int COURSE_ID = 8;
	private static final int COURSE_DETAILS = 9;
	private static final int COURSE_DETAILS_ID = 10;
	private static final int ANNOUNCEMENT = 11;
	private static final int ANNOUNCEMENT_ID = 12;
	private static final int USER_COURSE = 13;
	private static final int USER_COURSE_ID = 14;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(DB.News.AUTHORITY, DB.News.TABLE_NAME, NEWS);
		sUriMatcher.addURI(DB.News.AUTHORITY, DB.News.TABLE_NAME + "/#",
				NEWS_ID);
		sUriMatcher.addURI(DB.Events.AUTHORITY, DB.Events.TABLE_NAME, EVENT);
		sUriMatcher.addURI(DB.Events.AUTHORITY, DB.Events.TABLE_NAME + "/#",
				EVENT_ID);
		sUriMatcher.addURI(DB.Departments.AUTHORITY, DB.Departments.TABLE_NAME,
				DEPARTMENT);
		sUriMatcher.addURI(DB.Departments.AUTHORITY, DB.Departments.TABLE_NAME
				+ "/*", DEPARTMENT_ID);
		sUriMatcher.addURI(DB.Courses.AUTHORITY, DB.Courses.TABLE_NAME, COURSE);
		sUriMatcher.addURI(DB.Courses.AUTHORITY, DB.Courses.TABLE_NAME + "/*",
				COURSE_ID);
		sUriMatcher.addURI(DB.CourseDetails.AUTHORITY,
				DB.CourseDetails.TABLE_NAME, COURSE_DETAILS);
		sUriMatcher.addURI(DB.CourseDetails.AUTHORITY,
				DB.CourseDetails.TABLE_NAME + "/*", COURSE_DETAILS_ID);
		sUriMatcher.addURI(DB.Announce.AUTHORITY, DB.Announce.TABLE_NAME,
				ANNOUNCEMENT);
		sUriMatcher.addURI(DB.Announce.AUTHORITY,
				DB.Announce.TABLE_NAME + "/*", ANNOUNCEMENT_ID);
		sUriMatcher.addURI(DB.UserCourses.AUTHORITY, DB.UserCourses.TABLE_NAME,
				USER_COURSE);
		sUriMatcher.addURI(DB.UserCourses.AUTHORITY, DB.UserCourses.TABLE_NAME
				+ "/*", USER_COURSE_ID);
	}

	private static DBHelper dbHelper;
	private static SQLiteDatabase db;

	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case NEWS:
			return DB.News.CONTENT_TYPE;
		case NEWS_ID:
			return DB.News.CONTENT_ITEM_TYPE;
		case EVENT:
			return DB.Events.CONTENT_TYPE;
		case EVENT_ID:
			return DB.Events.CONTENT_ITEM_TYPE;
		case DEPARTMENT:
			return DB.Departments.CONTENT_TYPE;
		case DEPARTMENT_ID:
			return DB.Departments.CONTENT_ITEM_TYPE;
		case COURSE:
			return DB.Courses.CONTENT_TYPE;
		case COURSE_ID:
			return DB.Courses.CONTENT_ITEM_TYPE;
		case COURSE_DETAILS:
			return DB.CourseDetails.CONTENT_TYPE;
		case COURSE_DETAILS_ID:
			return DB.CourseDetails.CONTENT_ITEM_TYPE;
		case ANNOUNCEMENT:
			return DB.Announce.CONTENT_TYPE;
		case ANNOUNCEMENT_ID:
			return DB.Announce.CONTENT_ITEM_TYPE;
		case USER_COURSE:
			return DB.UserCourses.CONTENT_TYPE;
		case USER_COURSE_ID:
			return DB.UserCourses.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (sUriMatcher.match(uri)) {
		case NEWS:
			queryBuilder.setTables(DB.News.TABLE_NAME);
			break;
		case NEWS_ID:
			queryBuilder.setTables(DB.News.TABLE_NAME);
			queryBuilder.appendWhere(DB.News._ID + " = "
					+ uri.getLastPathSegment());
			break;
		case EVENT:
			queryBuilder.setTables(DB.Events.TABLE_NAME);
			break;
		case EVENT_ID:
			queryBuilder.setTables(DB.Events.TABLE_NAME);
			queryBuilder.appendWhere(DB.Events._ID + " = "
					+ uri.getLastPathSegment());
			break;
		case DEPARTMENT:
			queryBuilder.setTables(DB.Departments.TABLE_NAME);
			break;
		case DEPARTMENT_ID:
			queryBuilder.setTables(DB.Departments.TABLE_NAME);
			queryBuilder.appendWhere(DB.Departments.ID + " = '"
					+ uri.getLastPathSegment() + "'");
			break;
		case COURSE:
			queryBuilder.setTables(DB.Courses.TABLE_NAME);
			break;
		case COURSE_ID:
			queryBuilder.setTables(DB.Courses.TABLE_NAME);
			queryBuilder.appendWhere(DB.Courses.ID + " = '"
					+ uri.getLastPathSegment() + "'");
			break;
		case COURSE_DETAILS:
			queryBuilder.setTables(DB.CourseDetails.TABLE_NAME);
			break;
		case COURSE_DETAILS_ID:
			queryBuilder.setTables(DB.CourseDetails.TABLE_NAME);
			queryBuilder.appendWhere(DB.CourseDetails.ID + " = '"
					+ uri.getLastPathSegment() + "'");
			break;
		case ANNOUNCEMENT:
			queryBuilder.setTables(DB.Announce.TABLE_NAME);
			break;
		case ANNOUNCEMENT_ID:
			queryBuilder.setTables(DB.Announce.TABLE_NAME);
			queryBuilder.appendWhere(DB.Announce._ID + " = '"
					+ uri.getLastPathSegment() + "'");
			break;
		case USER_COURSE:
			queryBuilder.setTables(DB.UserCourses.TABLE_NAME);
			break;
		case USER_COURSE_ID:
			queryBuilder.setTables(DB.UserCourses.TABLE_NAME);
			queryBuilder.appendWhere(DB.UserCourses.ID + " = '"
					+ uri.getLastPathSegment() + "'");
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		db = dbHelper.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);

		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		if (initialValues != null)
			values = new ContentValues(initialValues);
		else
			values = new ContentValues();

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowId = 0;
		Uri noteUri = null;
		switch (sUriMatcher.match(uri)) {
		case NEWS:
			rowId = db.insertWithOnConflict(DB.News.TABLE_NAME, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.News.CONTENT_URI, rowId);
			break;
		case EVENT:
			rowId = db.insertWithOnConflict(DB.Events.TABLE_NAME, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.Events.CONTENT_URI, rowId);
			break;
		case DEPARTMENT:
			rowId = db.insertWithOnConflict(DB.Departments.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.Departments.CONTENT_URI,
					rowId);
			break;
		case COURSE:
			rowId = db.insertWithOnConflict(DB.Courses.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.Courses.CONTENT_URI, rowId);
			break;
		case COURSE_DETAILS:
			rowId = db.insertWithOnConflict(DB.CourseDetails.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.CourseDetails.CONTENT_URI,
					rowId);
			break;
		case ANNOUNCEMENT:
			rowId = db.insertWithOnConflict(DB.Announce.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris
					.withAppendedId(DB.Announce.CONTENT_URI, rowId);
			break;
		case USER_COURSE:
			rowId = db.insertWithOnConflict(DB.UserCourses.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.UserCourses.CONTENT_URI,
					rowId);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		if (rowId > 0) {
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsDeleted = 0;

		switch (sUriMatcher.match(uri)) {
		case NEWS:
			rowsDeleted = db.delete(DB.News.TABLE_NAME, selection,
					selectionArgs);
			break;
		case NEWS_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.News.TABLE_NAME, DB.News._ID + " = "
						+ uri.getLastPathSegment(), null);
			else
				rowsDeleted = db.delete(DB.News.TABLE_NAME, DB.News._ID + " = "
						+ uri.getLastPathSegment() + " AND " + selection,
						selectionArgs);
			break;
		case EVENT:
			rowsDeleted = db.delete(DB.Events.TABLE_NAME, selection,
					selectionArgs);
			break;
		case EVENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Events.TABLE_NAME, DB.Events._ID
						+ " = " + uri.getLastPathSegment(), null);
			else
				rowsDeleted = db.delete(DB.Events.TABLE_NAME, DB.Events._ID
						+ " = " + uri.getLastPathSegment() + " AND "
						+ selection, selectionArgs);
			break;
		case DEPARTMENT:
			rowsDeleted = db.delete(DB.Departments.TABLE_NAME, selection,
					selectionArgs);
			break;
		case DEPARTMENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Departments.TABLE_NAME,
						DB.Departments.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsDeleted = db.delete(DB.Departments.TABLE_NAME,
						DB.Departments.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case COURSE:
			rowsDeleted = db.delete(DB.Courses.TABLE_NAME, selection,
					selectionArgs);
			break;
		case COURSE_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Courses.TABLE_NAME, DB.Courses.ID
						+ " = '" + uri.getLastPathSegment() + "'", null);
			else
				rowsDeleted = db.delete(DB.Courses.TABLE_NAME, DB.Courses.ID
						+ " = '" + uri.getLastPathSegment() + "' AND "
						+ selection, selectionArgs);
			break;
		case COURSE_DETAILS:
			rowsDeleted = db.delete(DB.CourseDetails.TABLE_NAME, selection,
					selectionArgs);
			break;
		case COURSE_DETAILS_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.CourseDetails.TABLE_NAME,
						DB.CourseDetails.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsDeleted = db.delete(DB.CourseDetails.TABLE_NAME,
						DB.CourseDetails.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case ANNOUNCEMENT:
			rowsDeleted = db.delete(DB.Announce.TABLE_NAME, selection,
					selectionArgs);
			break;
		case ANNOUNCEMENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Announce.TABLE_NAME, DB.Announce._ID
						+ " = '" + uri.getLastPathSegment() + "'", null);
			else
				rowsDeleted = db.delete(DB.Announce.TABLE_NAME, DB.Announce._ID
						+ " = '" + uri.getLastPathSegment() + "' AND "
						+ selection, selectionArgs);
			break;
		case USER_COURSE:
			rowsDeleted = db.delete(DB.UserCourses.TABLE_NAME, selection,
					selectionArgs);
			break;
		case USER_COURSE_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.UserCourses.TABLE_NAME,
						DB.UserCourses.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsDeleted = db.delete(DB.UserCourses.TABLE_NAME,
						DB.UserCourses.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rowsUpdated = 0;

		switch (sUriMatcher.match(uri)) {
		case NEWS:
			rowsUpdated = db.update(DB.News.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case NEWS_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.News.TABLE_NAME, values, DB.News._ID
						+ " = " + uri.getLastPathSegment(), null);
			else
				rowsUpdated = db.update(DB.News.TABLE_NAME, values, DB.News._ID
						+ " = " + uri.getLastPathSegment() + " AND "
						+ selection, selectionArgs);
			break;
		case EVENT:
			rowsUpdated = db.update(DB.Events.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case EVENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.Events.TABLE_NAME, values,
						DB.Events._ID + " = " + uri.getLastPathSegment(), null);
			else
				rowsUpdated = db.update(DB.Events.TABLE_NAME, values,
						DB.Events._ID + " = " + uri.getLastPathSegment()
								+ " AND " + selection, selectionArgs);
			break;
		case DEPARTMENT:
			rowsUpdated = db.update(DB.Departments.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case DEPARTMENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.Departments.TABLE_NAME, values,
						DB.Departments.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsUpdated = db.update(DB.Departments.TABLE_NAME, values,
						DB.Departments.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case COURSE:
			rowsUpdated = db.update(DB.Courses.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case COURSE_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db
						.update(DB.Courses.TABLE_NAME, values, DB.Courses.ID
								+ " = '" + uri.getLastPathSegment() + "'", null);
			else
				rowsUpdated = db.update(DB.Courses.TABLE_NAME, values,
						DB.Courses.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case COURSE_DETAILS:
			rowsUpdated = db.update(DB.CourseDetails.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case COURSE_DETAILS_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.CourseDetails.TABLE_NAME, values,
						DB.CourseDetails.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsUpdated = db.update(DB.CourseDetails.TABLE_NAME, values,
						DB.CourseDetails.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case ANNOUNCEMENT:
			rowsUpdated = db.update(DB.Announce.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case ANNOUNCEMENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.Announce.TABLE_NAME, values,
						DB.Announce._ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsUpdated = db.update(DB.Announce.TABLE_NAME, values,
						DB.Announce._ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case USER_COURSE:
			rowsUpdated = db.update(DB.UserCourses.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case USER_COURSE_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.UserCourses.TABLE_NAME, values,
						DB.UserCourses.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsUpdated = db.update(DB.UserCourses.TABLE_NAME, values,
						DB.UserCourses.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}
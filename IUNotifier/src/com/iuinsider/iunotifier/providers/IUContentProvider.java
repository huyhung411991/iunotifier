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

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(DB.News.AUTHORITY, DB.News.TABLE_NAME, NEWS);
		sUriMatcher.addURI(DB.News.AUTHORITY, DB.News.TABLE_NAME + "/#",
				NEWS_ID);
		sUriMatcher.addURI(DB.Event.AUTHORITY, DB.Event.TABLE_NAME, EVENT);
		sUriMatcher.addURI(DB.Event.AUTHORITY, DB.Event.TABLE_NAME + "/#",
				EVENT_ID);
		sUriMatcher.addURI(DB.Department.AUTHORITY, DB.Department.TABLE_NAME,
				DEPARTMENT);
		sUriMatcher.addURI(DB.Department.AUTHORITY, DB.Department.TABLE_NAME
				+ "/*", DEPARTMENT_ID);
		sUriMatcher.addURI(DB.Course.AUTHORITY, DB.Course.TABLE_NAME, COURSE);
		sUriMatcher.addURI(DB.Course.AUTHORITY, DB.Course.TABLE_NAME + "/*",
				COURSE_ID);
		sUriMatcher.addURI(DB.CourseDetails.AUTHORITY,
				DB.CourseDetails.TABLE_NAME, COURSE_DETAILS);
		sUriMatcher.addURI(DB.CourseDetails.AUTHORITY,
				DB.CourseDetails.TABLE_NAME + "/*", COURSE_DETAILS_ID);
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
			return DB.Event.CONTENT_TYPE;
		case EVENT_ID:
			return DB.Event.CONTENT_ITEM_TYPE;
		case DEPARTMENT:
			return DB.Department.CONTENT_TYPE;
		case DEPARTMENT_ID:
			return DB.Department.CONTENT_ITEM_TYPE;
		case COURSE:
			return DB.Course.CONTENT_TYPE;
		case COURSE_ID:
			return DB.Course.CONTENT_ITEM_TYPE;
		case COURSE_DETAILS:
			return DB.CourseDetails.CONTENT_TYPE;
		case COURSE_DETAILS_ID:
			return DB.CourseDetails.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DB.News.TABLE_NAME);

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
			queryBuilder.setTables(DB.Event.TABLE_NAME);
			break;
		case EVENT_ID:
			queryBuilder.setTables(DB.Event.TABLE_NAME);
			queryBuilder.appendWhere(DB.Event._ID + " = "
					+ uri.getLastPathSegment());
			break;
		case DEPARTMENT:
			queryBuilder.setTables(DB.Department.TABLE_NAME);
			break;
		case DEPARTMENT_ID:
			queryBuilder.setTables(DB.Department.TABLE_NAME);
			queryBuilder.appendWhere(DB.Department.ID + " = '"
					+ uri.getLastPathSegment() + "'");
			break;
		case COURSE:
			queryBuilder.setTables(DB.Course.TABLE_NAME);
			break;
		case COURSE_ID:
			queryBuilder.setTables(DB.Course.TABLE_NAME);
			queryBuilder.appendWhere(DB.Course.ID + " = '"
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
			rowId = db.insertWithOnConflict(DB.Event.TABLE_NAME, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.Event.CONTENT_URI, rowId);
			break;
		case DEPARTMENT:
			rowId = db.insertWithOnConflict(DB.Department.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.Department.CONTENT_URI,
					rowId);
			break;
		case COURSE:
			rowId = db.insertWithOnConflict(DB.Course.TABLE_NAME, null, values,
					SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.Course.CONTENT_URI, rowId);
			break;
		case COURSE_DETAILS:
			rowId = db.insertWithOnConflict(DB.CourseDetails.TABLE_NAME, null,
					values, SQLiteDatabase.CONFLICT_REPLACE);
			noteUri = ContentUris.withAppendedId(DB.CourseDetails.CONTENT_URI,
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
			rowsDeleted = db.delete(DB.Event.TABLE_NAME, selection,
					selectionArgs);
			break;
		case EVENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Event.TABLE_NAME, DB.Event._ID
						+ " = " + uri.getLastPathSegment(), null);
			else
				rowsDeleted = db.delete(DB.Event.TABLE_NAME, DB.Event._ID
						+ " = " + uri.getLastPathSegment() + " AND "
						+ selection, selectionArgs);
			break;
		case DEPARTMENT:
			rowsDeleted = db.delete(DB.Department.TABLE_NAME, selection,
					selectionArgs);
			break;
		case DEPARTMENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Department.TABLE_NAME,
						DB.Department.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsDeleted = db.delete(DB.Department.TABLE_NAME,
						DB.Department.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case COURSE:
			rowsDeleted = db.delete(DB.Course.TABLE_NAME, selection,
					selectionArgs);
			break;
		case COURSE_ID:
			if (TextUtils.isEmpty(selection))
				rowsDeleted = db.delete(DB.Course.TABLE_NAME, DB.Course.ID
						+ " = '" + uri.getLastPathSegment() + "'", null);
			else
				rowsDeleted = db.delete(DB.Course.TABLE_NAME, DB.Course.ID
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
			rowsUpdated = db.update(DB.Event.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case EVENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.Event.TABLE_NAME, values,
						DB.Event._ID + " = " + uri.getLastPathSegment(), null);
			else
				rowsUpdated = db.update(DB.Event.TABLE_NAME, values,
						DB.Event._ID + " = " + uri.getLastPathSegment()
								+ " AND " + selection, selectionArgs);
			break;
		case DEPARTMENT:
			rowsUpdated = db.update(DB.Department.TABLE_NAME, values,
					selection, selectionArgs);
			break;
		case DEPARTMENT_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.Department.TABLE_NAME, values,
						DB.Department.ID + " = '" + uri.getLastPathSegment()
								+ "'", null);
			else
				rowsUpdated = db.update(DB.Department.TABLE_NAME, values,
						DB.Department.ID + " = '" + uri.getLastPathSegment()
								+ "' AND " + selection, selectionArgs);
			break;
		case COURSE:
			rowsUpdated = db.update(DB.Course.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case COURSE_ID:
			if (TextUtils.isEmpty(selection))
				rowsUpdated = db.update(DB.Course.TABLE_NAME, values,
						DB.Course.ID + " = '" + uri.getLastPathSegment() + "'",
						null);
			else
				rowsUpdated = db.update(DB.Course.TABLE_NAME, values,
						DB.Course.ID + " = '" + uri.getLastPathSegment()
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
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}
}
package com.iuinsider.iunotifier.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "database.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_TABLE_NEWS = "CREATE TABLE "
			+ DB.News.TABLE_NAME + " (" + DB.News._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.News.TITLE
			+ " TEXT, " + DB.News.LINK + " TEXT)";
	private static final String DELETE_TABLE_NEWS = "DROP TABLE IF EXISTS "
			+ DB.News.TABLE_NAME;

	private static final String CREATE_TABLE_EVENTS = "CREATE TABLE "
			+ DB.Event.TABLE_NAME + " (" + DB.Event._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Event.TITLE
			+ " TEXT, " + DB.Event.DESCRIPTION + " TEXT, " + DB.Event.DATE
			+ " TEXT, " + DB.Event.TIME + " TEXT, " + DB.Event.PLACE + " TEXT)";

	private static final String DELETE_TABLE_EVENTS = "DROP TABLE IF EXISTS "
			+ DB.Event.TABLE_NAME;

	private static final String CREATE_TABLE_DEPARTMENTS = "CREATE TABLE "
			+ DB.Department.TABLE_NAME + " (" + DB.Department._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Department.ID
			+ "TEXT, " + DB.Department.NAME + " TEXT, "
			+ DB.Department.UPDATED_AT + " TEXT)";

	private static final String DELETE_TABLE_DEPARTMENTS = "DROP TABLE IF EXISTS "
			+ DB.Department.TABLE_NAME;

	private static final String CREATE_TABLE_COURSE = "CREATE TABLE "
			+ DB.Course.TABLE_NAME + " (" + DB.Course._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Course.ID + " TEXT, "
			+ DB.Course.NAME + " TEXT, " + DB.Course.DEPARTMENT_ID + " TEXT, "
			+ DB.Course.UPDATED_AT + " TEXT)";

	private static final String DELETE_TABLE_COURSE = "DROP TABLE IF EXISTS "
			+ DB.Course.TABLE_NAME;

	private static final String CREATE_TABLE_COURSE_DETAILS = "CREATE TABLE "
			+ DB.CourseDetails.TABLE_NAME + " (" + DB.CourseDetails._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.CourseDetails.ID
			+ " TEXT, " + DB.CourseDetails.NAME + " TEXT, "
			+ DB.CourseDetails.LECTURER + " TEXT, " + DB.CourseDetails.THEORY
			+ " TEXT, " + DB.CourseDetails.LAB + " TEXT, "
			+ DB.CourseDetails.CREDIT + " INTEGER, "
			+ DB.CourseDetails.PREREQUISIT + " TEXT, "
			+ DB.CourseDetails.UPDATED_AT + " TEXT)";

	private static final String DELETE_TABLE_COURSE_DETAILS = "DROP TABLE IF EXISTS "
			+ DB.CourseDetails.TABLE_NAME;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_NEWS);
		db.execSQL(CREATE_TABLE_EVENTS);
		db.execSQL(CREATE_TABLE_DEPARTMENTS);
		db.execSQL(CREATE_TABLE_COURSE);
		db.execSQL(CREATE_TABLE_COURSE_DETAILS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade
		// policy is to simply to discard the data and start over
		db.execSQL(DELETE_TABLE_NEWS);
		db.execSQL(DELETE_TABLE_EVENTS);
		db.execSQL(DELETE_TABLE_DEPARTMENTS);
		db.execSQL(DELETE_TABLE_COURSE);
		db.execSQL(DELETE_TABLE_COURSE_DETAILS);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
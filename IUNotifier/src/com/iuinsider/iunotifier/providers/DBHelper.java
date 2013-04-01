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
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_NEWS = "DROP TABLE IF EXISTS "
			+ DB.News.TABLE_NAME;
	// ----------------------------------------------------------------------------------
	private static final String CREATE_TABLE_EVENTS = "CREATE TABLE "
			+ DB.Events.TABLE_NAME + " (" + DB.Events._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Events.TITLE
			+ " TEXT, " + DB.Events.DESCRIPTION + " TEXT, " + DB.Events.DATE
			+ " TEXT, " + DB.Events.TIME + " TEXT, " + DB.Events.PLACE + " TEXT)";
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_EVENTS = "DROP TABLE IF EXISTS "
			+ DB.Events.TABLE_NAME;
	// ----------------------------------------------------------------------------------
	private static final String CREATE_TABLE_DEPARTMENTS = "CREATE TABLE "
			+ DB.Departments.TABLE_NAME + " (" + DB.Departments._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Departments.ID
			+ " TEXT UNIQUE, " + DB.Departments.NAME + " TEXT, "
			+ DB.Departments.UPDATED_AT + " TEXT)";
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_DEPARTMENTS = "DROP TABLE IF EXISTS "
			+ DB.Departments.TABLE_NAME;
	// ----------------------------------------------------------------------------------
	private static final String CREATE_TABLE_COURSES = "CREATE TABLE "
			+ DB.Courses.TABLE_NAME + " (" + DB.Courses._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Courses.ID
			+ " TEXT UNIQUE, " + DB.Courses.NAME + " TEXT, "
			+ DB.Courses.DEPARTMENT_ID + " TEXT, " + DB.Courses.UPDATED_AT
			+ " TEXT)";
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_COURSES = "DROP TABLE IF EXISTS "
			+ DB.Courses.TABLE_NAME;
	// ----------------------------------------------------------------------------------
	private static final String CREATE_TABLE_COURSE_DETAILS = "CREATE TABLE "
			+ DB.CourseDetails.TABLE_NAME + " (" + DB.CourseDetails._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.CourseDetails.ID
			+ " TEXT UNIQUE, " + DB.CourseDetails.NAME + " TEXT, "
			+ DB.CourseDetails.LECTURER + " TEXT, " + DB.CourseDetails.THEORY
			+ " TEXT, " + DB.CourseDetails.LAB + " TEXT, "
			+ DB.CourseDetails.CREDIT + " INTEGER, "
			+ DB.CourseDetails.PREREQUISITE + " TEXT, "
			+ DB.CourseDetails.UPDATED_AT + " TEXT)";
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_COURSE_DETAILS = "DROP TABLE IF EXISTS "
			+ DB.CourseDetails.TABLE_NAME;
	// ----------------------------------------------------------------------------------
	private static final String CREATE_TABLE_USER_COURSES = "CREATE TABLE "
			+ DB.UserCourses.TABLE_NAME + " (" + DB.UserCourses._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.UserCourses.ID
			+ " TEXT UNIQUE, " + DB.UserCourses.NAME + " TEXT, "
			+ DB.UserCourses.UPDATED_AT + " TEXT)";
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_USER_COURSES = "DROP TABLE IF EXISTS "
			+ DB.UserCourses.TABLE_NAME;
	// ----------------------------------------------------------------------------------
	private static final String CREATE_TABLE_COURSE_ANNOUNCEMENTS = "CREATE TABLE "
			+ DB.Announce.TABLE_NAME + " (" + DB.Announce._ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + DB.Announce.PARSE_ID
			+ " TEXT UNIQUE, " + DB.Announce.COURSE_ID + " TEXT, " + DB.Announce.MESSAGE + " TEXT, "
			+ DB.Announce.UPDATED_AT + " TEXT)";
	// ----------------------------------------------------------------------------------
	private static final String DELETE_TABLE_COURSE_ANNOUNCEMENTS = "DROP TABLE IF EXISTS "
			+ DB.Announce.TABLE_NAME;

	// ----------------------------------------------------------------------------------
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_NEWS);
		db.execSQL(CREATE_TABLE_EVENTS);
		db.execSQL(CREATE_TABLE_DEPARTMENTS);
		db.execSQL(CREATE_TABLE_COURSES);
		db.execSQL(CREATE_TABLE_COURSE_DETAILS);
		db.execSQL(CREATE_TABLE_USER_COURSES);
		db.execSQL(CREATE_TABLE_COURSE_ANNOUNCEMENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade
		// policy is to simply to discard the data and start over
		db.execSQL(DELETE_TABLE_NEWS);
		db.execSQL(DELETE_TABLE_EVENTS);
		db.execSQL(DELETE_TABLE_DEPARTMENTS);
		db.execSQL(DELETE_TABLE_COURSES);
		db.execSQL(DELETE_TABLE_COURSE_DETAILS);
		db.execSQL(DELETE_TABLE_USER_COURSES);
		db.execSQL(DELETE_TABLE_COURSE_ANNOUNCEMENTS);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
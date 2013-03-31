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
			+ " TEXT, " + DB.Event.TIME + " TEXT, " + DB.Event.PLACE
			+ " TEXT)";

	private static final String DELETE_TABLE_EVENTS = "DROP TABLE IF EXISTS "
			+ DB.Event.TABLE_NAME;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_NEWS);
		db.execSQL(CREATE_TABLE_EVENTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// This database is only a cache for online data, so its upgrade
		// policy is to simply to discard the data and start over
		db.execSQL(DELETE_TABLE_NEWS);
		db.execSQL(DELETE_TABLE_EVENTS);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}
}
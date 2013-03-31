package com.iuinsider.iunotifier.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DB {
	public static abstract class News implements BaseColumns {
		public static final String TABLE_NAME = "News";
		public static final String PARSE_ID = "newsID";
		public static final String TITLE = "newsTitle";
		public static final String LINK = "newsLink";
		public static final String CREATED_AT = "createdAt";
		
		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		
	}
	
	public static abstract class Event implements BaseColumns {
		public static final String TABLE_NAME = "Events";
		public static final String PARSE_ID = "eventID";
		public static final String DESCRIPTION = "eventDescription"; 
		public static final String TITLE = "eventTitle";
		public static final String DATE = "eventDate";
		public static final String TIME = "eventTime";
		public static final String PLACE = "eventPlace";
		
		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		
	}

	private DB() {
	}
}

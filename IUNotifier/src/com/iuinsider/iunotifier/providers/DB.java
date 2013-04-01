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
	
	public static abstract class Events implements BaseColumns {
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
	
	public static abstract class Departments implements BaseColumns {
		public static final String TABLE_NAME = "Departments";
		public static final String ID = "departmentID";
		public static final String NAME = "departmentName";
		public static final String UPDATED_AT = "updatedAt";
		
		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		
	}
	
	public static abstract class Courses implements BaseColumns {
		public static final String TABLE_NAME = "Courses";
		public static final String ID = "courseID";
		public static final String NAME = "courseName";
		public static final String DEPARTMENT_ID = "departmentID";
		public static final String UPDATED_AT = "updatedAt";
		
		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		
	}
	
	public static abstract class UserCourses implements BaseColumns {
		public static final String TABLE_NAME = "UserCourses";
		public static final String ID = "courseID";
		public static final String NAME = "courseName";
		public static final String UPDATED_AT = "updatedAt";
		
		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		
	}
	
	public static abstract class CourseDetails implements BaseColumns {
		public static final String TABLE_NAME = "CourseDetails";
		public static final String ID = "courseID";
		public static final String NAME = "courseName";
		public static final String LECTURER = "lecturer";
		public static final String THEORY = "theory";
		public static final String LAB = "lab";
		public static final String CREDIT = "credit";
		public static final String PREREQUISIT = "prerequisit";
		public static final String UPDATED_AT = "updatedAt";
		
		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
	}
	
	public static abstract class CourseAnnouncements implements BaseColumns {
		public static final String TABLE_NAME = "CourseAnnouncementS";
		public static final String ID = "courseID";
		public static final String MESSAGE = "courseMsg";
		public static final String UPDATED_AT = "updatedAt";
		
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

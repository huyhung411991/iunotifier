package com.iuinsider.iunotifier.providers;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

///////////////////////////////////////////////////////////////////////////////////////////////
public class DB {
	
	public static abstract class News implements BaseColumns {
		public static final String TABLE_NAME = "News";
		public static final String PARSE_ID = "newsID";
		public static final String TITLE = "newsTitle";
		public static final String LINK = "newsLink";
		public static final String SOURCE = "newsSource";
		public static final String CREATED_AT = "createdAt";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class Events implements BaseColumns {
		public static final String TABLE_NAME = "Events";
		public static final String PARSE_ID = "eventID";
		public static final String DESCRIPTION = "eventDescription";
		public static final String TITLE = "eventTitle";
		public static final String DATE = "eventDate";
		public static final String PLACE = "eventPlace";
		public static final String CREATED_AT = "createdAt";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class Departments implements BaseColumns {
		public static final String TABLE_NAME = "Departments";
		public static final String ID = "departmentID";
		public static final String NAME = "departmentName";
		public static final String UPDATED_AT = "updatedAt";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class Courses implements BaseColumns {
		public static final String TABLE_NAME = "Courses";
		public static final String ID = "courseID";
		public static final String NAME = "courseName";
		public static final String DEPARTMENT_ID = "departmentID";
		public static final String UPDATED_AT = "updatedAt";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class UserCourses implements BaseColumns {
		public static final String TABLE_NAME = "UserCourses";
		public static final String ID = "courseID";
		public static final String NAME = "courseName";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class CourseDetails implements BaseColumns {
		public static final String TABLE_NAME = "CourseDetails";
		public static final String ID = "courseID";
		public static final String NAME = "courseName";
		public static final String LECTURER = "lecturer";
		public static final String THEORY = "theory";
		public static final String LAB = "lab";
		public static final String CREDIT = "credit";
		public static final String PREREQUISITE = "prerequisite";
		public static final String UPDATED_AT = "updatedAt";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////
	public static abstract class Announce implements BaseColumns {
		public static final String TABLE_NAME = "Announcements";
		public static final String ID = "objectId";
		public static final String COURSE_ID = "courseID";
		public static final String MESSAGE = "announceMsg";
		public static final String CREATED_AT = "createdAt";
		public static final String UPDATED_AT = "updatedAt";
		public static final String SUCCESSFUL_MSG = "Push Successful";
		public static final String FAILED_MSG = "Push Failed";

		public static final String AUTHORITY = "com.iuinsider.iunotifier.provider";
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/" + TABLE_NAME);
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/" + AUTHORITY + "." + TABLE_NAME;
	}
	
	public static class UserPermission {
		public static final String USER_COLUMN = "Permission";
		public static final String USER_ADMIN = "admin";
		public static final String USER_MODERATOR = "moderator";
		public static final String USER_STAFF = "staff";
		public static final String USER_TEACHER = "teacher";
		public static final String USER_STUDENT = "student";
	}

	private DB() {
	}
}

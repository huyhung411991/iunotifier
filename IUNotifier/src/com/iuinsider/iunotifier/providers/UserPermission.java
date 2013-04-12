package com.iuinsider.iunotifier.providers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class UserPermission {
	public static final String USER_COLUMN = "Permission";
	
	private static final String ADMIN = "admin";
	private static final String MODERATOR = "moderator";
	private static final String STAFF = "staff";
	private static final String TEACHER = "teacher";
	private static final String STUDENT = "student";

	public static boolean hasPushPermission1(String userRole) {
		if (userRole == null)
			return false;
		if (userRole.equals(ADMIN) || userRole.equals(MODERATOR)
				|| userRole.equals(STAFF))
			return true;
		return false;
	}

	public static boolean hasPushPermission2(Context context, String userRole,
			String courseID) {
		if (userRole == null || courseID == null || context == null)
			return false;
		if (userRole.equals(TEACHER)) {
			Uri uri = Uri
					.withAppendedPath(DB.UserCourses.CONTENT_URI, courseID);
			Cursor courseCursor = context.getContentResolver().query(uri, null,
					null, null, null);
			boolean hasCourse = courseCursor.getCount() > 0;

			if (hasCourse)
				return true;
		}
		return false;
	}

	public static boolean hasUserCourses(String userRole) {
		if (userRole == null)
			return false;
		if (userRole.equals(ADMIN) || userRole.equals(TEACHER)
				|| userRole.equals(STUDENT))
			return true;
		return false;
	}

	public static boolean hasCourseSubscribe(String userRole) {
		if (userRole == null)
			return false;
		if (userRole.equals(ADMIN) || userRole.equals(STUDENT))
			return true;
		return false;
	}
}

package com.iuinsider.iunotifier.test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.iuinsider.iunotifier.providers.DBRetriever;

public class DBRetrieverTest extends TestCase {
	@SmallTest
	public void testDateToString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		String string = sdf.format(new Date());
		
		assertEquals(string, DBRetriever.DateToString(new Date(), 2));
		assertNull("date null", DBRetriever.DateToString(null, 1));
		assertNull("format out of range", DBRetriever.DateToString(new Date(), -1));
		
	}
	
	@SmallTest
	public void testStringToDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date = null;
		try {
			date = sdf.parse("2013-04-12");
		} catch (Exception e) {
		}
		
		assertEquals(date, DBRetriever.StringToDate("2013-04-12", 2));
		assertNull("date null", DBRetriever.DateToString(null, 1));
		assertNull("format out of range", DBRetriever.DateToString(new Date(), -1));
		
	}
}

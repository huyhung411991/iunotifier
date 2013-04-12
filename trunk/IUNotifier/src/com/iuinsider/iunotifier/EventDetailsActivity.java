package com.iuinsider.iunotifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class EventDetailsActivity extends Activity {

	// =========================================================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);

		// Extract data from Intent
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		final String eventTitle = bundle.getString("EventTitle");
		final String eventDescription = bundle.getString("EventDescription");
		final String eventPlace = bundle.getString("EventPlace");
		final String eventDateTime = bundle.getString("EventDateTime");
		final Calendar calendar = Calendar.getInstance();

		// Get textview
		TextView titleView = (TextView) findViewById(R.id.event_details_title_textView);
		TextView descriptionView = (TextView) findViewById(R.id.event_details_description_textView);
		TextView dateView = (TextView) findViewById(R.id.event_details_date_textView);
		TextView timeView = (TextView) findViewById(R.id.event_details_time_textView);
		TextView placeView = (TextView) findViewById(R.id.event_details_place_textView);

		// Format time, date
		Date datetime = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.US);
		SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE, dd-MM-yyyy",
				Locale.US);
		SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm", Locale.US);

		try {
			datetime = sdf.parse(eventDateTime);
		} catch (ParseException e) {
			Log.e("eventDetails", e.toString());
		}
		calendar.setTime(datetime);

		String date = sdf2.format(datetime);
		String time = sdf3.format(datetime);

		// Set value for textViews
		titleView.setText(eventTitle);
		descriptionView.setText(eventDescription);
		dateView.setText(date);
		timeView.setText(time);
		placeView.setText(eventPlace);

		findViewById(R.id.event_details_cancel_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						onBackPressed();
					}
				});

		findViewById(R.id.event_details_addToCalendar_button)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						addToCalendar(eventTitle, eventDescription, calendar,
								eventPlace);
						onBackPressed();
					}
				});
	}

	// =========================================================================================
	// This override the default animation of the Android Device "Back" Button
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	// =========================================================================================
	private void addToCalendar(String title, String description,
			Calendar calendar, String place) {

		Intent intent = new Intent(Intent.ACTION_INSERT)
				.setType("vnd.android.cursor.item/event")
				.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
						calendar.getTimeInMillis())
				// .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
				// endTime.getTimeInMillis())
				.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
				// just included for completeness
				.putExtra(Events.TITLE, title)
				.putExtra(Events.DESCRIPTION, description)
				.putExtra(Events.EVENT_LOCATION, place)
				.putExtra(Events.RRULE, "FREQ=DAILY;COUNT=10")
				.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
				.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE)
				.putExtra(Intent.EXTRA_EMAIL, "");
		startActivity(intent);
	}
}

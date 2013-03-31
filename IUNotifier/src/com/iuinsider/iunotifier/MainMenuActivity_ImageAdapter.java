package com.iuinsider.iunotifier;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuActivity_ImageAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater layoutInflater;

	// =========================================================================================
	public MainMenuActivity_ImageAdapter(Context c) {
		mContext = c;
		layoutInflater = LayoutInflater.from(mContext);
	}

	// =========================================================================================
	@Override
	public int getCount() {
		return mThumbIds.length;
	}

	// =========================================================================================
	@Override
	public Object getItem(int position) {
		return null;
	}

	// =========================================================================================
	@Override
	public long getItemId(int position) {
		return 0;
	}

	// =========================================================================================
	// Create a new ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View grid;

		if (convertView == null) {
			grid = new View(mContext);
			grid = layoutInflater.inflate(
					R.layout.activity_main_menu_gridlayout, null);
		} else {
			grid = convertView;
		}

		// Set attributes for icons
		ImageView imageView = (ImageView) grid.findViewById(R.id.main_menu_gridLayout_icon_imageView);
		imageView.setImageResource(mThumbIds[position]);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		// Set the texts below icons	
		TextView textView = (TextView) grid.findViewById(R.id.main_menu_gridLayout_iconName_textView);
		textView.setText(iconNames[position]);
		textView.setTextColor(Color.WHITE);


		return grid;
	}

	// =========================================================================================
	// References to icons images and texts
	private Integer[] mThumbIds = { R.drawable.news, R.drawable.calendar,
			R.drawable.courses };
	private String[] iconNames = { "News", "Events", "Courses" };
}

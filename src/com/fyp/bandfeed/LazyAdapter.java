package com.fyp.bandfeed;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {

	private Activity activity;
	private ArrayList<Feed> listOfFeeds;
	private ArrayList<Entry> listOfEntries;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public LazyAdapter(Activity a, ArrayList<Feed> fList, ArrayList<Entry> eList) {
		activity = a;
		listOfFeeds = fList;
		listOfEntries = eList;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	public int getCount() {
		if (listOfFeeds == null) {
			return listOfEntries.size();
		} else {
			return listOfFeeds.size();
		}
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;

		if (listOfFeeds == null) {

			// This is dealing with a list of Entries
			if (convertView == null) {
				vi = inflater.inflate(R.layout.item2, null);
			}

			TextView textBandName = (TextView) vi.findViewById(R.id.bandName);
			textBandName.setText(listOfEntries.get(position).getBandName());
			TextView textGenres = (TextView) vi.findViewById(R.id.genres);
			textGenres.setText(listOfEntries.get(position).getGenre1() + " \\ "
					+ listOfEntries.get(position).getGenre2() + " \\ "
					+ listOfEntries.get(position).getGenre3());
			ImageView bImage = (ImageView) vi.findViewById(R.id.thumb2);
			imageLoader.DisplayImage(listOfEntries.get(position).getBandName(), bImage);
			return vi;

		} else {

			// This is dealing with a list of Feeds
			if (convertView == null) {
				vi = inflater.inflate(R.layout.item, null);
			}

			if (listOfFeeds.get(position).getType().equals("news")
					|| listOfFeeds.get(position).getType().equals("releases")
					|| listOfFeeds.get(position).getType().equals("updates")
					|| listOfFeeds.get(position).getType().equals("gigs")) {
				TextView textTitle = (TextView) vi.findViewById(R.id.title);
				textTitle.setText(listOfFeeds.get(position).getName());
				TextView textDetails = (TextView) vi.findViewById(R.id.details);
				textDetails.setText("    "
						+ listOfFeeds.get(position).getDate());
				TextView textMessage = (TextView) vi.findViewById(R.id.message);
				textMessage.setText(listOfFeeds.get(position).getMessage());
				ImageView image = (ImageView) vi.findViewById(R.id.thumb);
				imageLoader
						.DisplayImage(listOfFeeds.get(position).getName(), image);
				return vi;
			} else {
				TextView textTitle = (TextView) vi.findViewById(R.id.title);
				textTitle.setText("LINK REQUEST");
				TextView textDetails = (TextView) vi.findViewById(R.id.details);
				textDetails.setText("    "
						+ listOfFeeds.get(position).getDate());
				TextView textMessage = (TextView) vi.findViewById(R.id.message);
				textMessage.setText(listOfFeeds.get(position).getType()
						+ " wants to be linked to "
						+ listOfFeeds.get(position).getMessage()
						+ ". To follow this up click this message!");
				ImageView image = (ImageView) vi.findViewById(R.id.thumb);
				imageLoader
						.DisplayImage(listOfFeeds.get(position).getName(), image);
				return vi;
			}
		}
	}
}
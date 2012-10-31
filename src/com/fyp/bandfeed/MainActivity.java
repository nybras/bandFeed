/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

public class MainActivity extends Activity implements OnClickListener {

	private SparseArray<String> ids; //Instead of HashMap

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ids = new SparseArray<String>();

		// set up a scrollable view that is linear with a vertical orientation
		ScrollView scrollView = new ScrollView(this);
		LinearLayout.LayoutParams lp;
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		// Need to read up on this
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		scrollView.addView(linearLayout);

		linearLayout.setPadding(30, 30, 30, 30);

		this.setContentView(scrollView, lp);
		
		final float scale = getResources().getDisplayMetrics().density;
		int dip = (int) (100 * scale + 0.5f);
		// Don't understand this
		
		ImageView bandFeedLogo = new ImageView(this);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		bandFeedLogo.setLayoutParams(vp);
		bandFeedLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
		bandFeedLogo.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		bandFeedLogo.setMaxHeight(2);
		bandFeedLogo.setMaxWidth(2);
		bandFeedLogo.setImageDrawable(getResources().getDrawable(R.drawable.band_feed_equally_sized));
		bandFeedLogo.setPadding(0, 0, 0, 20);
		linearLayout.addView(bandFeedLogo);

		

		String dirPath = getFilesDir().getAbsolutePath() + File.separator;
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (files.length > 0) {
			for (File file : files) {
				Button button = new Button(this);
				// button to next activity
				Integer id = findId(1);
				button.setId(id);
				ids.put(id, file.getName());
				button.setLayoutParams(new LinearLayout.LayoutParams(
						(int) (dip * scale),
						LinearLayout.LayoutParams.WRAP_CONTENT));
				// Need to read up on this
				// http://stackoverflow.com/questions/5691411/dynamically-change-the-width-of-a-button-in-android
				button.setText(file.getName());
				button.setOnClickListener(this);
				linearLayout.addView(button);
			}
		}

		Button newBandButton = new Button(this);
		// button to next activity
		Integer id = findId(1);
		newBandButton.setId(id);
		ids.put(id, "newBandButton");
		newBandButton.setText("Create a new Band profile");
		newBandButton.setOnClickListener(this);
		linearLayout.addView(newBandButton);
		
		Button browseButton = new Button(this);
		// button to next activity
		Integer id2 = findId(1);
		browseButton.setId(id2);
		ids.put(id2, "browseButton");
		browseButton.setText("Browse bands");
		browseButton.setOnClickListener(this);
		linearLayout.addView(browseButton);
	
		Button aboutButton = new Button(this);
		// button to next activity
		Integer id1 = findId(1);
		aboutButton.setId(id1);
		ids.put(id1, "aboutButton");
		aboutButton.setText("About");
		aboutButton.setOnClickListener(this);
		linearLayout.addView(aboutButton);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Decides from the given buttons which new view to open up 
	 * @param v takes the button that has just been clicked
	 */

	public void onClick(View v) {
		Intent i = null;
		if (ids.get(v.getId()).equals("newBandButton")) {
			i = new Intent(this, AddNewBandStepOne.class);
			startActivity(i);
		} else if (ids.get(v.getId()).equals("aboutButton")) {
			i = new Intent(this, WTF.class);
			startActivity(i);
		} else if (ids.get(v.getId()).equals("browseButton")) {
			i = new Intent(this, Browse.class);
			startActivity(i);
		} else {
			i = new Intent(this, BandProfileSD.class);
			i.putExtra("profile", ids.get(v.getId()));
			startActivity(i);
		}
	}

	private int findId(int id) {
		// create an ID
		View v = findViewById(id);
		while (v != null) {
			v = findViewById(++id);
		}
		return id++;

	}
}

package com.fyp.bandfeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BandProfileSD extends Activity {

	private String bandName;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

		Bundle extras = getIntent().getExtras();
		bandName = extras.getString("profile");

		String res = "";
		String line;

		String path = getFilesDir().getAbsolutePath() + File.separator
				+ bandName.toString() + File.separator + bandName.toString();

		try {

			FileInputStream fin = new FileInputStream(path + ".profile");

			// prepare the file for reading
			InputStreamReader inputreader = new InputStreamReader(fin);
			BufferedReader buffreader = new BufferedReader(inputreader);

			// read every line of the file into the line-variable, on line
			// at the time
			while ((line = buffreader.readLine()) != null) {
				// do something with the settings from the file
				res += line + "\n";
			}

			// close the file again
			fin.close();
		} catch (java.io.FileNotFoundException e) {
			// do something if the myfilename.txt does not exits
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File imgFile = new File(path + ".jpg");
		if (imgFile.exists()) {

			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());
			ImageView bandImage = new ImageView(this);
			bandImage.setImageBitmap(myBitmap);
			bandImage.setPadding(0, 0, 0, 20);
			linearLayout.addView(bandImage);

		}

		TextView profileTextView = new TextView(this);
		profileTextView.setText(res.toString());
		profileTextView.setGravity(Gravity.CENTER);
		linearLayout.addView(profileTextView);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_band_profile, menu);
		return true;
	}
}

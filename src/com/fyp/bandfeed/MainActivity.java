/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private SparseArray<String> ids; // Instead of HashMap
	private boolean userLoggedIn;
	private String user;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		userLoggedIn = false;
		user = "";
		ids = new SparseArray<String>();

		// set up a scroll view that is linear with a vertical orientation
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

		// Set Image with parameters
		ImageView bandFeedLogo = new ImageView(this);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		bandFeedLogo.setLayoutParams(vp);
		bandFeedLogo.setScaleType(ImageView.ScaleType.CENTER_CROP);
		bandFeedLogo.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		bandFeedLogo.setMaxHeight(2);
		bandFeedLogo.setMaxWidth(2);
		bandFeedLogo.setImageDrawable(getResources().getDrawable(
				R.drawable.band_feed_equally_sized));
		bandFeedLogo.setPadding(0, 0, 0, 20);
		linearLayout.addView(bandFeedLogo);

		// Make a check to see if a User has already logged in (checks app's
		// memory)
		String dirPath = getFilesDir().getAbsolutePath() + File.separator;
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (files.length > 0) {
			for (File file : files) {
				if (file.getName().equals("user.profile")) {

					String line;

					try {
						FileInputStream fin = new FileInputStream(dirPath
								+ "user.profile");

						// prepare the file for reading
						InputStreamReader inputreader = new InputStreamReader(
								fin);
						BufferedReader buffreader = new BufferedReader(
								inputreader);

						// read every line of the file into the line-variable,
						// on line at the time
						while ((line = buffreader.readLine()) != null) {
							// do something with the settings from the file
							user += line;
						}

						// close the file again
						fin.close();
					} catch (java.io.FileNotFoundException e) {
						// do something if the myfilename.txt does not exits
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					userLoggedIn = true;
				}
			}
		}

		else {
			// User has not logged in yet so start the login activity
			Intent i = new Intent(this, Login.class);
			startActivity(i);
		}

		if (userLoggedIn) {

			// User has logged in and so the activity can be completed
			TextView welcome = new TextView(this);
			welcome.setText("Welcome " + user + "!");
			welcome.setGravity(Gravity.CENTER);
			linearLayout.addView(welcome);

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

			Button sendMessageButton = new Button(this);
			// button to next activity
			Integer id3 = findId(1);
			sendMessageButton.setId(id3);
			ids.put(id3, "sendMessageButton");
			sendMessageButton.setText("Test Send Message");
			sendMessageButton.setOnClickListener(this);
			linearLayout.addView(sendMessageButton);

			Button receiveMessageButton = new Button(this);
			// button to next activity
			Integer id4 = findId(1);
			receiveMessageButton.setId(id4);
			ids.put(id4, "receiveMessageButton");
			receiveMessageButton.setText("Test Receive Message");
			receiveMessageButton.setOnClickListener(this);
			linearLayout.addView(receiveMessageButton);

			Button aboutButton = new Button(this);
			// button to next activity
			Integer id1 = findId(1);
			aboutButton.setId(id1);
			ids.put(id1, "aboutButton");
			aboutButton.setText("About");
			aboutButton.setOnClickListener(this);
			linearLayout.addView(aboutButton);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Decides from the given buttons which new view to open up
	 * 
	 * @param v
	 *            takes the button that has just been clicked
	 */

	public void onClick(View v) {
		Intent i = null;
		if (ids.get(v.getId()).equals("newBandButton")) {
			i = new Intent(this, StepOne.class);
			startActivity(i);
		} else if (ids.get(v.getId()).equals("aboutButton")) {
			i = new Intent(this, About.class);
			startActivity(i);
		} else if (ids.get(v.getId()).equals("browseButton")) {
			i = new Intent(this, BrowseCriteria.class);
			startActivity(i);
		} else if (ids.get(v.getId()).equals("sendMessageButton")) {
			i = new Intent(this, SendMessages.class);
			startActivity(i);
		} else if (ids.get(v.getId()).equals("receiveMessageButton")) {
			i = new Intent(this, ReceiveMessages.class);
			startActivity(i);
		} else {
			// Do nothing
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

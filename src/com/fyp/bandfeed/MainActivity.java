/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SwipeActivity implements OnClickListener {

	private SharedPreferences prefs;
	int numOfBands;
	private String username;
	private TextView instruction, instruction2, welcome;
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		Button create = (Button) findViewById(R.id.create_profile_button);
		create.setOnClickListener(this);

		Button browse = (Button) findViewById(R.id.browse_bands_button);
		browse.setOnClickListener(this);

		welcome = (TextView) findViewById(R.id.welcome);

		instruction = (TextView) findViewById(R.id.swipe);
		instruction2 = (TextView) findViewById(R.id.swipe2);
	}

	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.create_profile_button:
			i = new Intent(this, StepOne.class);
			startActivity(i);
			break;
		case R.id.browse_bands_button:
			i = new Intent(this, BrowseCriteria.class);
			startActivity(i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		prefs = getSharedPreferences("userPrefs", 0);
		username = prefs.getString("userName", null);
		numOfBands = prefs.getInt("numOfBands", 0);

		if (username == null) {
			// User has not logged in yet so start the login activity
			Intent i = new Intent(this, Login.class);
			startActivity(i);
		} else {
			setXMLFileUp();
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		username = prefs.getString("userName", null);

		if (username == null) {
			this.finish();
		}

		upDateListofBands();

		welcome.setText("Welcome to bandFeed, " + username + "!");
		updateTextViews();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// respond to menu item selection
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "MainActivity");
			startActivity(i);
			return true;
		case R.id.log_out:
			Editor editor = prefs.edit();
			editor.clear();
			editor.commit();
			Intent intent = new Intent(this, Login.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void left() {
		if (numOfBands > 0) {
			Intent i = new Intent(this, SendMessages.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
		}

	}

	@Override
	protected void right() {
		if (prefs.getBoolean("fileCreated", false)) {
			Intent i = new Intent(this, AllFeed.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(i);
		} else {
			Toast.makeText(MainActivity.this,
					"Trouble loading feeds, try re-installing bandFeed",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void upDateListofBands() {
		new Thread(new Runnable() {
			public void run() {

				CheckForBands nb = new CheckForBands(username);
				ArrayList<String> bands = nb.check();
				if (bands != null) {
					Editor editor = prefs.edit();
					for (int i = 0; i < bands.size(); i++) {
						editor.putString("band" + i, bands.get(i));
					}
					editor.putInt("numOfBands", bands.size());
					editor.commit();

					mHandler.post(new Runnable() {
						public void run() {
							updateTextViews();

						}
					});
				}
			}

		}).start();
	}

	private void setXMLFileUp() {
		// See if a 'username'.xml file already exists, if doesn't create one!
		// XML file is used to "cache" the most recent feeds allowing for quick
		// and disconnected access
		try {
			FileInputStream fIn = openFileInput(username + ".xml");
			if (fIn != null) {
				Editor editor = prefs.edit();
				editor.putBoolean("fileCreated", true);
				editor.commit();
			}
		} catch (FileNotFoundException e1) {
			try {
				@SuppressWarnings("unused")
				FileOutputStream fOut = openFileOutput(username + ".xml",
						MODE_WORLD_READABLE);
				Editor editor = prefs.edit();
				editor.putBoolean("fileCreated", true);
				editor.commit();
			} catch (FileNotFoundException e) {
				Log.e("File Error", "Can't find or create file" + e.toString());
				Editor editor = prefs.edit();
				editor.putBoolean("fileCreated", false);
				editor.commit();
			}
		}
	}

	private void updateTextViews() {
		numOfBands = prefs.getInt("numOfBands", 0);
		// Display the appropriate text depending on whether the user has a band
		// profile or not due to such a user possessing the ability to now swipe
		// right to send a message.
		if (numOfBands == 0) {
			instruction.setText("Swipe right to flick through your feeds.");
		} else {
			instruction
					.setText("Swipe left to send a message to your followers.");
			instruction2.setText("Swipe right to flick through your feeds.");
		}
	}

}

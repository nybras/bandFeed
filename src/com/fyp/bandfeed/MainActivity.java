/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SwipeActivity implements OnClickListener {

	private SharedPreferences prefs;
	int numOfBands;
	private String username;
	private TextView instruction, instruction2, welcome, reco1, reco2;
	private Handler bHandler = new Handler();
	private Handler rHandler = new Handler();
	private ImageLoader imageLoader;
	private ImageView profImage;
	private String recommendation;
	private boolean recommendationMade;
	private int recoType;
	private int randomInt;
	private boolean connection = false;
	private boolean foundEntries = false;
	private Intent displayProf;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}
		imageLoader = new ImageLoader(this);
		profImage = (ImageView) findViewById(R.id.profile_image);
		displayProf = new Intent(this, DisplayProfile.class);

		Button create = (Button) findViewById(R.id.create_profile_button);
		create.setOnClickListener(this);

		Button browse = (Button) findViewById(R.id.browse_bands_button);
		browse.setOnClickListener(this);

		welcome = (TextView) findViewById(R.id.welcome);

		instruction = (TextView) findViewById(R.id.swipe);
		instruction2 = (TextView) findViewById(R.id.swipe2);

		reco1 = (TextView) findViewById(R.id.reco1);
		reco2 = (TextView) findViewById(R.id.reco2);

		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(3) + 1;
		recommendationMade = false;

		// WebView webView = (WebView) findViewById(R.id.webView1);
		// webView.getSettings().setJavaScriptEnabled(true);
		// //webView.loadUrl("https://w.soundcloud.com/player/?url=http%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F58675214");
		// //https://w.soundcloud.com/player/?url=http%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F58675214
		// webView.loadData("<iframe width=\"100%\" height=\"166\" scrolling=\"no\" frameborder=\"no\" src=\"https://w.soundcloud.com/player/?url=http%3A%2F%2Fapi.soundcloud.com%2Ftracks%2F58675214\"></iframe>",
		// "text/html",
		// "utf-8");
		// webView.setBackgroundColor(0);
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
			break;
		case R.id.profile_image:
			startActivity(displayProf);
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
			if (!recommendationMade) {
				setXMLFileUp();
				getRecom();
			}
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

					bHandler.post(new Runnable() {
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
			instruction2.setText("");
		} else {
			instruction
					.setText("Swipe left to send a message to your followers.");
			instruction2.setText("Swipe right to flick through your feeds.");
		}
	}

	private void getRecom() {
		new Thread(new Runnable() {
			public void run() {
				final JSONParser jsonParser = new JSONParser();
				final String GetProfileURL = "http://bandfeed.co.uk/api/recommend.php";
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("genre", prefs.getString(
						"genre" + randomInt, "")));

				// getting JSON Object
				// Note that create product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(GetProfileURL,
						"GET", params);

				if (json != null) {
					try {
						// check log cat for response
						Log.d("Profile Response", json.toString());

						if (json.getInt("success") == 1
								|| json.getInt("success") == 2) {

							recoType = json.getInt("success");

							JSONArray profileObj = json
									.getJSONArray("bprofile");
							JSONArray membersObj = json
									.getJSONArray("bmembers");

							// get first object from JSON Array
							JSONObject profile = profileObj.getJSONObject(0);
							JSONObject members = membersObj.getJSONObject(0);

							// Putting retrieved data in Intent's extras for
							// next
							// activty
							displayProf.putExtra("band_name",
									profile.getString("band_name"));
							recommendation = profile.getString("band_name");
							displayProf.putExtra("genre1",
									profile.getString("genre1"));
							displayProf.putExtra("genre2",
									profile.getString("genre2"));
							displayProf.putExtra("genre3",
									profile.getString("genre3"));
							displayProf.putExtra("county",
									profile.getString("county"));
							displayProf.putExtra("town",
									profile.getString("town"));
							displayProf.putExtra("amountOfMembers",
									profile.getInt("amountOfMembers"));
							displayProf.putExtra("soundCloud",
									profile.getString("soundCloud"));
							displayProf.putExtra("webpage",
									profile.getString("webpage"));
							displayProf.putExtra("image",
									profile.getInt("image"));
							displayProf.putExtra("updated_at",
									profile.getString("updated_at"));
							displayProf.putExtra("created_at",
									profile.getString("created_at"));
							displayProf.putExtra("bio",
									profile.getString("bio"));
							displayProf.putExtra("followers",
									profile.getInt("followers"));

							for (int j = 0; j < profile
									.getInt("amountOfMembers"); j++) {
								displayProf.putExtra("name" + j,
										members.getString("name" + j));
								displayProf.putExtra("role" + j,
										members.getString("role" + j));
							}

							foundEntries = true;
						}
					} catch (JSONException e) {
						foundEntries = false;
					}
					connection = true;
				} else {
					connection = false;
				}

				if (connection && foundEntries) {
					rHandler.post(new Runnable() {
						public void run() {
							imageLoader.DisplayImage(recommendation, profImage);
							recommendationMade = true;
							setUpRecomImg();
						}
					});
				}
			}

		}).start();
	}

	private void setUpRecomImg() {
		profImage.setOnClickListener(this);
		if (recoType == 1) {
			reco1.setText("Recommendation to you.. ");
		} else {
			reco1.setText("Latest band to join bandFeed.. ");
		}
		reco2.setText(recommendation);
	}
}

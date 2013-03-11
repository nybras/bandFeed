/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayProfile extends Activity implements OnClickListener,
		OnLongClickListener {

	private String band_name;
	private ProgressDialog progressDialog;
	private ImageView profileImage;
	private SharedPreferences prefs;
	private TextView bioText, samplesText, webpageText, followersText,
			membersText;
	private int amountOfMembers;
	private ArrayList<String> membersOfBand, roles, bands, numOfFollowers;
	private Handler mHandler = new Handler();
	private Bundle extras;
	private ImageLoader imageLoader;
	private AppendToLog logIt = new AppendToLog();
	private ConnectToHttpAPI httpAPI = new ConnectToHttpAPI();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		extras = getIntent().getExtras();
		band_name = extras.getString("band_name");
		bands = new ArrayList<String>();
		prefs = getSharedPreferences("userPrefs", 0);
		amountOfMembers = extras.getInt("amountOfMembers");
		membersOfBand = new ArrayList<String>();
		numOfFollowers = new ArrayList<String>();
		roles = new ArrayList<String>();

		int numOfBands = prefs.getInt("numOfBands", 0);
		for (int i = 0; i < numOfBands; i++) {
			bands.add(prefs.getString("band" + i, null));
		}

		imageLoader = new ImageLoader(this);
		profileImage = (ImageView) findViewById(R.id.profile_image);
		imageLoader.DisplayImage(band_name, profileImage);
		profileImage.setOnLongClickListener(this);

		// SET TEXTVIEWS WITH TEXT AND ADD CLICK LISTENERS

		// BAND NAME
		TextView bandName = (TextView) findViewById(R.id.profile_name);
		bandName.setText(band_name);

		// MEMBERS
		TextView members = (TextView) findViewById(R.id.profile_members);
		members.setText("Members");
		members.setOnLongClickListener(this);

		membersText = (TextView) findViewById(R.id.profile_members_text);
		// Add members to ArrayLists
		for (int j = 0; j < amountOfMembers; j++) {
			membersOfBand.add(extras.getString("name" + j));
			roles.add(extras.getString("role" + j));
		}
		printNames();
		membersText.setOnLongClickListener(this);

		// BIO
		TextView bio = (TextView) findViewById(R.id.profile_biography);
		bio.setText("Biography");
		bio.setOnLongClickListener(this);

		bioText = (TextView) findViewById(R.id.profile_biography_text);
		bioText.setText(extras.getString("bio"));
		bioText.setOnLongClickListener(this);

		// GENRES
		TextView genresText = (TextView) findViewById(R.id.profile_genres);
		genresText.setText(extras.getString("genre1") + " \\ "
				+ extras.getString("genre2") + " \\ "
				+ extras.getString("genre3"));
		genresText.setOnLongClickListener(this);

		// LOCATION
		TextView location = (TextView) findViewById(R.id.profile_location);
		location.setText("Location");
		location.setOnLongClickListener(this);

		TextView locationText = (TextView) findViewById(R.id.profile_location_text);
		locationText.setText(extras.getString("town") + ", "
				+ extras.getString("county"));
		locationText.setOnLongClickListener(this);

		// UPDATED_AT
		if (extras.getString("updated_at").equals("0000-00-00 00:00:00")) {
			TextView updatedText = (TextView) findViewById(R.id.profile_updated);
			updatedText.setText("Profile last updated:" + "\n"
					+ extras.getString("created_at"));
		} else {

			TextView updatedText = (TextView) findViewById(R.id.profile_updated);
			updatedText.setText("Profile last updated:" + "\n"
					+ extras.getString("updated_at"));
		}

		// SAMPLES
		TextView samples = (TextView) findViewById(R.id.profile_samples);
		samples.setText("Samples");
		samples.setOnLongClickListener(this);

		samplesText = (TextView) findViewById(R.id.profile_samples_text);
		samplesText.setText(extras.getString("soundCloud"));
		samplesText.setOnLongClickListener(this);
		samplesText.setOnClickListener(this);

		// WEB PAGE
		TextView webpage = (TextView) findViewById(R.id.profile_webpage);
		webpage.setText("Webpage");
		webpage.setOnLongClickListener(this);

		webpageText = (TextView) findViewById(R.id.profile_webpage_text);
		webpageText.setText(extras.getString("webpage"));
		webpageText.setOnLongClickListener(this);
		webpageText.setOnClickListener(this);

		// CREATED_AT
		TextView createdText = (TextView) findViewById(R.id.profile_created);
		createdText.setText("Profile created:" + "\n"
				+ extras.getString("created_at"));

		// FOLLOWERS
		followersText = (TextView) findViewById(R.id.profile_followers);
		followersText.setText("Followers:" + "\n" + numOfFollowers.size());
		getNumOfFollowers();

		// SUBSCRIBE
		Button subscribeToAll = (Button) findViewById(R.id.profile_subscriptions);
		subscribeToAll.setOnClickListener(this);

		// Instruct user first time this activity is displayed
		if (!prefs.contains("firstDisplayMember") && !bands.contains(band_name)) {
			Toast toast = Toast
					.makeText(
							this,
							"If you play in this band long press on the members section!",
							Toast.LENGTH_LONG);
			toast.show();
			Editor editor = prefs.edit();
			editor.putString("firstDisplayMember", "yes");
			editor.commit();
		}
		// Instruct user first time this activity is displayed
		if (!prefs.contains("firstDisplayUpdate") && bands.contains(band_name)) {
			Toast toast = Toast.makeText(this,
					"Long press on any of the profile sections to update!",
					Toast.LENGTH_LONG);
			toast.show();
			Editor editor = prefs.edit();
			editor.putString("firstDisplayUpdate", "yes");
			editor.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		// The following checks to see if any updates have been made to the
		// profile recently, for example the user may have just updated
		// the biography.

		// Profile has just been deleted in updated members, execute
		// DeleteProfile()
		if (prefs.contains("delete")) {
			Editor editor = prefs.edit();
			editor.remove("delete");
			editor.commit();
			new DeleteProfile().execute();
		}
		// Bio has been updated
		if (prefs.contains("bio")) {
			bioText.setText(prefs.getString("bio", null));
			Editor editor = prefs.edit();
			editor.remove("bio");
			editor.commit();
		}
		// Samples has been updated
		if (prefs.contains("samples")) {
			samplesText.setText(prefs.getString("samples", null));
			Editor editor = prefs.edit();
			editor.remove("samples");
			editor.commit();
		}
		// Webpage has been updated
		if (prefs.contains("webpage")) {
			webpageText.setText(prefs.getString("webpage", null));
			Editor editor = prefs.edit();
			editor.remove("webpage");
			editor.commit();
		}
		// Member has removed
		if (prefs.contains("removed")) {
			amountOfMembers--;
			int index = membersOfBand.indexOf(prefs.getString("removed", null));
			membersOfBand.remove(index);
			roles.remove(index);
			printNames();
			Editor editor = prefs.edit();
			editor.remove("removed");
			editor.commit();
		}
		// Member has been added
		if (prefs.contains("added")) {
			amountOfMembers++;
			membersOfBand.add(prefs.getString("added", null));
			roles.add(prefs.getString("role", null));
			printNames();
			Editor editor = prefs.edit();
			editor.remove("added");
			editor.remove("role");
			editor.commit();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// respond to menu item selection
		switch (item.getItemId()) {
		case R.id.about:
			// About button clicked
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			// Send button clicked
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "DisplayProfile");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		// Rough pattern to check that whether a webpage or samplepage might be
		// a url. Doesn't matter if the url is correct or not, but to be viewed
		// it must be
		String pattern = "https?://.*";

		switch (v.getId()) {
		case R.id.profile_samples_text:
			if (Pattern.matches(pattern, samplesText.getText().toString())) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(samplesText
						.getText().toString()));
				startActivity(i);
			}
			break;
		case R.id.profile_webpage_text:
			if (Pattern.matches(pattern, webpageText.getText().toString())) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(webpageText
						.getText().toString()));
				startActivity(i);
			}
			break;
		case R.id.profile_subscriptions:
			new Subscriptions().execute();
		}

	}

	public boolean onLongClick(View v) {

		Intent i = null;
		if (bands.contains(band_name)) {
			// If the user is listed as being a member of the current profile
			// then allow access to update the profile's section by long
			// pressing a section
			switch (v.getId()) {
			// IMAGE
			case R.id.profile_image:
				// Alert Dialog to check whether user really wants to delete
				// profile when long pressing the profile image
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

				// set dialog message
				alertDialogBuilder
						.setMessage(
								"Would you like to delete the "
										+ band_name
										+ " profile? This will not delete your user account.")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										new DeleteProfile().execute();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
				break;
			// MEMBERS
			case R.id.profile_members_text:
				i = new Intent(this, UpdateMembers.class);
				i.putExtra("bandName", band_name);
				i.putExtra("membersOfBand", membersOfBand);
				startActivity(i);
				break;
			case R.id.profile_members:
				i = new Intent(this, UpdateMembers.class);
				i.putExtra("bandName", band_name);
				i.putExtra("membersOfBand", membersOfBand);
				startActivity(i);
				break;
			// BIOGRAPHY
			case R.id.profile_biography_text:
				i = new Intent(this, UpdateBiography.class);
				i.putExtra("bandName", band_name);
				i.putExtra("bio", bioText.getText().toString());
				startActivity(i);
				break;
			case R.id.profile_biography:
				i = new Intent(this, UpdateBiography.class);
				i.putExtra("bandName", band_name);
				i.putExtra("bio", bioText.getText().toString());
				startActivity(i);
				break;
			// SAMPLES
			case R.id.profile_samples_text:
				i = new Intent(this, UpdateSamples.class);
				i.putExtra("bandName", band_name);
				i.putExtra("samples", samplesText.getText().toString());
				startActivity(i);
				break;
			case R.id.profile_samples:
				i = new Intent(this, UpdateSamples.class);
				i.putExtra("bandName", band_name);
				i.putExtra("samples", samplesText.getText().toString());
				startActivity(i);
				break;
			// WEBPAGE
			case R.id.profile_webpage_text:
				i = new Intent(this, UpdateWebpage.class);
				i.putExtra("bandName", band_name);
				i.putExtra("webpage", webpageText.getText().toString());
				startActivity(i);
				break;
			case R.id.profile_webpage:
				i = new Intent(this, UpdateWebpage.class);
				i.putExtra("bandName", band_name);
				i.putExtra("webpage", webpageText.getText().toString());
				startActivity(i);
				break;
			}
		} else {
			// If user is not the band of this profile but would like to
			// link them self to it..
			switch (v.getId()) {
			// MEMBERS
			case R.id.profile_members_text:
				i = new Intent(this, RequestLink.class);
				i.putExtra("bandName", band_name);
				i.putExtra("membersOfBand", membersOfBand);
				// i.putExtra("amountOfMembers", amountOfMembers);
				startActivity(i);
				break;
			case R.id.profile_members:
				i = new Intent(this, RequestLink.class);
				i.putExtra("bandName", band_name);
				i.putExtra("membersOfBand", membersOfBand);
				// i.putExtra("amountOfMembers", amountOfMembers);
				startActivity(i);
				break;

			}
		}
		return false;

	}

	class Subscriptions extends AsyncTask<String, String, String> {

		// Checks the user's current subscriptions of the current profile

		ArrayList<String> subs = new ArrayList<String>();
		private boolean connection = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(DisplayProfile.this);
			progressDialog.setMessage("Checking current Subscriptions..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			// URL is not fixed and consists of the band name of the profile
			// being displayed
			// This URL checks for bindings between username and bandname
			String request = "/api/bindings/%2f/e/" + band_name + "/q/"
					+ prefs.getString("userName", null);
			request = request.replace(" ", "%20");

			// Get subscriptions
			// Uses the following class to connect to the rabbitMQ HTTP API
			String result = httpAPI.connect(band_name, request);

			if (result != null) {
				Pattern pattern = Pattern
						.compile("\"routing_key\":\"([a-zA-Z0-9\\s]+)\"");
				// Use a regex to pick out the routing_keys
				Matcher matcher = pattern.matcher(result);
				while (matcher.find()) {
					subs.add(matcher.group(1));
				}
				connection = true;
			} else {
				connection = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
			if (connection) {
				// Made a connection successfully
				Intent i = new Intent(DisplayProfile.this, Subscribe.class);
				// Add the current subscriptions (if any)
				i.putExtra("subs", subs);
				i.putExtra("bandName", band_name);
				startActivity(i);
			} else {
				informUser("No internet connection!");
			}

		}
	}

	private void getNumOfFollowers() {
		new Thread(new Runnable() {
			public void run() {

				// Gets the amount of subscribers in background thread
				// Not the most important feature, so if it fails to return the
				// amount of subscribers then so be it
				String request = "/api/exchanges/%2f/" + band_name
						+ "/bindings/source";
				request = request.replace(" ", "%20");

				// Get followers
				String result = httpAPI.connect(band_name, request);

				if (result != null) {
					Pattern pattern = Pattern
							.compile("\"destination\":\"([a-zA-Z0-9\\s]+)\"");
					// Use a regex to pick out the bindings
					Matcher matcher = pattern.matcher(result);
					while (matcher.find()) {
						if (!numOfFollowers.contains(matcher.group(1))) {
							numOfFollowers.add(matcher.group(1));
						}
					}
				}

				mHandler.post(new Runnable() {
					public void run() {
						followersText.setText("Followers:" + "\n"
								+ numOfFollowers.size());

					}
				});
			}

		}).start();
	}

	private void printNames() {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < amountOfMembers; j++) {
			sb.append(membersOfBand.get(j) + " - " + roles.get(j));
			if (j < amountOfMembers - 1) {
				sb.append("\n");
			}
		}
		membersText.setText(sb);
	}

	public class DeleteProfile extends AsyncTask<String, String, String> {

		private static final String DeleteProfileURL = "http://bandfeed.co.uk/api/delete_profile.php";
		JSONParser jsonParser = new JSONParser();

		private boolean connection = false;
		private boolean profileDeleted = false;
		private ProgressDialog progressDialog = new ProgressDialog(
				DisplayProfile.this);

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog.setMessage("Deleting Profile..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		/**
		 * Creating profile
		 * */
		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", band_name.toString()));

			JSONObject json = jsonParser.makeHttpRequest(DeleteProfileURL,
					"POST", params);

			if (json != null) {

				// check log cat for response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {

					if (json.getInt("success") == 1) {
						// successfully created profile
						logIt.append(band_name + " DELETED BAND PROFILE");

						profileDeleted = true;  //Deleted at database
						
						// Delete EXCHANGE
						ConnectToRabbitMQ connection = new ConnectToRabbitMQ(
								band_name.toString(), null);
						if (connection.deleteExchange()) {
							// connection has been made and exchange deleted
							connection.dispose();

							//Remove band from shared prefs
							Editor editor = prefs.edit();
							for (int i = 0; i < bands.size(); i++) {
								if (prefs.getString("band" + i, null).equals(
										band_name)) {
									editor.remove("band" + i);
								}
							}
							editor.putInt("numOfBands", bands.size() - 1);
							editor.commit();

						} else {
							//Failed to delete exchange
							//Never mind exchanges die after 1 year of inactivity
							logIt.append(band_name
									+ " FAILED TO DELETE BAND EXCHANGE");
						}
					} else {
						//Failed to delete band at database
						profileDeleted = false;
						logIt.append(band_name
								+ " FAILED TO DELETE BAND PROFILE AT DATABASE");
					}

					//Delete band image
					DeleteImage delImg = new DeleteImage();
					if (delImg.makeConnection(band_name) == null) {
						logIt.append(band_name
								+ " FAILED TO DELETE IMAGE");
					}
				} catch (JSONException e) {
					profileDeleted = false;
				}
				connection = true;
			}
			else {
				connection = false;
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();

			if (connection) {
				if (profileDeleted == false) {
					informUser("Failed to delete profile, please try again later!");
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(i);
				} else {
					informUser("Profile successfully deleted!");
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					finish();
					startActivity(i);
				}
			} else {
				Toast toast = Toast.makeText(DisplayProfile.this,
						"No internet connection!",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void informUser(String msg) {

		Toast toast = Toast.makeText(DisplayProfile.this, msg,
				Toast.LENGTH_SHORT);
		toast.show();
	}
}

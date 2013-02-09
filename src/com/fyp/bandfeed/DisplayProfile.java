package com.fyp.bandfeed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
	private Bitmap bitmap = null;
	private SharedPreferences prefs;
	private ArrayList<String> bands;
	private TextView bioText, samplesText, webpageText;
	private int amountOfMembers;
	private String[] membersOfBand;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		Bundle extras = getIntent().getExtras();
		band_name = extras.getString("band_name");
		bands = new ArrayList<String>();
		prefs = getSharedPreferences("userPrefs", 0);
		amountOfMembers = extras.getInt("amountOfMembers");
		membersOfBand = new String[amountOfMembers];

		int numOfBands = prefs.getInt("numOfBands", 0);
		for (int i = 0; i < numOfBands; i++) {
			bands.add(prefs.getString("band" + i, null));
		}

		if (extras.getInt("image") == 1) {
			profileImage = (ImageView) findViewById(R.id.profile_image);
			getImage();
		}

		// BAND NAME
		TextView bandName = (TextView) findViewById(R.id.profile_name);
		bandName.setText(band_name);

		// MEMBERS
		TextView members = (TextView) findViewById(R.id.profile_members);
		members.setText("Members");
		members.setOnLongClickListener(this);

		TextView membersText = (TextView) findViewById(R.id.profile_members_text);
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < amountOfMembers; j++) {
			membersOfBand[j] = extras.getString("name" + j);
			sb.append(extras.getString("name" + j) + " - "
					+ extras.getString("role" + j));
			if (j < amountOfMembers - 1) {
				sb.append("\n");
			}

		}
		membersText.setText(sb);
		membersText.setOnLongClickListener(this);

		// BIO
		TextView bio = (TextView) findViewById(R.id.profile_biography);
		bio.setText("Biography");
		bio.setOnLongClickListener(this);

		bioText = (TextView) findViewById(R.id.profile_biography_text);
		bioText.setText(extras.getString("bio"));
		bioText.setOnLongClickListener(this);

		// DISCOGRAPHY - TO BE WORKED ON LATER
		// TODO
		// TextView discog = (TextView) findViewById(R.id.profile_discography);
		// discog.setText("Discography");
		// discog.setOnLongClickListener(this);
		//
		// TextView discogText = (TextView)
		// findViewById(R.id.profile_discography_text);
		// discogText.setText("Not available");
		// discogText.setOnLongClickListener(this);

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

		// GIGS - TO BE WORKED ON LATER
		// TODO
		// TextView gigs = (TextView) findViewById(R.id.profile_gigs);
		// gigs.setText("Gigs");
		// gigs.setOnLongClickListener(this);
		//
		// TextView gigsText = (TextView) findViewById(R.id.profile_gigs_text);
		// gigsText.setText("Not available");
		// gigsText.setOnLongClickListener(this);

		// SAMPLES
		TextView samples = (TextView) findViewById(R.id.profile_samples);
		samples.setText("Samples");
		samples.setOnLongClickListener(this);

		samplesText = (TextView) findViewById(R.id.profile_samples_text);
		samplesText.setText(extras.getString("soundCloud"));
		samplesText.setOnLongClickListener(this);

		// WEB PAGE
		TextView webpage = (TextView) findViewById(R.id.profile_webpage);
		webpage.setText("Webpage");
		webpage.setOnLongClickListener(this);

		webpageText = (TextView) findViewById(R.id.profile_webpage_text);
		webpageText.setText(extras.getString("webpage"));
		webpageText.setOnLongClickListener(this);

		// CREATED_AT
		TextView createdText = (TextView) findViewById(R.id.profile_created);
		createdText.setText("Profile created:" + "\n"
				+ extras.getString("created_at"));

		TextView followersText = (TextView) findViewById(R.id.profile_followers);
		followersText.setText("Followers:" + "\n" + extras.getInt("followers"));

		// SUBSCRIBE
		Button subscribeToAll = (Button) findViewById(R.id.profile_subscriptions);
		subscribeToAll.setOnClickListener(this);

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
		// TODO Needs the rest completed!
		if (prefs.contains("bio")) {
			bioText.setText(prefs.getString("bio", null));
			Editor editor = prefs.edit();
			editor.remove("bio");
			editor.commit();
		}
		if (prefs.contains("samples")) {
			samplesText.setText(prefs.getString("samples", null));
			Editor editor = prefs.edit();
			editor.remove("samples");
			editor.commit();
		}
		if (prefs.contains("webpage")) {
			webpageText.setText(prefs.getString("webpage", null));
			Editor editor = prefs.edit();
			editor.remove("webpage");
			editor.commit();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// respond to menu item selection
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "DisplayProfile");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {

		new Subscriptions().execute();

	}

	private void getImage() {
		new Thread(new Runnable() {
			public void run() {

				try {

					URL url = new URL("http://bandfeed.co.uk/images/"
							+ band_name + ".jpg");
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setDoInput(true);
					connection.connect();
					InputStream input = connection.getInputStream();
					bitmap = BitmapFactory.decodeStream(input);

				} catch (IOException e) {
					e.printStackTrace();
					// TODO
				}

				profileImage.post(new Runnable() {
					public void run() {
						profileImage.setImageBitmap(bitmap);
					}
				});
			}
		}).start();
	}

	public boolean onLongClick(View v) {

		Intent i = null;
		if (bands.contains(band_name)) {

			switch (v.getId()) {
			// MEMBERS
			case R.id.profile_members_text:
				// TODO create appropriate update profile section for this
				break;
			case R.id.profile_members:
				// TODO create appropriate update profile section for this
				break;
			// BIOGRAPHY
			case R.id.profile_biography_text:
				i = new Intent(this, UpdateBiography.class);
				i.putExtra("bandName", band_name);
				startActivity(i);
				break;
			case R.id.profile_biography:
				i = new Intent(this, UpdateBiography.class);
				i.putExtra("bandName", band_name);
				startActivity(i);
				break;
			// DISCOGRAPHY
			// case R.id.profile_discography_text:
			// section = "discography";
			// break;
			// GENRES (only one listener)
			case R.id.profile_genres:
				// TODO create appropriate update profile section for this
				break;
			// LOCATION
			case R.id.profile_location_text:
				// TODO create appropriate update profile section for this
				break;
			case R.id.profile_location:
				// TODO create appropriate update profile section for this
				break;
			// case R.id.profile_gigs_text:
			// section = "gigs";
			// break;
			// SAMPLES
			case R.id.profile_samples_text:
				i = new Intent(this, UpdateSamples.class);
				i.putExtra("bandName", band_name);
				startActivity(i);
				break;
			case R.id.profile_samples:
				i = new Intent(this, UpdateSamples.class);
				i.putExtra("bandName", band_name);
				startActivity(i);
				break;
			// WEBPAGE
			case R.id.profile_webpage_text:
				i = new Intent(this, UpdateWebpage.class);
				i.putExtra("bandName", band_name);
				startActivity(i);
				break;
			case R.id.profile_webpage:
				i = new Intent(this, UpdateWebpage.class);
				i.putExtra("bandName", band_name);
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
				//i.putExtra("amountOfMembers", amountOfMembers);
				startActivity(i);
				break;
			case R.id.profile_members:
				i = new Intent(this, RequestLink.class);
				i.putExtra("bandName", band_name);
				i.putExtra("membersOfBand", membersOfBand);
				//i.putExtra("amountOfMembers", amountOfMembers);
				startActivity(i);
				break;
			}
		}
		return false;
	}

	class Subscriptions extends AsyncTask<String, String, String> {

		ArrayList<String> subs = new ArrayList<String>();
		private boolean subscriptions = false;

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

			// http://stackoverflow.com/questions/10647631/rabbitmq-http-api-request-unauthorized

			InputStream is = null;
			String result = null;

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpHost targetHost = new HttpHost("81.169.135.67", 55672, "http");

			HttpGet request = new HttpGet("/api/bindings/%2f/e/" + band_name
					+ "/q/" + prefs.getString("userName", null));

			httpClient.getCredentialsProvider().setCredentials(
					new AuthScope(targetHost.getHostName(),
							targetHost.getPort()),
					new UsernamePasswordCredentials("admin", "prrpm5uBbf"));

			request.setHeader("Accept", "application/json");
			request.setHeader("Content-Type", "application/json");

			try {
				HttpResponse response = httpClient.execute(targetHost, request);
				HttpEntity httpEntity = response.getEntity();
				is = httpEntity.getContent();

			} catch (ClientProtocolException e) {
				Log.e("ClientProtocol",
						"Error with credentials " + e.toString());
				subscriptions = false;
				return null;
			} catch (IOException e) {
				Log.e("IOException Error",
						"Error making http response " + e.toString());
				subscriptions = false;
				return null;
			}

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				result = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
				subscriptions = false;
				return null;
			}

			Pattern pattern = Pattern.compile("\"routing_key\":\"([a-z]+)\"");
			Matcher matcher = pattern.matcher(result);
			while (matcher.find()) {
				subs.add(matcher.group(1));
			}
			subscriptions = true;

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
			if (subscriptions) {
				Intent i = new Intent(DisplayProfile.this, Subscribe.class);
				i.putExtra("subs", subs);
				i.putExtra("bandName", band_name);
				startActivity(i);
			} else {
				informUser();
			}

		}
	}

	public void informUser() {

		Toast toast = Toast.makeText(this,
				"Failed to check subscriptions, try again later",
				Toast.LENGTH_SHORT);
		toast.show();
	}
}

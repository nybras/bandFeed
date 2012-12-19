package com.fyp.bandfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayProfile extends Activity implements OnClickListener {

	private String band_name;
	private boolean subscriptionCreated;
	private ProgressDialog progressDialog;
	private ImageView profileImage;
	private Bitmap bitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);

		Bundle extras = getIntent().getExtras();
		band_name = extras.getString("band_name");

		if (extras.getInt("image") == 1) {
			profileImage = (ImageView) findViewById(R.id.profile_image);
			getImage();

		}

		TextView bandName = (TextView) findViewById(R.id.profile_name);
		bandName.setText(band_name);

		TextView members = (TextView) findViewById(R.id.profile_members);
		members.setText("Members");

		TextView membersText = (TextView) findViewById(R.id.profile_members_text);
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < extras.getInt("amountOfMembers"); j++) {
			sb.append(extras.getString("name" + j) + " - "
					+ extras.getString("role" + j) + "\n");
		}
		membersText.setText(sb);

		TextView bio = (TextView) findViewById(R.id.profile_biography);
		bio.setText("Biography");

		TextView bioText = (TextView) findViewById(R.id.profile_biography_text);
		bioText.setText(extras.getString("bio"));

		TextView discog = (TextView) findViewById(R.id.profile_discography);
		discog.setText("Discography");

		TextView discogText = (TextView) findViewById(R.id.profile_discography_text);
		discogText.setText("Not available");

		TextView genresText = (TextView) findViewById(R.id.profile_genres);
		genresText.setText(extras.getString("genre1") + " \\ "
				+ extras.getString("genre2") + " \\ "
				+ extras.getString("genre3"));

		TextView location = (TextView) findViewById(R.id.profile_location);
		location.setText("Location");

		TextView locationText = (TextView) findViewById(R.id.profile_location_text);
		locationText.setText(extras.getString("town") + ", "
				+ extras.getString("county"));

		if (extras.getString("updated_at").equals("0000-00-00 00:00:00")) {
			TextView updatedText = (TextView) findViewById(R.id.profile_updated);
			updatedText.setText("Profile last updated:" + "\n"
					+ extras.getString("created_at"));
		} else {

			TextView updatedText = (TextView) findViewById(R.id.profile_updated);
			updatedText.setText("Profile last updated:" + "\n"
					+ extras.getString("updated_at"));
		}

		TextView gigs = (TextView) findViewById(R.id.profile_gigs);
		gigs.setText("Gigs");

		TextView gigsText = (TextView) findViewById(R.id.profile_gigs_text);
		gigsText.setText("Not available");

		TextView samples = (TextView) findViewById(R.id.profile_samples);
		samples.setText("Samples");

		TextView samplesText = (TextView) findViewById(R.id.profile_samples_text);
		samplesText.setText(extras.getString("soundCloud"));

		TextView webpage = (TextView) findViewById(R.id.profile_webpage);
		webpage.setText("Webpage");

		TextView webpageText = (TextView) findViewById(R.id.profile_webpage_text);
		webpageText.setText(extras.getString("webpage"));
		
		TextView createdText = (TextView) findViewById(R.id.profile_created);
		createdText.setText("Profile created:" + "\n"
				+ extras.getString("created_at"));
		
		TextView followersText = (TextView) findViewById(R.id.profile_followers);
		followersText.setText("Followers:" + "\n"
				+ extras.getInt("followers"));

		Button subscribeToAll = (Button) findViewById(R.id.profile_sub_toall);
		subscribeToAll.setOnClickListener(this);

		subscriptionCreated = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_display_profile, menu);
		return true;
	}

	public void onClick(View v) {

		new CreateSubscription().execute();

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

	class CreateSubscription extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(DisplayProfile.this);
			progressDialog.setMessage("Subscribing..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			
			SharedPreferences prefs = getSharedPreferences("userPrefs", 0);
			String username = prefs.getString("userName", null);

			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(band_name,
					username.trim());
			if (connection.createBind()) {
				connection.dispose();
				subscriptionCreated = true;
			} else {
				subscriptionCreated = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
			informUserSubscript();
		}
	}

	public void informUserSubscript() {
		String msg;
		if (subscriptionCreated) {
			msg = "Subscription created";
		} else {
			msg = "Subscription failed";
		}

		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}

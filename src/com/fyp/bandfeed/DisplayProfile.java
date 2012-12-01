package com.fyp.bandfeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayProfile extends Activity implements OnClickListener {

	private String band_name;
	private boolean subscriptionCreated;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_profile);

		Bundle extras = getIntent().getExtras();
		band_name = extras.getString("band_name");

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
		samplesText.setText("Not available yet, see soundcloud.com\\"
				+ extras.getString("soundc_link"));

		TextView createdText = (TextView) findViewById(R.id.profile_created);
		createdText.setText("Profile created:" + "\n"
				+ extras.getString("created_at"));

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

			String username = "";
			String line;

			String path = getFilesDir().getAbsolutePath() + File.separator
					+ "User" + File.separator + "User";

			try {

				FileInputStream fin = new FileInputStream(path + ".profile");

				// prepare the file for reading
				InputStreamReader inputreader = new InputStreamReader(fin);
				BufferedReader buffreader = new BufferedReader(inputreader);

				// read every line of the file into the line-variable, on line
				// at the time
				while ((line = buffreader.readLine()) != null) {
					// do something with the settings from the file
					username += line;
				}

				// close the file again
				fin.close();
			} catch (java.io.FileNotFoundException e) {
				// do something if the myfilename.txt does not exits
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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

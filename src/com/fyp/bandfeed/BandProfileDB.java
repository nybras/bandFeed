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
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class BandProfileDB extends Activity implements OnClickListener {
	private SparseArray<String> ids; // Instead of HashMap
	private ProgressDialog progressDialog;
	private String bandName;
	private boolean subscriptionCreated;

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

		Bundle extras = getIntent().getExtras();
		bandName = extras.getString("band_name");
		StringBuilder sb = new StringBuilder();
		sb.append(bandName + "\n");
		sb.append(extras.getString("genre1") + "\n");
		sb.append(extras.getString("genre2") + "\n");
		sb.append(extras.getString("genre3") + "\n");
		sb.append(extras.getString("county") + "\n");
		sb.append(extras.getString("town") + "\n");
		sb.append(extras.getString("soundc_link") + "\n");
		sb.append(extras.getString("pic_link") + "\n");
		sb.append(extras.getString("updated_at") + "\n");
		sb.append(extras.getString("created_at") + "\n");

		TextView profileTextView = new TextView(this);
		profileTextView.setText(sb);
		profileTextView.setGravity(Gravity.CENTER);
		linearLayout.addView(profileTextView);

		Button subButton = new Button(this);
		// button to next activity
		Integer id = findId(1);
		subButton.setId(id);
		ids.put(id, "subButton");
		subButton.setText("Subscribe");
		subButton.setOnClickListener(this);
		linearLayout.addView(subButton);
		
		subscriptionCreated = false;

	}

	private int findId(int id) {
		// create an ID
		View v = findViewById(id);
		while (v != null) {
			v = findViewById(++id);
		}
		return id++;
	}

	public void informUserSubscript() {
		String msg;
		if(subscriptionCreated) {
			msg = "Subscription created";
		}
		else {
			msg = "Subscription failed";
		}
		
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_band_profile_db, menu);
		return true;
	}

	public void onClick(View v) {
		if (ids.get(v.getId()).equals("subButton")) {
			new CreateSubscription().execute();	
		}
	}
	
	class CreateSubscription extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(BandProfileDB.this);
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
					username += line + "\n";
				}

				// close the file again
				fin.close();
			} catch (java.io.FileNotFoundException e) {
				// do something if the myfilename.txt does not exits
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName, username.trim());
			if (connection.createBind()) {
				connection.dispose();
				subscriptionCreated = true;
			}
			else {
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
}

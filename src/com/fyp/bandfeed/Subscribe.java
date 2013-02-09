package com.fyp.bandfeed;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Subscribe extends Activity implements OnClickListener {

	private CheckBox cNews, cGigs, cReleases, cUpdates;
	// private boolean news, gigs, releases, updates = false;
	private ProgressDialog progressDialog;
	private String bandName;
	private ArrayList<String> subs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscribe);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		Bundle extras = getIntent().getExtras();
		subs = extras.getStringArrayList("subs");
		bandName = extras.getString("bandName");

		cNews = (CheckBox) findViewById(R.id.checkNews);
		cNews.setChecked(false);
		cGigs = (CheckBox) findViewById(R.id.checkGigs);
		cGigs.setChecked(false);
		cReleases = (CheckBox) findViewById(R.id.checkReleases);
		cReleases.setChecked(false);
		cUpdates = (CheckBox) findViewById(R.id.checkUpdates);
		cUpdates.setChecked(false);

		for (String st : subs) {
			if (st.equals("news")) {
				cNews.setChecked(true);
			}
			if (st.equals("gigs")) {
				cGigs.setChecked(true);
			}
			if (st.equals("releases")) {
				cReleases.setChecked(true);
			}
			if (st.equals("updates")) {
				cUpdates.setChecked(true);
			}
		}

		cNews.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
		});

		cGigs.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
		});

		cReleases.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
		});

		cUpdates.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
		});

		Button subscribe = (Button) findViewById(R.id.subscribe_button);
		subscribe.setOnClickListener(this);

		Button cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}

	class EditSubscriptions extends AsyncTask<String, String, String> {

		private boolean newsSubCreated, gigsSubCreated, releasesSubCreated,
				updatesSubCreated;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Subscribe.this);
			progressDialog.setMessage("Editing Subscriptions..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			SharedPreferences prefs = getSharedPreferences("userPrefs", 0);
			String username = prefs.getString("userName", null);

			// NEWS
			if (cNews.isChecked() && !subs.contains("news")) {
				// if news has been selected and wasn't already selected, then
				// create bind
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.createBind("news")) {
					connection.dispose();
					newsSubCreated = true;
				} else {
					newsSubCreated = false;
				}
			} else if (!cNews.isChecked() && subs.contains("news")) {
				// if new is unticked but was previously ticked then delete bind
				// Unbind!
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.deleteBind("news")) {
					connection.dispose();
					newsSubCreated = true;
				} else {
					newsSubCreated = false;
				}
			} else {
				// Do nothing, everything is fine!
				newsSubCreated = true;
			}

			// GIGS
			if (cGigs.isChecked() && !subs.contains("gigs")) {
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.createBind("gigs")) {
					connection.dispose();
					gigsSubCreated = true;
				} else {
					gigsSubCreated = false;
				}
			} else if (!cGigs.isChecked() && subs.contains("gigs")) {
				// Unbind!
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.deleteBind("gigs")) {
					connection.dispose();
					gigsSubCreated = true;
				} else {
					gigsSubCreated = false;
				}
			} else {
				// Do nothing, everything is fine!
				gigsSubCreated = true;
			}

			// RELEASES
			if (cReleases.isChecked() && !subs.contains("releases")) {
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.createBind("releases")) {
					connection.dispose();
					releasesSubCreated = true;
				} else {
					releasesSubCreated = false;
				}
			} else if (!cReleases.isChecked() && subs.contains("releases")) {
				// Unbind!
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.deleteBind("releases")) {
					connection.dispose();
					releasesSubCreated = true;
				} else {
					releasesSubCreated = false;
				}
			} else {
				// Do nothing, everything is fine!
				releasesSubCreated = true;
			}

			// UPDATES
			if (cUpdates.isChecked() && !subs.contains("updates")) {
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.createBind("updates")) {
					connection.dispose();
					updatesSubCreated = true;
				} else {
					updatesSubCreated = false;
				}
			} else if (!cUpdates.isChecked() && subs.contains("updates")) {
				// Unbind!
				ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName,
						username.trim());
				if (connection.deleteBind("updates")) {
					connection.dispose();
					updatesSubCreated = true;
				} else {
					updatesSubCreated = false;
				}
			} else {
				// Do nothing, everything is fine!
				updatesSubCreated = true;
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();

			if (newsSubCreated && gigsSubCreated && releasesSubCreated
					&& updatesSubCreated) {
				informUserSubscript("Subscriptions successfully changed!");
				Subscribe.this.finish();
			} else {
				informUserSubscript("Failed to edit Subscriptions, try again later!");
			}
		}
	}

	public void informUserSubscript(String msg) {

		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.subscribe_button:
			new EditSubscriptions().execute();
			break;
		case R.id.cancel_button:
			finish();
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
			i.putExtra("page", "Subscribe");
			startActivity(i);
			return true;
		case R.id.log_out:
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}

/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ResultsFromSearch extends Activity {

	private Bundle extras;
	private ProgressDialog progressDialog;
	private String profile;
	private ListView list;
	private LazyAdapter adapter;
	private ArrayList<Entry> listOfEntries;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_feed);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		listOfEntries = new ArrayList<Entry>();
		// logIt = new AppendToLog();
		list = (ListView) findViewById(R.id.listView1);
		adapter = new LazyAdapter(this, null, listOfEntries);
		list.setAdapter(adapter);
		extras = getIntent().getExtras();

		for (int i = 0; i < extras.getInt("numOfReturns"); i++) {
			Entry entry = new Entry();
			entry.setBandName(extras.getString("band_name" + i));
			entry.setGenre1(extras.getString("genre1-" + i));
			entry.setGenre2(extras.getString("genre2-" + i));
			entry.setGenre3(extras.getString("genre3-" + i));
			listOfEntries.add(entry);
		}

		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				profile = listOfEntries.get(position).getBandName();
				new OpenProfile().execute();

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// respond to menu item selection
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "ResultsFromSearch");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class OpenProfile extends AsyncTask<String, String, String> {

		JSONParser jsonParser = new JSONParser();
		private static final String GetProfileURL = "http://bandfeed.co.uk/api/read_profile.php";
		private boolean connection = false;
		private Intent i = new Intent(getApplicationContext(),
				DisplayProfile.class);
		private boolean foundEntries = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ResultsFromSearch.this);
			progressDialog.setMessage("Loading Profile..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", profile));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(GetProfileURL, "GET",
					params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Profile Response", json.toString());

					if (json.getInt("success") == 1) {

						JSONArray profileObj = json.getJSONArray("bprofile");
						JSONArray membersObj = json.getJSONArray("bmembers");

						// get first object from JSON Array
						JSONObject profile = profileObj.getJSONObject(0);
						JSONObject members = membersObj.getJSONObject(0);

						// Putting retrieved data in Intent's extras for next
						// activty
						i.putExtra("band_name", profile.getString("band_name"));
						i.putExtra("genre1", profile.getString("genre1"));
						i.putExtra("genre2", profile.getString("genre2"));
						i.putExtra("genre3", profile.getString("genre3"));
						i.putExtra("county", profile.getString("county"));
						i.putExtra("town", profile.getString("town"));
						i.putExtra("amountOfMembers",
								profile.getInt("amountOfMembers"));
						i.putExtra("soundCloud",
								profile.getString("soundCloud"));
						i.putExtra("webpage", profile.getString("webpage"));
						i.putExtra("image", profile.getInt("image"));
						i.putExtra("updated_at",
								profile.getString("updated_at"));
						i.putExtra("created_at",
								profile.getString("created_at"));
						i.putExtra("bio", profile.getString("bio"));
						i.putExtra("followers", profile.getInt("followers"));

						for (int j = 0; j < profile.getInt("amountOfMembers"); j++) {
							i.putExtra("name" + j,
									members.getString("name" + j));
							i.putExtra("role" + j,
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
				if (foundEntries) {
					startActivity(i);
				} else {
					informUser("Can't open profile, try again later!");
				}
			} else {
				informUser("No internet connection!");
			}
		}
	}

	private void informUser(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}

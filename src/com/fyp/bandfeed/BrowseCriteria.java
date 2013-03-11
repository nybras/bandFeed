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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class BrowseCriteria extends Activity implements OnClickListener {

	private EditText searchBandName;
	private AutoCompleteTextView textView;
	private ProgressDialog progressDialog;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		View findResults = findViewById(R.id.search_button);
		findResults.setOnClickListener(this);

		prefs = getSharedPreferences("userPrefs", 0);
		searchBandName = (EditText) findViewById(R.id.search_band_name_edit);

		Genres g = new Genres();
		// Add genres to list
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		textView = (AutoCompleteTextView) findViewById(R.id.genres_search);
		textView.setAdapter(adapter);

		// Instruct user the first time of viewing activity
		if (!prefs.contains("firstBrowse")) {
			Toast toast = Toast
					.makeText(
							this,
							"Just go ahead and click Search if you want to view all bands!",
							Toast.LENGTH_LONG);
			toast.show();
			Editor editor = prefs.edit();
			editor.putString("firstBrowse", "yes");
			editor.commit();
		}

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
			// FeedBack button clicked
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			// Feedback button clicked
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "BrowseCriteria");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		new SearchForProfiles().execute();
	}

	class SearchForProfiles extends AsyncTask<String, String, String> {

		JSONParser jsonParser = new JSONParser();
		private static final String GetProfileNames = "http://bandfeed.co.uk/api/read_names.php";
		private boolean connection = false;
		private boolean foundEntries = false;
		Intent i = new Intent(getApplicationContext(), ResultsFromSearch.class);

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(BrowseCriteria.this);
			progressDialog.setMessage("Searching..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", searchBandName
					.getText().toString().trim()));
			params.add(new BasicNameValuePair("genre", textView.getText()
					.toString().trim()));

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(GetProfileNames,
					"GET", params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Profile Response", json.toString());

					if (json.getInt("success") == 1) {

						JSONArray profileObj = json.getJSONArray("names");

						// The bands found are added to the intents extras
						i.putExtra("success", true);
						i.putExtra("numOfReturns", profileObj.length());
						for (int num = 0; num < profileObj.length(); num++) {
							// get first object from JSON Array
							JSONObject profile = profileObj.getJSONObject(num);
							i.putExtra("band_name" + num,
									profile.getString("band_name"));
							i.putExtra("genre1-" + num,
									profile.getString("genre1"));
							i.putExtra("genre2-" + num,
									profile.getString("genre2"));
							i.putExtra("genre3-" + num,
									profile.getString("genre3"));

							// Found at least one band which fitted the user
							// search criteria
							foundEntries = true;
						}
					} else {
						// No bands found!
						foundEntries = false;
					}
				} catch (JSONException e) {
					// JSON exception
					foundEntries = false;
				}
				// Connection was successful regardless of whether it
				// returned any bands
				connection = true;
			} else {
				// No JSON object returned
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

			if (!connection) {
				// No connection so inform user
				Toast toast = Toast.makeText(BrowseCriteria.this,
						"No internet connection!", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				if (foundEntries) {
					// Found at least one band so start activity
					startActivity(i);
				} else {
					// No bands found
					Toast toast = Toast.makeText(BrowseCriteria.this,
							"No profiles match.. Try a new search!",
							Toast.LENGTH_LONG);
					toast.show();
				}

			}
		}

	}
}

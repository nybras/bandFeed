package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class BrowseCriteria extends Activity implements OnClickListener {

	private EditText searchBandName;

	private ProgressDialog progressDialog;

	// url to create new profile
	private static String GetProfileNames = "http://bandfeed.co.uk/api/read_names.php";

	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		View findResults = findViewById(R.id.search_button);
		findResults.setOnClickListener(this);

		searchBandName = (EditText) findViewById(R.id.search_band_name_edit);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	public void onClick(View v) {
		new SearchForProfiles().execute();
	}

	class SearchForProfiles extends AsyncTask<String, String, String> {

		JSONParser jsonParser = new JSONParser();

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

		/**
		 * Creating product
		 * */
		@Override
		protected String doInBackground(String... args) {

			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("band_name", searchBandName
						.getText().toString().trim()));

				// getting JSON Object
				// Note that create product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(GetProfileNames,
						"GET", params);

				// TODO NEED TO CREATE A PHP API THAN ONLY RETEIVES NAMES AND
				// GENRES OR WHATEVER DEPENDING ON SEARCH CRITERIA
				// TODO NEED TO CREATE A PHP API THAN ONLY RETEIVES NAMES AND
				// GENRES OR WHATEVER DEPENDING ON SEARCH CRITERIA
				// TODO NEED TO CREATE A PHP API THAN ONLY RETEIVES NAMES AND
				// GENRES OR WHATEVER DEPENDING ON SEARCH CRITERIA
				// e.g. need to change the getProfileURL to the new api!!

				// check log cat for response
				Log.d("Profile Response", json.toString());

				// check for success tag

				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully received product details
					JSONArray profileObj = json.getJSONArray("names"); // JSON
																			// array

					
					
					

					Intent i = new Intent(getApplicationContext(),
							ResultsFromSearch.class);

					// TODO Display returned results from search query
					i.putExtra("success", true); 
					i.putExtra("numOfReturns", profileObj.length());					
					for (int num = 0; num < profileObj.length(); num++ ) {
						// get first object from JSON Array
						JSONObject profile = profileObj.getJSONObject(num);
					i.putExtra("band_name" + num, profile.getString("band_name"));
					}
					
					startActivity(i);

				} else {
					// failed to find a profile
					Intent i = new Intent(getApplicationContext(),
							ResultsFromSearch.class);
					i.putExtra("success", false);
					// TODO Needs "can't find a profile"!
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
		}

	}
}

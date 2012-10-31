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
import android.util.SparseArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class SearchResults extends Activity implements OnClickListener {

	private Bundle extras;
	private SparseArray<String> ids;
	private ProgressDialog progressDialog;
	private String profile;
	
	// url to create new profile
	private static String GetProfileURL = "http://bandfeed.co.uk/api/read_profile.php";
	// private static String CreateProfileURL =
	// "http://129.168.0.3:3401/bandFeed/api/create_profile.php";

	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);

		ids = new SparseArray<String>();
		extras = getIntent().getExtras();

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

		if (extras.getBoolean("success")) {
			String bandName = extras.getString("band_name");
			Button profileButton = new Button(this);
			// button to next activity
			Integer id = findId(1);
			profileButton.setId(id);
			ids.put(id, bandName);
			profileButton.setText(bandName);
			profileButton.setOnClickListener(this);
			linearLayout.addView(profileButton);
		} else {
			TextView noProfileText = new TextView(this);
			noProfileText.setText("No profiles match..");
			linearLayout.addView(noProfileText);
		}
	}

	private int findId(int id) {
		// create an ID
		View v = findViewById(id);
		while (v != null) {
			v = findViewById(++id);
		}
		return id++;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_search_results, menu);
		return true;
	}

	public void onClick(View v) {
//		Intent i = null;
//		i = new Intent(this, BandProfileDB.class);
//		i.putExtra("profile", ids.get(v.getId()));
		profile = ids.get(v.getId());
		new OpenProfile().execute();
		
	}

	class OpenProfile extends AsyncTask<String, String, String> {

		JSONParser jsonParser = new JSONParser();

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(SearchResults.this);
			progressDialog.setMessage("Loading profile..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		
		@Override
		protected String doInBackground(String... args) {

			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("band_name", profile));

				// getting JSON Object
				// Note that create product url accepts POST method
				JSONObject json = jsonParser.makeHttpRequest(GetProfileURL,
						"GET", params);

				// check log cat for response
				Log.d("Profile Response", json.toString());

				// check for success tag

				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully received product details
					JSONArray profileObj = json.getJSONArray("bprofile"); // JSON
																			// Array

					// get first object from JSON Array
					JSONObject profile = profileObj.getJSONObject(0);

					Intent i = new Intent(getApplicationContext(),
							BandProfileDB.class);

					// TODO Display returned results from search query
					i.putExtra("band_name", profile.getString("band_name"));
					i.putExtra("genre1", profile.getString("genre1"));
					i.putExtra("genre2", profile.getString("genre2"));
					i.putExtra("genre3", profile.getString("genre3"));
					i.putExtra("county", profile.getString("county"));
					i.putExtra("town", profile.getString("town"));
					i.putExtra("soundc_link", profile.getString("soundc_link"));
					i.putExtra("pic_link", profile.getString("pic_link"));
					i.putExtra("updated_at", profile.getString("updated_at"));
					i.putExtra("created_at", profile.getString("created_at"));

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

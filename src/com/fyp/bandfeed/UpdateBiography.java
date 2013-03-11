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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateBiography extends Activity implements OnClickListener {

	private EditText bioEdit;
	private ProgressDialog progressDialog;
	private String bandName;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_biography);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		prefs = getSharedPreferences("userPrefs", 0);

		Bundle extras = getIntent().getExtras();
		bandName = extras.getString("bandName");

		TextView bioText = (TextView) findViewById(R.id.update_biography_textview);
		bioText.setText("Update your band biography here. You don't need to worry about mentioning where you're based or listing the members & their roles etc, there's appropriate sections for these on your profile!");

		bioEdit = (EditText) findViewById(R.id.update_biography_edit);
		bioEdit.setText(extras.getString("bio"));

		Button update = (Button) findViewById(R.id.update_biography_button);
		update.setOnClickListener(this);

		Button cancel = (Button) findViewById(R.id.cancel_biography_button);
		cancel.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
			i.putExtra("page", "UpdateBiography");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_biography_button:
			new UpdateBio().execute();
			break;
		case R.id.cancel_biography_button:
			finish();
		}

	}

	class UpdateBio extends AsyncTask<String, String, String> {

		JSONParser jsonParser = new JSONParser();
		private static final String UpdateProfileURL = "http://bandfeed.co.uk/api/update_profile.php";
		private boolean connection = false;
		private boolean updated = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// closeKeyboard();
			progressDialog = new ProgressDialog(UpdateBiography.this);
			progressDialog.setMessage("Updating..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", bandName));
			params.add(new BasicNameValuePair("bio", bioEdit.getText()
					.toString()));

			JSONObject json = jsonParser.makeHttpRequest(UpdateProfileURL,
					"POST", params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Create Response", json.toString());

					connection = true;

					if (json.getInt("success") == 1) {
						// successfully created profile

						AppendToLog logIt = new AppendToLog();
						logIt.append(bandName + " UPDATED BIOGRAPHY");
						// Database entry updated!
						updated = true;

					} else {
						// Connection made but did not update entry in database
						updated = false;
					}
				} catch (JSONException e) {
					// Connection made but did not update entry in database
					updated = false;
				}
				// Connection made regardless of successful update
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
				if (updated) {
					informUser("Biography successfully updated!");
					Editor editor = prefs.edit();
					editor.putString("bio", bioEdit.getText().toString());
					editor.commit();
					UpdateBiography.this.finish();
				} else {
					informUser("Update failed, try again!");
				}
			} else {
				informUser("No internet connection!");
			}
		}
	}

	public void informUser(String msg) {

		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}

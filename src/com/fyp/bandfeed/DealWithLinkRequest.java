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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DealWithLinkRequest extends Activity implements OnClickListener {

	private String user, member, band;
	private ProgressDialog progressDialog;
	AppendToLog logIt = new AppendToLog();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deal_with_link_request);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		Bundle extras = getIntent().getExtras();
		user = extras.getString("user");
		member = extras.getString("member");
		band = extras.getString("band");

		TextView requestText = (TextView) findViewById(R.id.request_textview);
		requestText
				.setText(""
						+ user
						+ " would like to be linked as band member "
						+ member
						+ " in your band "
						+ band
						+ ". Warning, should you accept, you will be giving this user full access rights to edit "
						+ band + "'s profile.");

		Button accept = (Button) findViewById(R.id.accept_link_button);
		accept.setOnClickListener(this);

		Button cancel = (Button) findViewById(R.id.cancel_request_button);
		cancel.setOnClickListener(this);

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
			i.putExtra("page", "DealWithLinkRequest");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.accept_link_button:
			// User accepts link request
			new UpdateUser().execute();
			break;
		case R.id.cancel_link_button:
			finish();
			break;
		}
	}

	class UpdateUser extends AsyncTask<String, String, String> {

		private boolean profileUpdated = false;
		private boolean connection = false;
		JSONParser jsonParser = new JSONParser();
		private static final String userAcceptedURL = "http://www.bandfeed.co.uk/api/user_accepted.php";

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(DealWithLinkRequest.this);
			progressDialog.setMessage("Accepting link..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		/**
		 * Creating profile
		 * */
		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", band));
			params.add(new BasicNameValuePair("name", member));
			params.add(new BasicNameValuePair("user_accepted", user));

			JSONObject json = jsonParser.makeHttpRequest(userAcceptedURL,
					"POST", params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Create Response", json.toString());

					if (json.getInt("success") == 1) {
						// successfully created profile
						logIt.append("LINK CREATED, " + user + " is linked as "
								+ member + " in " + band);
						profileUpdated = true;
					} else {
						logIt.append("FAILED TO LINK " + user + " as " + member
								+ " in " + band);
					}
				} catch (JSONException e) {
					logIt.append("FAILED TO LINK " + user + " as " + member
							+ " in " + band);
				}
				// Connection was successful regardless of whether it
				// dealt with link request
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
				if (profileUpdated) {
					informUser("Link created!");
					DealWithLinkRequest.this.finish();
				} else {
					informUser("Link failed, try again later!");
				}
			}
			else {
				informUser("No internet connection!");
			}
		}
	}

	private void informUser(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}

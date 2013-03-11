/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RequestLink extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private Spinner membersSpinner;
	private Bundle extras;
	private ProgressDialog progressDialog;
	private SharedPreferences prefs;
	private String bandName;
	private ArrayList<String> membersOfBand;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_link);

		extras = getIntent().getExtras();
		bandName = extras.getString("bandName");
		prefs = getSharedPreferences("userPrefs", 0);
		membersOfBand = extras.getStringArrayList("membersOfBand");
		membersOfBand.add(0, " Select..");

		TextView linkText = (TextView) findViewById(R.id.link_textview);
		linkText.setText("If you wish to be linked to the "
				+ bandName
				+ " profile, "
				+ "select the band member which correspnds to you and click the 'Link' button. "
				+ "A request will be sent to all your fellow band mates. "
				+ "Upon being accepted you will gain permission to edit this profile and to send messages to the band's followers. "
				+ "To ensure a quick link, "
				+ "it would be a good idea to inform your band mates that your making a request and to provide them with your username. "
				+ "Your link request will be displayed in each linked band member's 'All Feed'.");

		membersSpinner = (Spinner) findViewById(R.id.link_name_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, membersOfBand);
		// Style of drop down
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		// Set the adapter to the spinner
		membersSpinner.setAdapter(adapter);
		// Listen for a selected item
		membersSpinner.setOnItemSelectedListener(this);

		Button linkButton = (Button) findViewById(R.id.link_button);
		linkButton.setOnClickListener(this);

		Button cancel = (Button) findViewById(R.id.cancel_link_button);
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
			i.putExtra("page", "RequestLink");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		//Do nothing
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.link_button:
			if (membersSpinner
					.getItemAtPosition(membersSpinner.getSelectedItemPosition())
					.toString().equals(" Select..")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				// Dialog Message
				builder.setMessage(
						"Please select the member that corresponds to you!")
						.setCancelable(false)
						.setNegativeButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();

			} else {
				new CreateConnectionAndSendReq().execute();
			}
			break;
		case R.id.cancel_link_button:
			finish();
			break;
		}
	}

	class CreateConnectionAndSendReq extends AsyncTask<String, String, String> {

		private static final String CheckMembersURL = "http://bandfeed.co.uk/api/users_accepted.php";
		JSONParser jsonParser = new JSONParser();
		private boolean messagesSent = true;
		private boolean connection = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(RequestLink.this);
			progressDialog.setMessage("Sending request..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			ArrayList<String> usersAccepted = new ArrayList<String>();
			String member = membersSpinner.getItemAtPosition(
					membersSpinner.getSelectedItemPosition()).toString();
			String userName = prefs.getString("userName", null);

			String message = "<feed type=\"" + userName + "\"><name>" + member
					+ "</name><date>" + new Date().toString()
					+ "</date><message>" + bandName + "</message></feed>";

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", bandName));

			JSONObject json = jsonParser.makeHttpRequest(CheckMembersURL,
					"GET", params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Create Response", json.toString());

					if (json.getInt("success") == 1) {
						// successfully received profile details
						// JSON array
						JSONArray users = json.getJSONArray("users");
						// Gets the user_accepted including those that have the
						// value null.
						for (int i = 0; i < users.length(); i++) {
							JSONObject user = users.getJSONObject(i);
							if (user.getString("user_accepted").equals("null")) {
								// Do nothing!
							} else {
								usersAccepted.add(user
										.getString("user_accepted"));
							}
						}
					} else {
						messagesSent = false;
					}

				} catch (JSONException e) {
					messagesSent = false;
				}

				// If the above connection to the database failed to retrieve
				// the user names,
				// then don't bother carrying out the next connection to
				// RabbitMQ
				if (messagesSent) {
					ConnectToRabbitMQ connection = new ConnectToRabbitMQ(
							userName, null);

					if (connection.createExchange()) {
						// Successfully created exchange!

						for (String user : usersAccepted) {
							// Keep looping provided messages are being sent.
							// Change queue destination each loop to next user
							connection.setQueue(user);
							// Bind to the users queue
							// and send message to user's queue
							if (connection.createBind("news")
									&& connection
											.sendMessage(message.getBytes(),
													"news", message)) {
								// Great at least one member has had the message
								// sent to them!
								messagesSent = true;
							}
						}
						connection.deleteExchange();
						connection.dispose();

					} else {
						// Failed to create exchange!
						messagesSent = false;
					}
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
				if (messagesSent) {
					RequestLink.this.finish();
				} else {
					informUser("Failed to send a link request, try again later!");
				}
			} else {
				informUser("No internet connection!");
			}
		}
	}

	private void informUser(String msg) {
		Toast toast = Toast.makeText(RequestLink.this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}

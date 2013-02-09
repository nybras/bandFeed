package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SendMessages extends SwipeActivity implements OnClickListener,
		OnItemSelectedListener {

	private EditText inputMessage;
	private Button sendButton;
	private ProgressDialog progressDialog;
	private String EXCHANGE_NAME;
	private Spinner bandSpinner, topicSpinner;
	private boolean messageSent;
	private ArrayList<String> bands;
	private SharedPreferences prefs;
	private int numOfBands;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);

		bands = new ArrayList<String>();
		prefs = getSharedPreferences("userPrefs", 0);
		numOfBands = prefs.getInt("numOfBands", 0);

		topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getTopics());
		// Style of drop down
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

		// Set the adapter to the spinner
		topicSpinner.setAdapter(adapter);
		// Listen for a selected item
		topicSpinner.setOnItemSelectedListener(this);

		bandSpinner = (Spinner) findViewById(R.id.from_who_spinner);
		if (numOfBands > 1) {

			ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, getBands());
			// Style of drop down
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);

			// Set the adapter to the spinner
			bandSpinner.setAdapter(adapter2);
			// Listen for a selected item
			bandSpinner.setOnItemSelectedListener(this);
		} else {
			bandSpinner.setVisibility(View.GONE);
			TextView bandSpinText = (TextView) findViewById(R.id.send_from_textview);
			bandSpinText.setVisibility(View.GONE);
		}

		inputMessage = (EditText) findViewById(R.id.send_message_edit);

		sendButton = (Button) findViewById(R.id.send_test_message_button);
		sendButton.setOnClickListener(this);

		messageSent = false;

	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
			i.putExtra("page", "SendMessages");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class CreateConnection extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			closeKeyboard();
			progressDialog = new ProgressDialog(SendMessages.this);
			progressDialog.setMessage("Sending..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			String topic = topicSpinner.getItemAtPosition(
					topicSpinner.getSelectedItemPosition()).toString();
			if (topic.equals("General News")) {
				topic = "news";
			} else if (topic.equals("Gig")) {
				topic = "gigs";
			} else if (topic.equals("Profile Update")) {
				topic = "updates";
			}
			else {
				topic = "releases";
			}
			String message = "<feed type=\"" + topic + "\"><name>"
					+ EXCHANGE_NAME + "</name><date>" + new Date().toString()
					+ "</date><message>" + inputMessage.getText().toString()
					+ "</message></feed>";

			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(EXCHANGE_NAME,
					null);
			if (connection.sendMessage(message.getBytes(), topic, message)) {
				connection.dispose();

				messageSent = true;

			} else {
				// TODO if message wasn't sent
				messageSent = false;
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

			if (messageSent) {
				SendMessages.this.finish();
			}
			else {
				Toast toast = Toast.makeText(SendMessages.this,
						"Failed to send message to your followers, try again later!",
						Toast.LENGTH_SHORT);
				toast.show();
			}

		}

	}

	public void onClick(View v) {

		String band = null;
		if (numOfBands > 1) {
			band = (String) bandSpinner.getItemAtPosition(bandSpinner
					.getSelectedItemPosition());
		} else {
			band = prefs.getString("band0", null);
		}
		if (band.equals("")
				|| band == null
				|| band.equals(" Select..")
				|| topicSpinner
						.getItemAtPosition(
								topicSpinner.getSelectedItemPosition())
						.toString().equals(" Select..")
				|| inputMessage.getText().toString().equals("")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Dialog Message
			builder.setMessage("Please select & enter all fields!")
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

			EXCHANGE_NAME = band.trim();
			new CreateConnection().execute();
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	private ArrayList<String> getBands() {

		bands.add(" Select..");
		for (int i = 0; i < numOfBands; i++) {
			bands.add(prefs.getString("band" + i, null));
		}
		return bands;
	}

	private ArrayList<String> getTopics() {
		ArrayList<String> topics = new ArrayList<String>();
		topics.add(" Select..");
		topics.add("General News");
		topics.add("Gig");
		topics.add("Profile Update");
		topics.add("Music Release");
		return topics;

	}

	@Override
	protected void left() {
		// Do nothing!
		
	}

	@Override
	protected void right() {
		Intent i = new Intent(getApplicationContext(),
				MainActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		startActivity(i);
		
	}
}

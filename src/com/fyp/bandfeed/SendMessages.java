package com.fyp.bandfeed;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Toast;

public class SendMessages extends Activity implements OnClickListener,
		OnItemSelectedListener {

	private EditText inputMessage;
	private Button sendButton;
	private ProgressDialog progressDialog;
	private String EXCHANGE_NAME;
	private Spinner bandSpinner;
	private boolean messageSent;
	private ArrayList<String> bands;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_message);

		bands = new ArrayList<String>();
		generateBands();
		inputMessage = (EditText) findViewById(R.id.send_message_edit);
		bandSpinner = (Spinner) findViewById(R.id.from_who_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getBands());
		// Style of drop down
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

		// Set the adapter to the spinner
		bandSpinner.setAdapter(adapter);
		// Listen for a selected item
		bandSpinner.setOnItemSelectedListener(this);

		sendButton = (Button) findViewById(R.id.send_test_message_button);
		sendButton.setOnClickListener(this);

		messageSent = false;

	}
	
	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// respond to menu item selection
		Toast toast = null;
		switch (item.getItemId()) {
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.settings:
			toast = Toast.makeText(this, "Not implemented yet, coming soon!",
					Toast.LENGTH_SHORT);
			toast.show();
			return true;
		case R.id.send_feedback:
			toast = Toast.makeText(this, "Not implemented yet, coming soon!",
					Toast.LENGTH_SHORT);
			toast.show();
			return true;
		case R.id.log_out:
			final SharedPreferences prefs = getSharedPreferences("userPrefs", 0);
			Editor editor = prefs.edit();
			editor.clear();
			editor.commit();
			//startActivity(new Intent(this, MainActivity.class));
			Intent intent = new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        finish();
	        startActivity(intent);
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

			String message = inputMessage.getText().toString();

			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(EXCHANGE_NAME,
					null);
			if (connection.sendMessage(message.getBytes(), message)) {
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
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
			}

		}

	}

	public void onClick(View v) {

		String band = (String) bandSpinner.getItemAtPosition(bandSpinner
				.getSelectedItemPosition());
		if (band.equals(" Select..")) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Dialog Message
			builder.setMessage("Please select the band member that is you!")
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
		return bands;
	}

	private void generateBands() {

		SharedPreferences prefs = getSharedPreferences("userPrefs", 0);
		int numOfBands = prefs.getInt("numOfBands", 0);
		bands.add(" Select..");
		for (int i = 0; i < numOfBands; i++) {
			bands.add(prefs.getString("band" + i, null));
		}
	}
}

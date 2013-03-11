/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

public class StepOne extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private AutoCompleteTextView gView1, gView2, gView3;
	private EditText bandNameEditText;
	private boolean nameNotInUse;
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_one);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		bandNameEditText = (EditText) findViewById(R.id.add_band_name_edit);

		nameNotInUse = false;

		Genres g = new Genres();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		gView1 = (AutoCompleteTextView) findViewById(R.id.first_genre);
		gView1.setAdapter(adapter);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		gView2 = (AutoCompleteTextView) findViewById(R.id.second_genre);
		gView2.setAdapter(adapter2);

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		gView3 = (AutoCompleteTextView) findViewById(R.id.third_genre);
		gView3.setAdapter(adapter3);

		// set up click listener for the next button
		View nextButton = findViewById(R.id.next_step_two_button);
		nextButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9\\s]+");
		Matcher matcher = pattern.matcher(bandNameEditText.getText().toString()
				.trim());
		if (matcher.matches()) {	
			new CheckName().execute();
		} else {
			alertUser("Band names can't be left blank and can only consist of letters, numbers and whitespace!");
			
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
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.send_feedback:
			Intent i = new Intent(this, SendFeedback.class);
			i.putExtra("page", "StepOne");
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

	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	class CheckName extends AsyncTask<String, String, String> {

		private static final String CheckNameURL = "http://bandfeed.co.uk/api/check_band_name.php";
		JSONParser jsonParser = new JSONParser();
		private boolean connection = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			closeKeyboard();
			progressDialog = new ProgressDialog(StepOne.this);
			progressDialog
					.setMessage("Checking to see if Band already exists..");
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
			params.add(new BasicNameValuePair("band_name", bandNameEditText
					.getText().toString()));

			JSONObject json = jsonParser.makeHttpRequest(CheckNameURL, "GET",
					params);

			// Test for connection
			if (json != null) {
				// check log cat for response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {
					if (json.getInt("success") == 1) {
						// successfully checked names
						nameNotInUse = true;
					} else {
						nameNotInUse = false;
					}
				} catch (JSONException e) {
					nameNotInUse = false;
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
				// Connection made
				if (nameNotInUse == false) {
					// Band name already in use, user must choose a new name
					Toast toast = Toast
							.makeText(
									StepOne.this,
									"Band already exists! "
											+ "Use a different band name or request to be "
											+ "linked to the existing band profile!",
									Toast.LENGTH_LONG);
					toast.show();
				} else {
					// Connection and name check successful
					// Now check all fields are completed
					checkFields();
				}
			} else {
				Toast toast = Toast.makeText(StepOne.this,
						"No internet connection!", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	private void checkFields() {
		String genre1 = gView1.getText().toString().trim();
		String genre2 = gView2.getText().toString().trim();
		String genre3 = gView3.getText().toString().trim();

		if (genre1.length() == 0 || genre2.length() == 0
				|| genre3.length() == 0 || nameNotInUse == false) {
			alertUser("Please enter all fields!");

		} else {
			Globals.setBANDNAME(bandNameEditText.getText().toString().trim());
			Globals.setGENRE1(genre1);
			Globals.setGENRE2(genre2);
			Globals.setGENRE3(genre3);

			Intent i = new Intent(this, StepTwo.class);
			startActivity(i);
		}
	}

	private void alertUser(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Dialog Message
		builder.setMessage(msg).setCancelable(false)
				.setNegativeButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}
}

/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @edited 21/09/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class StepOne extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private ArrayList<String> genres;
	private Spinner firstGenreSpinner, secondGenreSpinner, thirdGenreSpinner;
	private EditText bandNameEditText;
	private boolean nameNotInUse;
	private ProgressDialog progressDialog;

	private static String CheckNameURL = "http://bandfeed.co.uk/api/check_band_name.php";
	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

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

		genres = new ArrayList<String>();
		generateGenres();
		Collections.sort(genres);
		nameNotInUse = false;

		// Search R.layout to see different layout styles
		// Connecting an arrayList up to a Spinner - first genre spinner
		firstGenreSpinner = (Spinner) findViewById(R.id.first_genre_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getGenres());
		// Style of drop down
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

		// Set the adapter to the spinner
		firstGenreSpinner.setAdapter(adapter);
		// Listen for a selected item
		firstGenreSpinner.setOnItemSelectedListener(this);

		// Second genre spinner
		secondGenreSpinner = (Spinner) findViewById(R.id.second_genre_spinner);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getGenres());
		// Style of drop down
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item);
		secondGenreSpinner.setAdapter(adapter2);
		secondGenreSpinner.setOnItemSelectedListener(this);

		// Third genre spinner
		thirdGenreSpinner = (Spinner) findViewById(R.id.third_genre_spinner);
		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getGenres());
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_item);
		thirdGenreSpinner.setAdapter(adapter3);
		thirdGenreSpinner.setOnItemSelectedListener(this);

		// set up click listener for the next button
		View nextButton = findViewById(R.id.next_step_two_button);
		nextButton.setOnClickListener(this);

	}

	public void onClick(View v) {
		new CheckName().execute();

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

	public ArrayList<String> getGenres() {
		return genres;
	}

	private void generateGenres() {

		// This method will eventually draw up genres from somewhere else such
		// as soundCloud etc.
		genres.add(" Select..");
		genres.add("Death Metal");
		genres.add("Black Metal");
		genres.add("Psychedelic");
		genres.add("Rock");
		genres.add("Extreme Metal");
		genres.add("Pop");
		genres.add("Jazz");
		genres.add("Soul");
		genres.add("Punk");
		genres.add("Goth");
		genres.add("Dark-Psy");
		genres.add("Metal");
		genres.add("Progressive");
		genres.add("Drum 'n' Bass");
		genres.add("Trance");
		genres.add("Psy-Trance");
		genres.add("Industrial");
		genres.add("Doom Metal");
		genres.add("Thrash Metal");
		genres.add("Darkwave");
		genres.add("Electronic");
		genres.add("Techno");

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

		JSONParser jsonParser = new JSONParser();

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

			// getting JSON Object
			// Note that create profile url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(CheckNameURL, "GET",
					params);

			// check log cat for response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created profile
					nameNotInUse = true;
				} else {
					nameNotInUse = false;
				}
			} catch (JSONException e) {
				nameNotInUse = false;
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

			if (nameNotInUse == false) {
				Toast toast = Toast
						.makeText(
								StepOne.this,
								"Band already exists! Use a different band name or request to be linked to the existing band profile (see 'About' for more instructions).",
								Toast.LENGTH_LONG);
				toast.show();
			} else {
				checkFields();
			}

		}

	}

	private void checkFields() {
		String genre1 = (String) firstGenreSpinner
				.getItemAtPosition(firstGenreSpinner.getSelectedItemPosition());
		String genre2 = (String) secondGenreSpinner
				.getItemAtPosition(secondGenreSpinner.getSelectedItemPosition());
		String genre3 = (String) thirdGenreSpinner
				.getItemAtPosition(thirdGenreSpinner.getSelectedItemPosition());
		String bandName = bandNameEditText.getText().toString();

		// SOMETHING TO LOOK INTO
		// Instead of doing getText().toString().equals("") or vice-versa,
		// it
		// may be faster to do getText().length() == 0
		if (bandName.equals("") || genre1.equals(" Select..")
				|| genre2.equals(" Select..") || genre3.equals(" Select..")
				|| nameNotInUse == false) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Dialog Message
			builder.setMessage("Please enter all fields!")
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

			Intent i = new Intent(this, StepTwo.class);
			i.putExtra("bandName", bandName.trim());
			i.putExtra("genre1", genre1);
			i.putExtra("genre2", genre2);
			i.putExtra("genre3", genre3);
			startActivity(i);

		}
	}
}

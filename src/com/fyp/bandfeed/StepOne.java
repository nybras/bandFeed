/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @edited 21/09/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class StepOne extends Activity implements
		OnItemSelectedListener, OnClickListener {

	private ArrayList<String> genres;
	private Spinner firstGenreSpinner, secondGenreSpinner, thirdGenreSpinner;
	private EditText bandNameEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_one);

		bandNameEditText = (EditText) findViewById(R.id.add_band_name_edit);

		genres = new ArrayList<String>();
		generateGenres();
		Collections.sort(genres);

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
		String genre1 = (String) firstGenreSpinner
				.getItemAtPosition(firstGenreSpinner.getSelectedItemPosition());
		String genre2 = (String) secondGenreSpinner
				.getItemAtPosition(secondGenreSpinner.getSelectedItemPosition());
		String genre3 = (String) thirdGenreSpinner
				.getItemAtPosition(thirdGenreSpinner.getSelectedItemPosition());
		String bandName = bandNameEditText.getText().toString();
		
//		SOMETHING TO LOOK INTO
//		Instead of doing getText().toString().equals("") or vice-versa, it may be faster to do getText().length() == 0
		if (bandName.equals("") || genre1.equals(" Select..")
				|| genre2.equals(" Select..") || genre3.equals(" Select..")) {

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

}

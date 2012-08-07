/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @edited 05/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;



public class AddNewBandStepOne extends Activity implements OnItemSelectedListener, OnClickListener{
	
	private ArrayList<String> genres;
	private Spinner firstGenreSpinner, secondGenreSpinner, thirdGenreSpinner;
	private EditText bandName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_one);
        
        bandName = (EditText) findViewById(R.id.add_band_name_edit);
        
        genres = new ArrayList<String>();
	    generateGenres();
		Collections.sort(genres);
        
		// Search R.layout to see different layout styles
        // Connecting an arrayList up to a Spinner - first genre spinner
        firstGenreSpinner = (Spinner) findViewById(R.id.first_genre_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getGenres());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item); // Style of drop down
        firstGenreSpinner.setAdapter(adapter); // Set the adapter to the spinner
        firstGenreSpinner.setOnItemSelectedListener(this); // Listen for a selected item
        
        // Second genre spinner
        secondGenreSpinner = (Spinner) findViewById(R.id.second_genre_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getGenres());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item); // Style of drop down
        secondGenreSpinner.setAdapter(adapter2); // Set the adapter to the spinner
        secondGenreSpinner.setOnItemSelectedListener(this); // Listen for a selected item
        
        
        // Third genre spinner
        thirdGenreSpinner = (Spinner) findViewById(R.id.third_genre_spinner);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getGenres());
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_item); // Style of drop down
        thirdGenreSpinner.setAdapter(adapter3); // Set the adapter to the spinner
        thirdGenreSpinner.setOnItemSelectedListener(this); // Listen for a selected item
        
        // set up click listener for the next button
        View nextButton = findViewById(R.id.next_step_two_button);
        nextButton.setOnClickListener(this);
        
    }
	
	public void onClick(View v) {
		String genre1 = (String) firstGenreSpinner.getItemAtPosition(firstGenreSpinner.getSelectedItemPosition());
		String genre2 = (String) secondGenreSpinner.getItemAtPosition(secondGenreSpinner.getSelectedItemPosition());
		String genre3 = (String) thirdGenreSpinner.getItemAtPosition(thirdGenreSpinner.getSelectedItemPosition()); 
		String bandNameValue = bandName.getText().toString();
		Intent i = null;
		switch (v.getId()) {
		case R.id.next_step_two_button:
			if(bandNameValue.equals("") || genre1.equals(" Select..") || genre2.equals(" Select..") || genre3.equals(" Select..")) {
				
				//TODO Implement a warning box that a field is incomplete
			}
			else {
				i = new Intent(this, AddNewBandStepTwo.class);
				i.putExtra("bandName", bandNameValue);
				i.putExtra("genre1", genre1);
				i.putExtra("genre2", genre2);
				i.putExtra("genre3", genre3);
				startActivity(i);
				break;
			}
		}
		
	}

	public ArrayList<String> getGenres() {
		return genres;
	}

	private void generateGenres() {
		
		// This method will eventually draw up genres from somewhere else such as soundcloud etc.
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

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

}

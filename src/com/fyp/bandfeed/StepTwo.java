/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.AdapterView.OnItemSelectedListener;

public class StepTwo extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private Spinner locationsSpinner;
	private EditText amountOfMembers;
	private EditText town;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_two);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		amountOfMembers = (EditText) findViewById(R.id.amount_of_members_edit);
		town = (EditText) findViewById(R.id.town_edit);

		County c = new County();
		// Search R.layout to see different layout styles
		// Connecting an arrayList up to a Spinner - first genre spinner
		locationsSpinner = (Spinner) findViewById(R.id.where_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, c.getCounties());
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		locationsSpinner.setAdapter(adapter);
		// Set the adapter to the spinner
		locationsSpinner.setOnItemSelectedListener(this);

		// set up click listener for the next button
		View nextButton = findViewById(R.id.next_step_three_button);
		nextButton.setOnClickListener(this);

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

	public void onClick(View v) {

		String county = (String) locationsSpinner
				.getItemAtPosition(locationsSpinner.getSelectedItemPosition());

		String townSelected = town.getText().toString().trim();

		if (townSelected.equals("") || county.equals(" Select..")
				|| county.equals("-- England --")
				|| county.equals("-- Wales --")
				|| county.equals("-- Scotland --")
				|| county.equals("-- Northern Ireland --")) {

			alert("Please select & enter all fields!");

		} else {

			int value = 0;
			try {
				value = Integer.parseInt(amountOfMembers.getText().toString());
			} catch (NumberFormatException e) {

				alert("Not a whole number used for the amount of band members!");

			}
			if (value > 0 && value < 11) {

				Globals.setNUMOFMEMBERS(value);
				Globals.setCOUNTY(county);
				Globals.setTOWN(townSelected);

				Intent i = new Intent(this, StepThree.class);
				startActivity(i);

			} else {
				alert("Number must between 1 and 10 for the amount of band members!");

			}
		}
	}

	private void alert(String msg) {
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
			i.putExtra("page", "StepTwo");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

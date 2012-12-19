/**
 * @author Brett Flitter
 * @version Prototype1 - 02/08/2012
 * @edited 21/09/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.ArrayList;
import java.util.Collections;

public class StepTwo extends Activity implements
		OnItemSelectedListener, OnClickListener {
	private ArrayList<String> locations;
	private String bandName;
	private String genre1;
	private String genre2;
	private String genre3;
	private Spinner locationsSpinner;
	private EditText amountOfMembers;
	private EditText town;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_two);

		Bundle extras = getIntent().getExtras();
		bandName = extras.getString("bandName");
		genre1 = extras.getString("genre1");
		genre2 = extras.getString("genre2");
		genre3 = extras.getString("genre3");

		
		amountOfMembers = (EditText) findViewById(R.id.amount_of_members_edit);
		town = (EditText) findViewById(R.id.town_edit);

		locations = new ArrayList<String>();
		generateLocations();
		Collections.sort(locations);

		// Search R.layout to see different layout styles
		// Connecting an arrayList up to a Spinner - first genre spinner
		locationsSpinner = (Spinner) findViewById(R.id.where_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getLocations());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		locationsSpinner.setAdapter(adapter);
		// Set the adapter to the spinner
		locationsSpinner.setOnItemSelectedListener(this);

		// set up click listener for the next button
		View nextButton = findViewById(R.id.next_step_three_button);
		nextButton.setOnClickListener(this);

	}

	public ArrayList<String> getLocations() {
		return locations;
	}

	private void generateLocations() {
		locations.add(" Select..");
		locations.add("Aberdeenshire");
		locations.add("Anglesey");
		locations.add("Angus");
		locations.add("Argyllshire");
		locations.add("Ayrshire");
		locations.add("Banffshire");
		locations.add("Bedfordshire");
		locations.add("Berkshire");
		locations.add("Berwickshire");
		locations.add("Brecknockshire");
		locations.add("Buckinghamshire");
		locations.add("Buteshire");
		locations.add("Caernarfonshire");
		locations.add("Caithness");
		locations.add("Cambridgeshire");
		locations.add("Cardiganshire");
		locations.add("Carmarthenshire");
		locations.add("Cheshire");
		locations.add("Clackmannanshire");
		locations.add("Cornwall");
		locations.add("Cromartyshire");
		locations.add("Cumberland");
		locations.add("Denbighshire");
		locations.add("Derbyshire");
		locations.add("Devon");
		locations.add("Dorset");
		locations.add("Dumfriesshire");
		locations.add("Dunbartonshire");
		locations.add("Durham");
		locations.add("East Loathian");
		locations.add("Essex");
		locations.add("Fife");
		locations.add("Flintshire");
		locations.add("Glamorgan");
		locations.add("Gloucestershire");
		locations.add("Hampshire");
		locations.add("Herefordshire");
		locations.add("Hertfordshire");
		locations.add("Huntingdonshire");
		locations.add("Inverness-shire");
		locations.add("Kent");
		locations.add("Kincardineshire");
		locations.add("Kinross-shire");
		locations.add("Kirkcudbrightshire");
		locations.add("Lanarkshire");
		locations.add("Lancashire");
		locations.add("Leicestershire");
		locations.add("Lincolnshire");
		locations.add("Merioneth");
		locations.add("Middlesex");
		locations.add("Midlothian");
		locations.add("Monmouthshire");
		locations.add("Montgomeryshire");
		locations.add("Morayshire");
		locations.add("Nairnshire");
		locations.add("Norfolk");
		locations.add("Northamptonshire");
		locations.add("Northumberland");
		locations.add("Nottinghamshire");
		locations.add("Orkney");
		locations.add("Oxfordshire");
		locations.add("Peeblesshire");
		locations.add("Pembrokeshire");
		locations.add("Perthshire");
		locations.add("Radnorshire");
		locations.add("Renfrewshire");
		locations.add("Ross-shire");
		locations.add("Roxburghshire");
		locations.add("Rutland");
		locations.add("Selkirkshire");
		locations.add("Shetland");
		locations.add("Shropshire");
		locations.add("Somerset");
		locations.add("Staffordshire");
		locations.add("Stirlingshire");
		locations.add("Suffolk");
		locations.add("Surrey");
		locations.add("Sussex");
		locations.add("Sutherland");
		locations.add("Warwickshire");
		locations.add("West Lothian");
		locations.add("Westmorland");
		locations.add("Wigtownshire");
		locations.add("Wiltshire");
		locations.add("Worcestershire");
		locations.add("Yorkshire");
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

	public void onClick(View v) {

		Intent i = null;

		int value = 0;
		try {
			value = Integer.parseInt(amountOfMembers.getText().toString());
		} catch (NumberFormatException e) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Dialog Message
			builder.setMessage("Not a whole number!")
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

		}
		if (value > 0 && value < 11) {
			i = new Intent(this, StepThree.class);
			i.putExtra("bandName", bandName);
			i.putExtra("genre1", genre1);
			i.putExtra("genre2", genre2);
			i.putExtra("genre3", genre3);
			i.putExtra("amountOfMembers", value);
			i.putExtra("county", (String) locationsSpinner
					.getItemAtPosition(locationsSpinner
							.getSelectedItemPosition()));
			i.putExtra("town", town.getText().toString().trim());

			startActivity(i);

		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Dialog Message
			builder.setMessage("Number must between 1 and 10!")
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
		}
	}
}

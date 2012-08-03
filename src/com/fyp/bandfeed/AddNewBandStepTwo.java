/**
 * @author Brett Flitter
 * @version Prototype1 - 02/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import java.util.ArrayList;
import java.util.Collections;

public class AddNewBandStepTwo extends Activity implements OnItemSelectedListener {
	private ArrayList<String> locations;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_two);
        
        locations = new ArrayList<String>();
        generateLocations();
        Collections.sort(locations);
        
        // Search R.layout to see different layout styles
        // Connecting an arrayList up to a Spinner - first genre spinner
        Spinner locationsSpinner = (Spinner) findViewById(R.id.where_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getLocations());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item); // Style of drop down
        locationsSpinner.setAdapter(adapter); // Set the adapter to the spinner
        locationsSpinner.setOnItemSelectedListener(this); // Listen for a selected item
        
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

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}

}

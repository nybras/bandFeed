/**
 * @author Brett Flitter
 * @version Prototype1 - 05/08/2012
 * @edited 07/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;

public class AddNewBandStepThree extends Activity implements OnClickListener {

	private EditText[] names, roles;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras();
        names = new EditText[extras.getInt("amountOfMembers")]; //array to hold the member's names
        roles = new EditText[extras.getInt("amountOfMembers")]; //array to hold the member's roles
        
        // set up a scrollable view that is linear with a vertical orientation
        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
        linearLayout.setPadding(30, 30, 30, 30);
        
        this.setContentView(scrollView);
        
        for (int i = 0; i < extras.getInt("amountOfMembers"); i++) {
        	
        	TextView textViewName = new TextView(this);
            textViewName.setText("Enter the name of band member " + i);
            textViewName.setGravity(Gravity.CENTER);
            linearLayout.addView(textViewName);
            
        	EditText editTextName = new EditText(this);
        	editTextName.setId(findId(i));
        	names[i] = editTextName;
        	editTextName.setHint("Type name..");
        	editTextName.setGravity(Gravity.CENTER);
        	linearLayout.addView(editTextName);
        	
        	TextView textViewInstrument = new TextView(this);
            textViewInstrument.setText("Enter the role(s) of member " + i + " e.g. Bassist, Vocalist. Use a comma to seperate roles");
            textViewInstrument.setGravity(Gravity.CENTER);
            linearLayout.addView(textViewInstrument);
        	
        	EditText editTextInstrument = new EditText(this);
        	editTextInstrument.setId(findId(i));
        	roles[i] = editTextInstrument;
        	editTextInstrument.setHint("Type role(s)..");
        	editTextInstrument.setGravity(Gravity.CENTER);
        	linearLayout.addView(editTextInstrument);
        	
        	TextView textViewSpace = new TextView(this);
        	textViewSpace.setPadding(0, 0, 0, 10);
        	linearLayout.addView(textViewSpace);
        	
        }
        
        Button nextButton = new Button(this);  //button to next activity
        nextButton.setId(findId(1));
        nextButton.setGravity(Gravity.CENTER);
        nextButton.setOnClickListener(this);
        linearLayout.addView(nextButton);
	}
	
	private int findId(int id) {
		View v = findViewById(id);
		while (v != null) {
			v = findViewById(++id); 
		}
		return id++;
		
	}

	public void onClick(View v) {
		
		boolean detailsEntered = true;
		for (EditText etn : names) {
			if (etn.getText().toString().equals("")) {  //check to see if any names haven't been entered
				detailsEntered = false;
			}
		}
		for (EditText etr : roles) {
			if (etr.getText().toString().equals("")) {  //check to see if roles haven't been entered
				detailsEntered = false;
			}
		}
		
		if (detailsEntered) {
			//TODO go to next activity
		}
		else {
			//TODO not all members details entered 
		}
	}
}

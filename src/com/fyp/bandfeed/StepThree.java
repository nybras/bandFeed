/**
 * @author Brett Flitter
 * @version Prototype1 - 05/08/2012
 * @edited 21/09/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

public class StepThree extends Activity implements OnClickListener {

	private EditText[] names, roles;
	private int amountOfMembers;
	private String bandName, genre1, genre2, genre3, county, town;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		amountOfMembers = extras.getInt("amountOfMembers");
		bandName = extras.getString("bandName");
		genre1 = extras.getString("genre1");
		genre2 = extras.getString("genre2");
		genre3 = extras.getString("genre3");
		county = extras.getString("county");
		town = extras.getString("town");
		names = new EditText[amountOfMembers];
		// array to hold the member's names
		roles = new EditText[amountOfMembers];
		// array to hold the member's roles

		// set up a scroll view that is linear with a vertical orientation
		ScrollView scrollView = new ScrollView(this);
		LinearLayout.LayoutParams lp;
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		// Need to read up on this
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		scrollView.addView(linearLayout);

		linearLayout.setPadding(30, 30, 30, 30);

		this.setContentView(scrollView, lp);

		for (int i = 1; i < amountOfMembers + 1; i++) {

			TextView textViewName = new TextView(this);
			textViewName.setText("Enter the name of band member " + i);
			textViewName.setGravity(Gravity.CENTER);
			linearLayout.addView(textViewName);

			EditText editTextName = new EditText(this);
			editTextName.setId(findId(i));
			names[i - 1] = editTextName;
			editTextName.setHint("Type name..");
			editTextName.setGravity(Gravity.CENTER);
			linearLayout.addView(editTextName);

			TextView textViewInstrument = new TextView(this);
			textViewInstrument.setText("Enter the role(s) of member " + i
					+ " e.g. Bassist, Vocalist. Use a comma to seperate roles");
			textViewInstrument.setGravity(Gravity.CENTER);
			linearLayout.addView(textViewInstrument);

			EditText editTextInstrument = new EditText(this);
			editTextInstrument.setId(findId(i));
			roles[i - 1] = editTextInstrument;
			editTextInstrument.setHint("Type role(s)..");
			editTextInstrument.setGravity(Gravity.CENTER);
			linearLayout.addView(editTextInstrument);

			TextView textViewSpace = new TextView(this);
			textViewSpace.setPadding(0, 0, 0, 10);
			linearLayout.addView(textViewSpace);

		}
				
		final float scale = getResources().getDisplayMetrics().density;
		int dip = (int) (100 * scale + 0.5f);
		// Don't understand this

		Button nextButton = new Button(this);
		// button to next activity
		nextButton.setId(findId(1));
		nextButton.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (dip * scale), LinearLayout.LayoutParams.WRAP_CONTENT));
		// Need to read up on this
		// http://stackoverflow.com/questions/5691411/dynamically-change-the-width-of-a-button-in-android
		nextButton.setText("Next");
		nextButton.setOnClickListener(this);
		linearLayout.addView(nextButton);

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

	private int findId(int id) {
		// create an ID
		View v = findViewById(id);
		while (v != null) {
			v = findViewById(++id);
		}
		return id++;

	}

	public void onClick(View v) {

		Intent i = null;
		boolean detailsEntered = true;
		for (EditText etn : names) {
			if (etn.getText().toString().equals("")) {
				// check to see if any names haven't been entered
				detailsEntered = false;
			}
		}
		for (EditText etr : roles) {
			if (etr.getText().toString().equals("")) {
				// check to see if roles haven't been entered
				detailsEntered = false;
			}
		}

		if (detailsEntered) {

			i = new Intent(this, StepFour.class);
			i.putExtra("bandName", bandName);
			i.putExtra("genre1", genre1);
			i.putExtra("genre2", genre2);
			i.putExtra("genre3", genre3);
			i.putExtra("amountOfMembers", amountOfMembers);
			i.putExtra("county", county);
			i.putExtra("town", town);
			for (int j = 0; j < amountOfMembers; j++) {
				i.putExtra("names" + j, names[j].getText().toString().trim());
				i.putExtra("roles" + j, roles[j].getText().toString().trim());
			}

			startActivity(i);

		} else {
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
		}
	}
}

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
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.EditText;

public class StepThree extends Activity implements OnClickListener {

	private EditText[] names, roles;
	private int amountOfMembers;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		amountOfMembers = Globals.getNUMOFMEMBERS();
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
		linearLayout.setPadding(60, 60, 60, 60);
		scrollView.addView(linearLayout);

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
			editTextName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			editTextName.setGravity(Gravity.CENTER);
			linearLayout.addView(editTextName);

			TextView textViewInstrument = new TextView(this);
			textViewInstrument.setText("Enter the role(s) of member " + i
					+ " e.g. Bassist, Vocalist");
			textViewInstrument.setGravity(Gravity.CENTER);
			linearLayout.addView(textViewInstrument);

			EditText editTextInstrument = new EditText(this);
			editTextInstrument.setId(findId(i));
			roles[i - 1] = editTextInstrument;
			editTextInstrument.setHint("Type role(s)..");
			editTextInstrument.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
			editTextInstrument.setGravity(Gravity.CENTER);
			linearLayout.addView(editTextInstrument);

			TextView textViewSpace = new TextView(this);
			textViewSpace.setPadding(0, 0, 0, 10);
			linearLayout.addView(textViewSpace);

		}

		Button nextButton = new Button(this);
		// button to next activity
		nextButton.setId(findId(1));
		nextButton.setText("Next");
		nextButton.setOnClickListener(this);
		linearLayout.addView(nextButton);
		LinearLayout.LayoutParams params = (LayoutParams) nextButton
				.getLayoutParams();
		params.width = 300;
		nextButton.setLayoutParams(params);

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
			i.putExtra("page", "StepThree");
			startActivity(i);
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

		boolean detailsEntered = true;
		for (EditText etn : names) {
			if (etn.getText().length() == 0) {
				// check to see if any names haven't been entered
				detailsEntered = false;
			}
		}
		for (EditText etr : roles) {
			if (etr.getText().length() == 0) {
				// check to see if roles haven't been entered
				detailsEntered = false;
			}
		}

		if (detailsEntered) {

			for (int j = 0; j < amountOfMembers; j++) {
				Globals.getMEMBERS().add(names[j].getText().toString().trim());
				Globals.getROLES().add(roles[j].getText().toString().trim());
			}

			Intent i = new Intent(this, StepFour.class);
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

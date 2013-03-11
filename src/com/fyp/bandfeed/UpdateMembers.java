/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class UpdateMembers extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private Spinner membersSpinner;
	private Bundle extras;
	private EditText name, role;
	private ProgressDialog progressDialog;
	private String bandName;
	private boolean deleting;
	private ArrayList<String> membersOfBand;
	private CheckBox cMember;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_members);

		prefs = getSharedPreferences("userPrefs", 0);

		extras = getIntent().getExtras();
		deleting = false;
		bandName = extras.getString("bandName");

		membersOfBand = extras.getStringArrayList("membersOfBand");
		membersOfBand.add(0, " Select..");
		membersSpinner = (Spinner) findViewById(R.id.update_members_spinner);
		setSpinner();
		// Listen for a selected item
		membersSpinner.setOnItemSelectedListener(this);

		name = (EditText) findViewById(R.id.update_name);
		role = (EditText) findViewById(R.id.update_role);

		cMember = (CheckBox) findViewById(R.id.checkMember);
		cMember.setChecked(false);
		cMember.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
		});

		Button deleteButton = (Button) findViewById(R.id.delete_member_button);
		deleteButton.setOnClickListener(this);

		Button addButton = (Button) findViewById(R.id.add_member_button);
		addButton.setOnClickListener(this);

		Button cancel = (Button) findViewById(R.id.cancel_update_member_button);
		cancel.setOnClickListener(this);
	}

	private void setSpinner() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, membersOfBand);
		// Style of drop down
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
		// Set the adapter to the spinner
		membersSpinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_update_members, menu);
		return true;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		new CheckForLinkedUsers().execute();
		return;
	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.add_member_button:
			if (name.getText().length() != 0 && role.getText().length() != 0) {
				deleting = false;
				new ManageMembers().execute();
			}

			else {
				alert("A name and role must be entered!");
			}
			break;
		case R.id.delete_member_button:
			if (membersSpinner
					.getItemAtPosition(membersSpinner.getSelectedItemPosition())
					.toString().equals(" Select..")) {
				alert("Please select the member you wish to delete!");

			} else {

				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						this);

				// set dialog message
				alertDialogBuilder
						.setMessage(
								"Are you sure you want to delete this person? If you're deleting yourself and still want to be linked to the "
										+ bandName
										+ " profile, make sure to add yourself again.")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										deleting = true;
										new ManageMembers().execute();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();

			}
			break;
		case R.id.cancel_update_member_button:
			new CheckForLinkedUsers().execute();
			break;
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

	class ManageMembers extends AsyncTask<String, String, String> {

		private static final String UpdateMembersURL = "http://bandfeed.co.uk/api/update_member.php";
		JSONParser jsonParser = new JSONParser();
		private boolean membersUpdated = false;
		private boolean connection = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(UpdateMembers.this);
			progressDialog.setMessage("Updating members..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			String member = membersSpinner
					.getItemAtPosition(membersSpinner.getSelectedItemPosition())
					.toString().trim();
			String nameOfNew = name.getText().toString().trim();
			String roleOfNew = role.getText().toString().trim();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", bandName));
			// Used to delete existing member under the name..(member)
			params.add(new BasicNameValuePair("member", member));
			// Used for creating a new member with the name..(nameOfNew) and
			// role..(roleOfNew)
			params.add(new BasicNameValuePair("role", roleOfNew));
			params.add(new BasicNameValuePair("name", nameOfNew));
			if (cMember.isChecked()) {
				params.add(new BasicNameValuePair("user_accepted", "true"));
				params.add(new BasicNameValuePair("user_name", prefs.getString(
						"userName", null)));
			}

			JSONObject json = jsonParser.makeHttpRequest(UpdateMembersURL,
					"POST", params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Create Response", json.toString());

					if (json.getInt("success") == 1) {

						membersUpdated = true;
						Editor editor = prefs.edit();

						if (deleting) {
							membersOfBand.remove(member);
							editor.putString("removed", member);
							editor.commit();
						} else {
							membersOfBand.add(nameOfNew);
							editor.putString("added", nameOfNew);
							editor.putString("role", roleOfNew);
							editor.commit();
						}
					} else {
						membersUpdated = false;
					}

				} catch (JSONException e) {
					membersUpdated = false;
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
			setSpinner();
			if (connection) {
				if (membersUpdated) {
					informUser("Members list updated!");
					UpdateMembers.this.name.setText("");
					UpdateMembers.this.role.setText("");

				} else {
					informUser("Failed to update members list, try again later!");
				}
			} else {
				informUser("No internet connection!");
			}
		}
	}

	private void informUser(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	class CheckForLinkedUsers extends AsyncTask<String, String, String> {

		private static final String UsersAcceptedURL = "http://bandfeed.co.uk/api/users_accepted.php";
		private boolean isOwned = true;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(UpdateMembers.this);
			progressDialog.setMessage("Checking for linked users..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			JSONParser jsonParser = new JSONParser();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", bandName.toString()));

			// getting JSON Object
			// Note that create profile url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(UsersAcceptedURL,
					"GET", params);

			if (json != null) {

				// check log cat for response
				Log.d("Create Response", json.toString());

				// check for success tag
				try {

					if (json.getInt("success") == 1) {
						// successfully created profile
						isOwned = true;
					} else {
						isOwned = false;
					}

				} catch (JSONException e) {
					isOwned = true;
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
			if (isOwned) {
				UpdateMembers.this.finish();
			} else {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						UpdateMembers.this);

				// set dialog message
				alertDialogBuilder
						.setMessage(
								"There are no linked users left! If you cancel now the "
										+ bandName
										+ " profile will be deleted. Do you wish to cancel and exit?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										Editor editor = prefs.edit();
										editor.putInt("delete", 1);
										editor.commit();
										UpdateMembers.this.finish();
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, just close
										// the dialog box and do nothing
										dialog.cancel();
									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}

		}
	}
}

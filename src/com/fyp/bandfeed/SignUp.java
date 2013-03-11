/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText password2EditText;
	private ProgressDialog progressDialog;
	private AppendToLog logIt;
	private String name;
	private AutoCompleteTextView gView1, gView2, gView3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);

		logIt = new AppendToLog();

		usernameEditText = (EditText) findViewById(R.id.add_username_edit);
		passwordEditText = (EditText) findViewById(R.id.add_password_edit);
		password2EditText = (EditText) findViewById(R.id.add_password2_edit);
		
		Genres g = new Genres();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		gView1 = (AutoCompleteTextView) findViewById(R.id.first_genre);
		gView1.setAdapter(adapter);

		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		gView2 = (AutoCompleteTextView) findViewById(R.id.second_genre);
		gView2.setAdapter(adapter2);

		ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, g.getGenres());
		gView3 = (AutoCompleteTextView) findViewById(R.id.third_genre);
		gView3.setAdapter(adapter3);


		Button next = (Button) findViewById(R.id.username_next_button);
		next.setOnClickListener(this);

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
			i.putExtra("page", "SignUp");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	class CreateUser extends AsyncTask<String, String, String> {

		private boolean signedUp = false;
		private boolean connection = false;
		private static final String CreateProfileURL = "http://bandfeed.co.uk/api/create_user.php";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			closeKeyboard();
			progressDialog = new ProgressDialog(SignUp.this);
			progressDialog.setMessage("Creating account..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			JSONParser jsonParser = new JSONParser();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("password", passwordEditText
					.getText().toString().trim()));
			params.add(new BasicNameValuePair("genre1", gView1.getText().toString().trim()));
			params.add(new BasicNameValuePair("genre2", gView2.getText().toString().trim()));
			params.add(new BasicNameValuePair("genre3", gView3.getText().toString().trim()));

			JSONObject json = jsonParser.makeHttpRequest(CreateProfileURL,
					"POST", params);

			if (json != null) {
				try {
					// check log cat for response
					Log.d("Create Response", json.toString());

					// check for success tag
					if (json.getInt("success") == 1) {
						// successfully logged in
						logIt.append(name + " CREATED ACCOUNT IN DATABASE");

						signedUp = true;
						ConnectToRabbitMQ connection = new ConnectToRabbitMQ(
								null, usernameEditText.getText().toString());
						if (connection.createQueue()) {
							connection.dispose();
						} else {
							logIt.append(name + " FAILED TO CREATE QUEUE");
						}
					} else {
						// User failed to sign up
						signedUp = false;
						logIt.append(name
								+ " FAILED TO CREATE ACCOUNT IN DATABASE");
					}
				} catch (JSONException e) {
					signedUp = false;
				}
				connection = true;
			} else {
				connection = false;
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
			if (connection) {
				if (signedUp) {
					Intent i = new Intent(getApplicationContext(), Login.class)
							.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					informUser("Sign-up was successfull!");
					finish();
					startActivity(i);
				} else {
					informUser("Sign-up unsuccessfull, username already in use!");
				}
			}
			else {
				informUser("No internet connection!");
			}
		}
	}

	public void onClick(View v) {
		String p1 = passwordEditText.getText().toString().trim();
		String p2 = password2EditText.getText().toString().trim();
		name = usernameEditText.getText().toString().trim();

		Pattern pattern = Pattern.compile("[a-zA-Z0-9\\s]+");
		Matcher matcher = pattern.matcher(name);
		if (matcher.matches()) {
			if (p1.equals("") || p2.equals("")) {
				alertUser("Please enter all fields!");
			} else {
				if (p1.equals(p2)) {
					new CreateUser().execute();
				} else {
					alertUser("Passwords don't match!");
				}
			}
		} else {
			alertUser("User name can't be left blank and can only consist of letters, numbers and whitespace!");
		}

	}

	public void informUser(String msg) {

		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void alertUser(String msg) {
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
}

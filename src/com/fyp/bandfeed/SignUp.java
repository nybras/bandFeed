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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends Activity implements OnClickListener {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText password2EditText;
	private ProgressDialog progressDialog;
	private boolean signedUp;
	private AppendToLog logIt;
	private String name;

	// url to create new profile
	private static String CreateProfileURL = "http://bandfeed.co.uk/api/create_user.php";
	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_become_afeeder);

		signedUp = false;
		logIt = new AppendToLog();

		usernameEditText = (EditText) findViewById(R.id.add_username_edit);
		passwordEditText = (EditText) findViewById(R.id.add_password_edit);
		password2EditText = (EditText) findViewById(R.id.add_password2_edit);

		Button next = (Button) findViewById(R.id.username_next_button);
		next.setOnClickListener(this);

	}
	
	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	class CreateUser extends AsyncTask<String, String, String> {

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

			// getting JSON Object
			// Note that create profile url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(CreateProfileURL,
					"POST", params);

			// check log cat for response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully logged in
					logIt.append(name + " CREATED ACCOUNT IN DATABASE");

					ConnectToRabbitMQ connection = new ConnectToRabbitMQ(null,
							usernameEditText.getText().toString());
					if (connection.createQueue()) {
						connection.dispose();
						signedUp = true;
					}

					else {
						// TODO deal with queue not being created!
						signedUp = false;
						logIt.append(name
								+ " FAILED TO CREATE ACCOUNT IN DATABASE");
					}

				} else {
					// User failed to log in
					signedUp = false;
					logIt.append(name + " FAILED TO CREATE ACCOUNT IN DATABASE");

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
			if (signedUp) {
				Intent i = new Intent(getApplicationContext(), Login.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				startActivity(i);
			} else {
				informUser();
			}
		}
	}

	public void onClick(View v) {
		String p1 = passwordEditText.getText().toString().trim();
		String p2 = password2EditText.getText().toString().trim();
		String name = usernameEditText.getText().toString().trim();

		if (p1.equals("") || p2.equals("") || name.equals("")) {
			Toast toast = Toast.makeText(this, "Please enter all fields",
					Toast.LENGTH_SHORT);
			toast.show();
		} else {
			if (p1.equals(p2)) {
				new CreateUser().execute();
			} else {
				Toast toast = Toast.makeText(this, "Passwords don't match",
						Toast.LENGTH_SHORT);
				toast.show();
			}
		}

	}

	public void informUser() {

		Toast toast = Toast.makeText(this,
				"Sign-up unsuccessfull, username already in use",
				Toast.LENGTH_SHORT);
		toast.show();
	}
}

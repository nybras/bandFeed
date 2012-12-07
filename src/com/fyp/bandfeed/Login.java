package com.fyp.bandfeed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private ProgressDialog progressDialog;
	private boolean loggedIn;

	// url to create new profile
	private static String CreateProfileURL = "http://bandfeed.co.uk/api/login_user.php";
	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loggedIn = false;

		usernameEditText = (EditText) findViewById(R.id.add_username_login_edit);
		passwordEditText = (EditText) findViewById(R.id.add_password_login_edit);

		Button login = (Button) findViewById(R.id.login_button);
		login.setOnClickListener(this);

		Button signUp = (Button) findViewById(R.id.signup_button);
		signUp.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.login_button:
			if (usernameEditText.getText().toString().equals("")
					|| passwordEditText.getText().toString().equals("")) {
				Toast toast = Toast.makeText(this,
						"No username or password entered!", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				new LoginUser().execute();
			}
			break;

		case R.id.signup_button:
			Intent intent = new Intent(this, SignUp.class);
			this.startActivity(intent);
			break;

		}
	}

	class LoginUser extends AsyncTask<String, String, String> {

		// CREATES A PERSONALISED QUEUE FOR THE USER

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(Login.this);
			progressDialog.setMessage("Checking details..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			JSONParser jsonParser = new JSONParser();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", usernameEditText
					.getText().toString().trim()));
			params.add(new BasicNameValuePair("password", passwordEditText
					.getText().toString().trim()));

			// getting JSON Object
			// Note that create profile url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(CreateProfileURL,
					"GET", params);

			// check log cat for response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully logged in

					loggedIn = true;

					// Creates username in phone memory as a confirmation
					// that
					// user has logged in
					// TODO this is not safe

					String dirPath = getFilesDir().getAbsolutePath();
					File projDir = new File(dirPath);
					if (!projDir.exists()) {
						projDir.mkdirs();
					}
					try {
						String path = dirPath + File.separator + "user.profile";

						FileOutputStream fOut = new FileOutputStream(path);
						OutputStreamWriter out = new OutputStreamWriter(fOut);

						out.write(usernameEditText.getText().toString().trim()
								+ "\r\n");

						out.close();

						Intent i = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(i);

					} catch (FileNotFoundException e) {

						loggedIn = false;
					} catch (IOException e) {

						loggedIn = false;
					}

				} else {
					// User failed to log in
					loggedIn = false;

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
			informUserSubscript();
		}
	}

	public void informUserSubscript() {

		if (!loggedIn) {
			Toast toast = Toast
					.makeText(
							this,
							"Login unsuccessful, please check your username and password",
							Toast.LENGTH_SHORT);
			toast.show();
		}
	}

}

package com.fyp.bandfeed;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity implements OnClickListener {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private ProgressDialog progressDialog;
	private boolean loggedIn;
	private AppendToLog logIt;
	private SharedPreferences prefs;

	private static String loginUserURL = "http://bandfeed.co.uk/api/login_user.php";

	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		

		loggedIn = false;
		logIt = new AppendToLog();
		prefs = getSharedPreferences("userPrefs", 0);

		usernameEditText = (EditText) findViewById(R.id.add_username_login_edit);
		passwordEditText = (EditText) findViewById(R.id.add_password_login_edit);

		Button login = (Button) findViewById(R.id.login_button);
		login.setOnClickListener(this);

		Button signUp = (Button) findViewById(R.id.signup_button);
		signUp.setOnClickListener(this);

	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
			i.putExtra("page", "Login");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			closeKeyboard();
			progressDialog = new ProgressDialog(Login.this);
			progressDialog.setMessage("Checking details..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... args) {

			JSONParser jsonParser = new JSONParser();

			String name = usernameEditText.getText().toString().trim();
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("name", name));
			params.add(new BasicNameValuePair("password", passwordEditText
					.getText().toString().trim()));

			// getting JSON Object
			// Note that create profile url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(loginUserURL, "GET",
					params);

			// check log cat for response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully logged in
					logIt.append(name + " LOGGED IN");

					Editor editor = prefs.edit();
					editor.putString("userName", usernameEditText.getText()
							.toString().trim());
					editor.commit();

					loggedIn = true;

					int success2 = json.getInt("success2");

					if (success2 == 1) {
						JSONArray bandsObj = json.getJSONArray("bands"); // JSON
						// array
						editor.putInt("numOfBands", bandsObj.length());
						editor.commit();

						for (int i = 0; i < bandsObj.length(); i++) {
							JSONObject band = bandsObj.getJSONObject(i);
							editor.putString("band" + i, band.getString("band"));
							editor.commit();
						}
					}

				} else {
					// User failed to log in
					loggedIn = false;
					logIt.append(name + " FAILED TO LOG IN");

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

			if (loggedIn) {
				Intent i = new Intent(getApplicationContext(),
						MainActivity.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				finish();
				startActivity(i);
			} else {
				informUser();
			}
		}
	}

	public void informUser() {

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

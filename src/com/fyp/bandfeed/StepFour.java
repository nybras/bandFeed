/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class StepFour extends Activity implements OnClickListener,
		OnItemSelectedListener {

	private String imageResponse, name;
	private ImageView imageSelector;
	private static final int SELECT_PHOTO = 1;
	private Bitmap bitmap;
	private boolean selected = false;
	private EditText webpage, soundCloudPage;
	private Spinner nameSpinner;
	private ArrayList<String> names;
	private AppendToLog logIt = new AppendToLog();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_four);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// gets the activity's default ActionBar
			ActionBar actionBar = getActionBar();
			actionBar.show();
		}

		imageSelector = (ImageView) findViewById(R.id.select_logo);
		imageSelector.setOnClickListener(this);

		webpage = (EditText) findViewById(R.id.webpage_edit);
		webpage.setText("http://www.");
		soundCloudPage = (EditText) findViewById(R.id.soundCloud_edit);
		soundCloudPage.setText("http://www.");

		names = new ArrayList<String>();
		generateNames();

		nameSpinner = (Spinner) findViewById(R.id.you_are_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, getNames());
		// Style of drop down
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

		// Set the adapter to the spinner
		nameSpinner.setAdapter(adapter);
		// Listen for a selected item
		nameSpinner.setOnItemSelectedListener(this);

		View nextButton = findViewById(R.id.next_step_five_button);
		nextButton.setOnClickListener(this);

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
			i.putExtra("page", "StepFour");
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_logo:
			// Select an image
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			setSelected(true);
			break;

		case R.id.next_step_five_button:
			name = (String) nameSpinner.getItemAtPosition(nameSpinner
					.getSelectedItemPosition());
			if (name.equals(" Select..")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				// Dialog Message
				builder.setMessage("Please select the band member that is you!")
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
				break;
			} else {
				new CreateNewProfile().execute();
				break;
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		// Part of selecting a photo
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					bitmap = decodeUri(selectedImage);
					imageSelector.setImageBitmap(bitmap);
					setSelected(true);
				} catch (FileNotFoundException e) {
					setSelected(false);
				}
			} else {
				setSelected(false);
			}
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	class CreateNewProfile extends AsyncTask<String, String, String> {

		private static final String CreateProfileURL = "http://bandfeed.co.uk/api/create_profile.php";

		SharedPreferences prefs = getSharedPreferences("userPrefs", 0);
		String username = prefs.getString("userName", null);
		String bandName = Globals.getBANDNAME();
		private static final String ImageResponse = "ImageResponse";
		JSONParser jsonParser = new JSONParser();

		private boolean connection = false;
		private boolean profileCreated = false;
		private boolean exchangeCreated = false;
		private ProgressDialog progressDialog = new ProgressDialog(
				StepFour.this);

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			closeKeyboard();
			progressDialog.setMessage("Uploading Profile..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		/**
		 * Creating profile
		 * */
		@Override
		protected String doInBackground(String... args) {

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", bandName));
			params.add(new BasicNameValuePair("genre1", Globals.getGENRE1()));
			params.add(new BasicNameValuePair("genre2", Globals.getGENRE2()));
			params.add(new BasicNameValuePair("genre3", Globals.getGENRE3()));
			params.add(new BasicNameValuePair("county", Globals.getCOUNTY()));
			params.add(new BasicNameValuePair("town", Globals.getTOWN()));
			params.add(new BasicNameValuePair("amountOfMembers", ""
					+ Globals.getNUMOFMEMBERS()));
			params.add(new BasicNameValuePair("bio", "Not available yet"));

			String sc = soundCloudPage.getText().toString().trim();
			if (sc.equals("")) {
				sc = "Not available yet";
			}
			String wp = webpage.getText().toString().trim();
			if (wp.equals("")) {
				wp = "Not available yet";
			}
			params.add(new BasicNameValuePair("webpage", wp));
			params.add(new BasicNameValuePair("soundCloud", sc));
			params.add(new BasicNameValuePair("user_accepted", name));
			params.add(new BasicNameValuePair("user_name", username));

			if (isSelected()) {
				params.add(new BasicNameValuePair("image", "" + 1));
			} else {
				params.add(new BasicNameValuePair("image", "" + 0));
			}
			for (int i = 0; i < Globals.getNUMOFMEMBERS(); i++) {
				params.add(new BasicNameValuePair("name" + i, Globals
						.getMEMBERS().get(i)));
				params.add(new BasicNameValuePair("role" + i, Globals
						.getROLES().get(i)));
			}

			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(CreateProfileURL,
					"POST", params);

			// Test for connection
			if (json != null) {

				try {
					// check log cat for response
					Log.d("Create Response", json.toString());

					if (json.getInt("success") == 1) {
						// successfully created profile
						logIt.append(bandName + " CREATED BAND PROFILE");

						profileCreated = true;
						// CREATE EXCHANGE
						ConnectToRabbitMQ connection = new ConnectToRabbitMQ(
								bandName.toString(), null);
						if (connection.createExchange()) {
							// connection and exchange has been made
							connection.dispose();

							exchangeCreated = true;

							// Exchange created, update user's list of bands to
							// enable message sending
							CheckForBands nb = new CheckForBands(username);
							ArrayList<String> bands = nb.check();
							Editor editor = prefs.edit();
							for (int i = 0; i < bands.size(); i++) {
								editor.putString("band" + i, bands.get(i));
							}
							editor.putInt("numOfBands", bands.size());
							editor.commit();

						} else {
							// Failed to create exchange
							exchangeCreated = false;
							logIt.append(bandName
									+ " FAILED TO CREATE EXCHANGE");
						}
					} else {
						// Failed to create profile in database
						profileCreated = false;
						logIt.append(bandName
								+ " FAILED TO CREATE BAND PROFILE");
					}
				} catch (JSONException e) {
					profileCreated = false;
				}
				connection = true;
			} else {
				// Failed to make http connection
				connection = false;
			}
			if (isSelected()) {
				// If image is selected up loaded image
				UploadImage uploadImage = new UploadImage();
				uploadImage.getPicture(bitmap, bandName.toString());
				imageResponse = uploadImage.makeConnection();

				Log.d(ImageResponse, imageResponse);
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

			if (connection) {
				if (profileCreated == false) {
					// Connection made but profile wasn't created in database
					informUser("Failed to create profile, try again later!", 0);
					// Return to main activity
					returnToMain();
				} else {
					// Profile was created in Database
					if (exchangeCreated) {
						// Exchange created too!
						informUser("Profile successfully created. "
								+ "You can now swipe left "
								+ "to send messages to your followers!", 1);
						// Return to main activity
						returnToMain();
					} else {
						// Database was created but Exchange wasn't!
						// Inform user!
						informUser(
								"Sorry but part of your profile failed to be created. "
										+ "Please delete your profile and then "
										+ "recreate it to enable "
										+ "message sending!", 1);
						// Return to main activity
						returnToMain();
					}
				}
			} else {
				// No connection made
				informUser("No internet connection, try again later!", 0);
			}

		}

		private void returnToMain() {
			Intent i = new Intent(getApplicationContext(), MainActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			finish();
			startActivity(i);
		}

	}

	public void informUser(String msg, int n) {
		if (n == 0) {
			Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
			toast.show();
		}

	}

	private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
		// http://stackoverflow.com/questions/2507898/how-to-pick-a-image-from-gallery-sd-card-for-my-app-in-android?answertab=oldest#tab-top
		// Decode image size (prevents memory issues)
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 150;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(
				getContentResolver().openInputStream(selectedImage), null, o2);

	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		parent.getItemAtPosition(pos);
	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing!

	}

	private ArrayList<String> getNames() {
		return names;
	}

	private void generateNames() {

		names.add(" Select..");
		for (int i = 0; i < Globals.getNUMOFMEMBERS(); i++) {
			names.add(Globals.getMEMBERS().get(i));
		}
	}
}

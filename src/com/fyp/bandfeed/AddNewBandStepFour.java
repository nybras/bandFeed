/**
x * @author Brett Flitter
 * @version Prototype1 - 25/08/2012
 * @edited 21/09/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

//import android.os.StrictMode;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

//import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
//import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class AddNewBandStepFour extends Activity implements OnClickListener {

	private String bandName;
	private ImageView imageSelector;
	private static final int SELECT_PHOTO = 1;
	private Bundle extras;
	private Bitmap bitmap;
	private boolean selected;
	@SuppressWarnings("unused")
	private EditText bio, soundCloudPage;

	private ProgressDialog progressDialog;

	// url to create new profile
	private static String CreateProfileURL = "http://bandfeed.co.uk/api/create_profile.php";
	// private static String CreateProfileURL =
	// "http://129.168.0.3:3401/bandFeed/api/create_profile.php";

	// JSON NODE names
	private static final String TAG_SUCCESS = "success";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_step_four);

		imageSelector = (ImageView) findViewById(R.id.select_logo);
		imageSelector.setOnClickListener(this);

		bio = (EditText) findViewById(R.id.bio_edit);
		soundCloudPage = (EditText) findViewById(R.id.soundCloud_edit);
		extras = getIntent().getExtras();
		bandName = extras.getString("bandName");
		setSelected(false);

		View nextButton = findViewById(R.id.next_step_five_button);
		nextButton.setOnClickListener(this);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_step_four, menu);
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_logo:
			// Select a picture
			// http://viralpatel.net/blogs/pick-image-from-galary-android-app/
			// Select an image
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			setSelected(true);

			break;
		case R.id.next_step_five_button:

			//createProfileOfBandOnSD();
			new CreateNewProfile().execute();

			break;
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

//	@SuppressWarnings("unused")
//	private void createProfileOfBandOnSD() {
//		String dirPath = getFilesDir().getAbsolutePath() + File.separator
//				+ bandName.toString();
//		File projDir = new File(dirPath);
//		if (!projDir.exists()) {
//			projDir.mkdirs();
//		}
//		try {
//			String path = dirPath + File.separator + bandName.toString()
//					+ ".profile";
//
//			FileOutputStream fOut = new FileOutputStream(path);
//			OutputStreamWriter out = new OutputStreamWriter(fOut);
//
//			// OutputStreamWriter out = new OutputStreamWriter(openFileOutput(
//			// path, MODE_APPEND));
//			// The above comment caused all sorts of problems
//			out.write("#" + bandName + "\r\n");
//			out.write("#" + extras.getString("genre1") + "\r\n");
//			out.write("#" + extras.getString("genre2") + "\r\n");
//			out.write("#" + extras.getString("genre3") + "\r\n");
//			out.write("#" + extras.getString("county") + "\r\n");
//			out.write("#" + extras.getString("town") + "\r\n");
//			out.write("#" + extras.getInt("amountOfMembers") + "\r\n");
//			out.write("#" + bio.getText().toString().trim() + "\r\n");
//
//			String[] splitz = extras.getString("namesAndRoles").split("#");
//			for (String s : splitz) {
//				out.write("#" + s + "\r\n");
//			}
//			out.write("#" + soundCloudPage.getText().toString().trim());
//			// TODO Make sure user doesn't add 'soundcloud.com'. Possibly make
//			// an page existence check
//			out.close();
//
//			if (isSelected()) {
//				File f = new File(dirPath + File.separator + bandName + ".jpg");
//				FileOutputStream ostream = new FileOutputStream(f);
//				// FileOutputStream to write a file
//				bitmap.compress(CompressFormat.JPEG, 100, ostream);
//				ostream.close();
//			}
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	class CreateNewProfile extends AsyncTask<String, String, String> {

		JSONParser jsonParser = new JSONParser();

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(AddNewBandStepFour.this);
			progressDialog.setMessage("Uploading Profile..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		/**
		 * Creating product
		 * */
		@Override
		protected String doInBackground(String... args) {
			

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("band_name", bandName.toString()));
			params.add(new BasicNameValuePair("genre1", extras
					.getString("genre1")));
			params.add(new BasicNameValuePair("genre2", extras
					.getString("genre1")));
			params.add(new BasicNameValuePair("genre3", extras
					.getString("genre3")));
			params.add(new BasicNameValuePair("county", extras
					.getString("county")));
			params.add(new BasicNameValuePair("town", extras.getString("town")));
			params.add(new BasicNameValuePair("members", ""
					+ extras.getInt("amountOfMembers")));
			params.add(new BasicNameValuePair("soundc_link", soundCloudPage
					.getText().toString().trim()));
			params.add(new BasicNameValuePair("pic_link", "" + "none yet"));

			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(CreateProfileURL,
					"POST", params);

			// check log cat for response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created profile
					
					ConnectToRabbitMQ connection = new ConnectToRabbitMQ(bandName.toString(), null);
					if (connection.createExchange()) {
						// connection and exchange has been made
						connection.dispose();
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(i);
					}
					else {
						// TODO failed to create exchange
						Intent i = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(i);
					}
					// closing this screen
					
				} else {
					// TODO failed to create profile
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
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
		}

	}

}

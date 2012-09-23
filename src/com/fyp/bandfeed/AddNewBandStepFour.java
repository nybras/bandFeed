/**
 * @author Brett Flitter
 * @version Prototype1 - 25/08/2012
 * @edited 21/09/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
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
	private EditText bio, soundCloudPage;

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
			if (selected && bio.getText().toString() != "") {
				createProfileOfBand();
			}

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
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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

	private void createProfileOfBand() {
		String dirPath = getFilesDir().getAbsolutePath() + File.separator
				+ bandName.toString();
		File projDir = new File(dirPath);
		if (!projDir.exists()) {
			projDir.mkdirs();
		}
		try {
			String path = dirPath + File.separator + bandName.toString()
					+ ".profile";

			FileOutputStream fOut = new FileOutputStream(path);
			OutputStreamWriter out = new OutputStreamWriter(fOut);

			// OutputStreamWriter out = new OutputStreamWriter(openFileOutput(
			// path, MODE_APPEND));
			// The above comment caused all sorts of problems
			out.write("#" + bandName + "\r\n");
			out.write("#" + extras.getString("genre1") + "\r\n");
			out.write("#" + extras.getString("genre2") + "\r\n");
			out.write("#" + extras.getString("genre3") + "\r\n");
			out.write("#" + extras.getString("county") + "\r\n");
			out.write("#" + extras.getString("town") + "\r\n");
			out.write("#" + extras.getInt("amountOfMembers") + "\r\n");
			out.write("#" + bio.getText().toString().trim() + "\r\n");

			String[] splitz = extras.getString("namesAndRoles").split("#");
			for (String s : splitz) {
				out.write("#" + s + "\r\n");
			}
			out.write("#" + soundCloudPage.getText().toString().trim());
			// TODO Make sure user doesn't add 'soundcloud.com'. Possibly make
			// an page existence check
			out.close();

			File f = new File(dirPath + File.separator + bandName + ".jpg");
			FileOutputStream ostream = new FileOutputStream(f);
			// FileOutputStream to write a file
			bitmap.compress(CompressFormat.JPEG, 100, ostream);
			ostream.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}

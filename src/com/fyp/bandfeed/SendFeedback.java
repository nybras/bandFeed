/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SendFeedback extends Activity implements OnClickListener {

	private ProgressDialog progressDialog;
	private EditText comment, device, version;
	private CheckBox page;
	private String pageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_feedback);
		
		Bundle extras = getIntent().getExtras();
		pageName = extras.getString("page");
		comment = (EditText) findViewById(R.id.send_feedback_edit);
		device = (EditText) findViewById(R.id.send_device_edit);
		version = (EditText) findViewById(R.id.send_version_edit);
		page = (CheckBox) findViewById(R.id.checkPage);
		page.setChecked(false);
		page.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
			}
		});
		
		Button send = (Button) findViewById(R.id.submit_button);
		send.setOnClickListener(this);
		
		Button cancel = (Button) findViewById(R.id.cancel_feedback_button);
		cancel.setOnClickListener(this);
	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	class AppendFeedback extends AsyncTask<String, String, String> {

		private boolean sent = false;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			closeKeyboard();
			progressDialog = new ProgressDialog(SendFeedback.this);
			progressDialog.setMessage("Sending feedback..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		/**
		 * Creating profile
		 * */
		@Override
		protected String doInBackground(String... args) {

			String message = "COMMENT " + comment.getText().toString()
					+ " - DEVICE " + device.getText().toString()
					+ " - VERSION " + version.getText().toString();

			if (page.isChecked()) {
				message = message + " - PAGE " + pageName;
			}

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("message", message));

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://bandfeed.co.uk/feedback/feedback.php");
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httpclient.execute(httppost);
				sent = true;

			} catch (Exception e) {
				sent = false;
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
			if (sent) {
				informUser("Thank you, your feedback has been sent!");
				SendFeedback.this.finish();
			}
			else {
				informUser("Failed to send feedback, please try again later!");
			}
		}
	}

	public void informUser(String msg) {

		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit_button:
			new AppendFeedback().execute();
			break;
		case R.id.cancel_feedback_button:
			finish();
		}
		
	}

}

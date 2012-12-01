package com.fyp.bandfeed;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TestFanOut extends Activity implements OnClickListener {

	private EditText inputMessage, inputBand;
	private Button sendButton;
	private ProgressDialog progressDialog;
	private String EXCHANGE_NAME;
	private boolean messageSent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_fan_out);

		inputMessage = (EditText) findViewById(R.id.send_message_edit);
		inputBand = (EditText) findViewById(R.id.send_from_edit);

		sendButton = (Button) findViewById(R.id.send_test_message_button);
		sendButton.setOnClickListener(this);
		
		messageSent = false;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_test_fan_out, menu);
		return true;
	}

	class CreateConnection extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(TestFanOut.this);
			progressDialog.setMessage("Sending..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			
			
			String message = inputMessage.getText().toString();
			
			
			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(EXCHANGE_NAME,
					null);
			if (connection.sendMessage(message.getBytes())) {
				connection.dispose();
				
				
				messageSent = true;
//				Intent i = new Intent(getApplicationContext(), MainActivity.class);
//				startActivity(i);
				
			} else {
				//TODO if message wasn't sent
				messageSent = false;
				
//				Intent i = new Intent(getApplicationContext(), MainActivity.class);
//				startActivity(i);
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
			
			if (messageSent) {
				Intent i = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(i);
			}
			
		}

	}

	public void onClick(View v) {
		
		EXCHANGE_NAME = inputBand.getText().toString().trim();
		new CreateConnection().execute();
	}
}

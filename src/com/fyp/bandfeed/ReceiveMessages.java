package com.fyp.bandfeed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ReceiveMessages extends Activity implements OnClickListener {

	private ArrayList<String> messages = new ArrayList<String>();
	private ProgressDialog progressDialog;
	private GetMessages task;
	private LinearLayout linearLayout;
	String queueName;
	private SparseArray<String> ids; // Instead of HashMap

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ids = new SparseArray<String>();
		
		
		
		queueName = getUsername().trim();

		// set up a scrollable view that is linear with a vertical orientation
		ScrollView scrollView = new ScrollView(this);
		LinearLayout.LayoutParams lp;
		lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		// Need to read up on this
		linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		scrollView.addView(linearLayout);

		linearLayout.setPadding(30, 30, 30, 30);

		this.setContentView(scrollView, lp);

		final float scale = getResources().getDisplayMetrics().density;
		int dip = (int) (100 * scale + 0.5f);
		// Don't understand this

		Button nextButton = new Button(this);
		// button to next activity
		nextButton.setLayoutParams(new LinearLayout.LayoutParams(
				(int) (dip * scale), LinearLayout.LayoutParams.WRAP_CONTENT));
		// Need to read up on this
		// http://stackoverflow.com/questions/5691411/dynamically-change-the-width-of-a-button-in-android
		
		Integer id2 = findId(1);
		nextButton.setId(id2);
		ids.put(id2, "nextButton");
		nextButton.setText("Get");
		nextButton.setOnClickListener(this);
		linearLayout.addView(nextButton);

		Button backButton = new Button(this);
		Integer id1 = findId(1);
		backButton.setId(id1);
		ids.put(id1, "backButton");
		backButton.setText("Back to home screen");
		backButton.setOnClickListener(this);
		linearLayout.addView(backButton);

	}
	
	private String getUsername() {
		
		SharedPreferences prefs = getSharedPreferences("userPrefs", 0);
		String username = prefs.getString("userName", null);
		
		return username;
	}

	public void printMessages() {

		if (messages.isEmpty()) {
			Toast toast = Toast.makeText(this, "No new messages",
					Toast.LENGTH_SHORT);
			toast.show();

		} else {

			Iterator<String> it = messages.iterator();
			while (it.hasNext()) {
				String stringPulledOut = it.next();
				TextView textViewMessage = new TextView(this);
				textViewMessage.setText(stringPulledOut);
				textViewMessage.setGravity(Gravity.CENTER);
				linearLayout.addView(textViewMessage);
				it.remove(); // Empty array as it goes along
			}
		}
	}

	class GetMessages extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = new ProgressDialog(ReceiveMessages.this);
			progressDialog.setMessage("Receiving..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {


			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(null,
					queueName);

			// TODO try and make this work again with the below code back in the
			// ConnectToRabbitMQ class

			try {
				if (connection.connectToRabbitMQ()) {
					Channel channel = connection.getChannel();
					QueueingConsumer consumer = new QueueingConsumer(channel);

					int queueSize = channel.queueDeclarePassive(queueName)
							.getMessageCount();
					channel.basicConsume(queueName, true, consumer);

					QueueingConsumer.Delivery delivery;
					// boolean noMessageYet = true;
					for (int i = 0; i < queueSize; i++) {
						delivery = consumer.nextDelivery();
						String message = new String(delivery.getBody());
						messages.add(message);
					}
					connection.dispose();

				}
			} catch (IOException e) {

			} catch (ShutdownSignalException e) {

			} catch (ConsumerCancelledException e) {

			} catch (InterruptedException e) {

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
			printMessages();

		}

	}

	public void onClick(View v) {

		
		if (ids.get(v.getId()).equals("backButton")) {
			Intent i = null;
			i = new Intent(this, MainActivity.class);
			startActivity(i);
		} else {

			task = new GetMessages();
			task.execute();
		}

	}

	private int findId(int id) {
		// create an ID
		View v = findViewById(id);
		while (v != null) {
			v = findViewById(++id);
		}
		return id++;

	}

}

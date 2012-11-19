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
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class FeedAll extends Activity implements OnClickListener{

	private ArrayList<String> messages = new ArrayList<String>();
	private ProgressDialog progressDialog;
	private GetMessages task;
	private LinearLayout linearLayout;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		


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
		nextButton.setText("Get");
		nextButton.setOnClickListener(this);
		linearLayout.addView(nextButton);
		
		
	}

	public void setPageUp() {

		if (messages.isEmpty()) {
			Toast toast = Toast.makeText(this, "No new messages", Toast.LENGTH_SHORT);
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
			progressDialog = new ProgressDialog(FeedAll.this);
			progressDialog.setMessage("Receiving..");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			String queueName = "nybras";

			ConnectToRabbitMQ connection = new ConnectToRabbitMQ(null,
					queueName);
			
			// TODO try and make this work again with the below code back in the ConnectToRabbitMQ class

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
			setPageUp();
			

		}

	}

	public void onClick(View v) {
		
		task = new GetMessages();
		task.execute();
		
	}

}

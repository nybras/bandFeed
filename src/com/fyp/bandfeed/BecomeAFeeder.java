package com.fyp.bandfeed;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

public class BecomeAFeeder extends Activity implements OnClickListener{
	
	private EditText usernameEditText;
	private ProgressDialog progressDialog;

	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_afeeder);
           
        usernameEditText = (EditText) findViewById(R.id.add_username_edit);
        Button next = (Button) findViewById(R.id.username_next_button);
        next.setOnClickListener(this);
        

	}
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_become_afeeder, menu);
        return true;
    }
    
    class CreateUserQueue extends AsyncTask<String, String, String> {
    	
    	// CREATES A PERSONALISED QUEUE FOR THE USER
    	
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		progressDialog = new ProgressDialog(BecomeAFeeder.this);
    		progressDialog.setMessage("Creating Username..");
    		progressDialog.setIndeterminate(false);
    		progressDialog.setCancelable(true);
    		progressDialog.show();
    	}

    	@Override
    	protected String doInBackground(String... params) {
    		
    		 // TODO currently creates a user text file on device
            // Needs changing so that it's just created in Database
            // Database needs to check for duplicates
            
            String dirPath = getFilesDir().getAbsolutePath() + File.separator
    				+ "User";
    		File projDir = new File(dirPath);
    		if (!projDir.exists()) {
    			projDir.mkdirs();
    		}
    		try {
    			String path = dirPath + File.separator + "User"
    					+ ".profile";

    			FileOutputStream fOut = new FileOutputStream(path);
    			OutputStreamWriter out = new OutputStreamWriter(fOut);

    			out.write(usernameEditText.getText().toString() + "\r\n");
    			
    			out.close();

    		} catch (FileNotFoundException e) {

    			// TODO
    		} catch (IOException e) {

    			// TODO
    		}
    		// TODO check user name in Database for duplicates and add user
    		
    		ConnectToRabbitMQ connection = new ConnectToRabbitMQ(null, usernameEditText.getText().toString());
			if (connection.createQueue()) {
				connection.dispose();

				Intent i = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(i);
			}
    		return null;
    	}
    	@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			progressDialog.dismiss();
		}
    }

	public void onClick(View v) {
		new CreateUserQueue().execute();

		
	}
}




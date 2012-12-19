package com.fyp.bandfeed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;

public class GetUserName extends Activity {

	public GetUserName() {
		
	}
	
	public String getUser() {
		
		String dirPath = getFilesDir().getAbsolutePath() + File.separator;
		
		String line;
		String user = null;
		try {
			FileInputStream fin = new FileInputStream(dirPath
					+ "user.profile");

			// prepare the file for reading
			InputStreamReader inputreader = new InputStreamReader(
					fin);
			BufferedReader buffreader = new BufferedReader(
					inputreader);

			// read every line of the file into the line-variable,
			// on line at the time
			while ((line = buffreader.readLine()) != null) {
				// do something with the settings from the file
				user += line;
			}
			
			// close the file again
			fin.close();
			
			return user;
		} catch (java.io.FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		
	}
}

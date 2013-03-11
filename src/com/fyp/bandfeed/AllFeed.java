/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

public class AllFeed extends Feeds {

	protected void onCreate(Bundle savedInstanceState) {
		//Set the type of feeds that should be displayed in this feed
		//null shows all feeds regardless of their type
		attType = null;
		super.onCreate(savedInstanceState);
		
		//Instruct user the first time the activity is displayed
		if (!prefs.contains("firstFeeds")) {
			Toast toast = Toast
					.makeText(
							this,
							"Your feeds are updated automatically as you flick through them!",
							Toast.LENGTH_LONG);
			toast.show();
			Toast toast2 = Toast
					.makeText(
							this,
							"As you subscribe to bands these feeds will fill up with messages!",
							Toast.LENGTH_LONG);
			toast2.show();
			Toast toast3 = Toast
					.makeText(
							this,
							"Go on, keep swipping left or right!",
							Toast.LENGTH_LONG);
			toast3.show();
			Editor editor = prefs.edit();
			editor.putString("firstFeeds", "yes");
			editor.commit();
		}
	}

	@Override
	protected void right() {
		//Swipe right to go to News feed
		Intent i = new Intent(this, NewsFeed.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
	}

	@Override
	protected void left() {
		//Swipe left to go to the Main activity
		Intent i = new Intent(getApplicationContext(), MainActivity.class)
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		finish();
		startActivity(i);
	}

}

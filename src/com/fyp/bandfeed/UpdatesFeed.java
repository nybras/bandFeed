/**
 * @author Brett Flitter
 * @version Prototype1 - 20/02/2013
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.content.Intent;
import android.os.Bundle;

public class UpdatesFeed extends Feeds {
	
	protected void onCreate(Bundle savedInstanceState) {
		attType = "updates";
		super.onCreate(savedInstanceState);
	}


	@Override
	protected void left() {
		Intent i= new Intent(this, ReleasesFeed.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(i);
	}

	@Override
	protected void right() {
		//Do nothing!
		
	}

}

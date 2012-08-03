/**
 * @author Brett Flitter
 * @version Prototype1 - 01/08/2012
 * @title Project bandFeed
 */

package com.fyp.bandfeed;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // set up click listener for add new band button
        View addNewBandButton = findViewById(R.id.add_new_band_button);
        addNewBandButton.setOnClickListener(this);
        
     // set up click listener for wtf button
        View wtfButton = findViewById(R.id.wtf_button);
        wtfButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Decides from the given buttons which new view to open up
     * @ param v takes the button that has just been clicked
     */
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.add_new_band_button:
			i = new Intent(this, AddNewBandStepOne.class);
			startActivity(i);
			break;
		case R.id.wtf_button:
			i = new Intent(this, WTF.class);
			startActivity(i);
			break;
		}
		
	}
}

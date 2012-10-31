package com.fyp.bandfeed;

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class BandProfileDB extends Activity {


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
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);
		scrollView.addView(linearLayout);

		linearLayout.setPadding(30, 30, 30, 30);

		this.setContentView(scrollView, lp);

		Bundle extras = getIntent().getExtras();
		StringBuilder sb = new StringBuilder();
		sb.append(extras.getString("band_name") + "\n");
		sb.append(extras.getString("genre1") + "\n");
		sb.append(extras.getString("genre2") + "\n");
		sb.append(extras.getString("genre3") + "\n");
		sb.append(extras.getString("county") + "\n");
		sb.append(extras.getString("town") + "\n");
		sb.append(extras.getString("soundc_link") + "\n");
		sb.append(extras.getString("pic_link") + "\n");
		sb.append(extras.getString("updated_at") + "\n");
		sb.append(extras.getString("created_at") + "\n");

		
		
		TextView profileTextView = new TextView(this);
		profileTextView.setText(sb);
		profileTextView.setGravity(Gravity.CENTER);
		linearLayout.addView(profileTextView);

	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_band_profile_db, menu);
        return true;
    }
}

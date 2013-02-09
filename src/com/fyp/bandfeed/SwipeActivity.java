package com.fyp.bandfeed;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public abstract class SwipeActivity extends Activity {
	
	//Code taken from
	//http://stackoverflow.com/a/8327453
	
	private static final int SWIPE_MIN_DISTANCE = 40;
	private static final int SWIPE_MAX_OFF_PATH = 300;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gestureDetector = new GestureDetector(new SwipeDetector());
	}

	private class SwipeDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {

			// Check movement along the Y-axis. If it exceeds
			// SWIPE_MAX_OFF_PATH,
			// then dismiss the swipe.
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
				return false;

			// Swipe from right to left.
			// The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
			// and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				right();
				return true;
			}

			// Swipe from left to right.
			// The swipe needs to exceed a certain distance (SWIPE_MIN_DISTANCE)
			// and a certain velocity (SWIPE_THRESHOLD_VELOCITY).
			if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
					&& Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				left();
				return true;
			}

			return false;
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TouchEvent dispatcher.
		if (gestureDetector != null) {
			if (gestureDetector.onTouchEvent(ev))
				// If the gestureDetector handles the event, a swipe has been
				// executed and no more needs to be done.
				return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	protected abstract void left();

	protected abstract void right();
}
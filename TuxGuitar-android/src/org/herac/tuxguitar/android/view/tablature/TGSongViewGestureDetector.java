package org.herac.tuxguitar.android.view.tablature;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class TGSongViewGestureDetector extends GestureDetector.SimpleOnGestureListener {

	private GestureDetectorCompat gestureDetector;
	private TGSongViewScaleGestureDetector songViewScaleGestureDetector;
	private TGSongViewCaretSelector songViewCaretSelector;
	private TGSongView songView;
	
	public TGSongViewGestureDetector(Context context, TGSongView songView) {
		this.gestureDetector = new GestureDetectorCompat(context, this);
		this.songViewScaleGestureDetector = new TGSongViewScaleGestureDetector(context, songView);
		this.songViewCaretSelector = new TGSongViewCaretSelector(songView);
		this.songView = songView;
	}
	
	public boolean processTouchEvent(MotionEvent event) {
		this.songViewScaleGestureDetector.processTouchEvent(event);
		if(!this.songViewScaleGestureDetector.isInProgress() ) {
			this.gestureDetector.onTouchEvent(event);
		}
		return true;
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return this.songViewCaretSelector.select(e.getX(), e.getY());
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if( this.songView.getController().isScrollActionAvailable() ) {
			this.updateAxis(this.songView.getController().getScroll().getX(), distanceX);
			this.updateAxis(this.songView.getController().getScroll().getY(), distanceY);
		}
		return true;
	}
	
	public void updateAxis(TGScrollAxis axis, float distance) {
		if( axis.isEnabled() ) {
			axis.setValue(Math.max(Math.min(axis.getValue() + distance, axis.getMaximum()), axis.getMinimum()));
		}
	}
}

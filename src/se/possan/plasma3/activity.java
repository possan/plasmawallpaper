package se.possan.plasma3;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;

public class activity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * PlasmaSurfaceView psf = new PlasmaSurfaceView();
		 * 
		 * // R.id.preview
		 */
		setContentView(R.layout.main);
		// LinearLayout v =
		// (LinearLayout)getWindow().findViewById(R.id.preview);
		// v.addView( )

	}

}
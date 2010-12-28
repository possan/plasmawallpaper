package se.possan.plasma3;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PlasmaSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private final PlasmaRenderer pr = new PlasmaRenderer(1, 1);
	private final Handler mHandler = new Handler();

	private final Runnable mDrawCube = new Runnable() {
		public void run() {
			drawFrame();
		}
	};

	void drawFrame() {
		final SurfaceHolder holder = getHolder();
		Canvas c = null;
		try {
			c = holder.lockCanvas();
			if (c != null) {
				pr.renderTo(c);
			}
		} finally {
			if (c != null)
				holder.unlockCanvasAndPost(c);
		}
		mHandler.removeCallbacks(mDrawCube);
		mHandler.postDelayed(mDrawCube, 1000 / 60);
		pr.step(1000.0f / 60.0f);
	}

	public PlasmaSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		setFocusable(true);
		mDrawCube.run();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		pr.resize(width, height);
		drawFrame();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		drawFrame();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		mHandler.removeCallbacks(mDrawCube);
	}
}

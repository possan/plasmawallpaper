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

	/*
	 * Draw one frame of the animation. This method gets called repeatedly by
	 * posting a delayed Runnable. You can do any drawing you want in here. This
	 * example draws a wireframe cube.
	 */
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
		mHandler.postDelayed(mDrawCube, 1000 / 20);
		pr.step(1000.0f / 20.0f);
	}

	public PlasmaSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		setFocusable(true);
		// TODO Auto-generated constructor stub
		mDrawCube.run();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// thread.setSurfaceSize(width, height);
		pr.resize(width, height);
		drawFrame();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// start the thread here so that we don't busy-wait in run()
		// waiting for the surface to be created
		// thread.setRunning(true);
		// thread.start();
		drawFrame();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer
	 * be touched. WARNING: after this method returns, the Surface/Canvas must
	 * never be touched again!
	 */

	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		/*
		 * boolean retry = true; // thread.setRunning(false); while (retry) {
		 * try { // thread.join(); retry = false; } catch (InterruptedException
		 * e) { } }
		 */
		mHandler.removeCallbacks(mDrawCube);
	}
}

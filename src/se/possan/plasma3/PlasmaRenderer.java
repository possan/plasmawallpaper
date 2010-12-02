package se.possan.plasma3;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;

public class PlasmaRenderer {

	float time;
	float coeff1;
	float coeff2;
	int[] sintab;
	boolean dirty;
	int width;
	int height;
	int frame;
	int[] pixelbuffer;
	Random r = new Random();
	Bitmap bmp = null;

	public PlasmaRenderer(int w, int h) {
		time = .0f;
		dirty = true;
		width = 0;
		height = 0;
		coeff1 = 1.0f;
		coeff2 = 1.0f;
		frame = 0;
		sintab = new int[256];
		for (int j = 0; j < 256; j++)
			sintab[j] = (int) Math.round(128.0f + 127.0f * Math.sin((float) j
					* Math.PI / 128.0f));
		resize(w, h);
	}

	public void resize(int w, int h) {
		if (w == width && h == height)
			return;
		width = w;
		height = h;
		pixelbuffer = new int[w * h];
		dirty = true;
	}

	public void renderTo(Canvas c) {

		if (dirty) {

			frame++;
			int subpixel0 = frame & 1;
			int subpixel1 = (frame >> 1) & 1;

			int x0 = (int) Math.round(80.0f * time * coeff1);
			int x1 = (int) Math.round(70.0f * time * coeff2);
			int x2 = (int) Math.round(60.0f * time * coeff1);

			for (int j = subpixel0; j < height; j += 2) {
				// int cc = r.nextInt();

				int o = j * width;

				int ta = j + 64 * sintab[(j + x1) & 255];
				int tb = (1024 * j) + 64 * sintab[(x0 + j) & 255];

				int dta = 1024;// + 64 * sintab[(j + x1) & 255];
				int dtb = 0;// 64 * sintab[(j + x2) & 255];

				for (int i = subpixel1; i < width; i += 2) {

					// int sh = (sintab[(ta >> 8) & 255] ^ sintab[(tb >> 8) &
					// 255]);

					int xx = (x0>>4) + sintab[(i + (ta >> 10)) & 255];
					// + sintab[((x2 >> 10) + i) & 255];// +
					// sintab[(o1
					// +
					// j + ta + (x1
					// >> 8)) &
					// 255];
					int yy = (x1>>4) + sintab[(j + (x2>>6)) & 255]
							+ sintab[(i + (tb >> 10)) & 255];// + sintab[xx];
					// + sintab[((x1 >> 10) + j) & 255];// (tb
					// >>
					// 8)
					// +
					// o1
					// + sintab[(o2
					// + i + (x2 >>
					// 8)) &
					// 255];
					
					int zz = (xx * yy) >> 8;// ((xx) & 255) ^ ((yy) & 255);//
											// (tc >> 8)
											// + j + o2
											// +
					zz = sintab[ zz & 255 ];
					xx = sintab[ xx & 255 ];
					yy = sintab[ yy & 255 ];

					// sintab[(o0 + j + (x0
					// >>
					// 8)) & 255];

					// int sh = ((xx >> 8) & 255) ^ (yy & 255);
					// pixelbuffer[o++] = (sh) | (sh << 8) | (sh << 16)
					// | 0xFF000000;

					pixelbuffer[o++] = (xx & 255) | ((yy & 255) << 8)
							| ((zz & 255) << 16) | 0xFF000000;

					ta += dta;
					tb += dtb;

					o++;
				}
			}

			bmp = Bitmap.createBitmap(pixelbuffer, 0, width, width, height,
					Bitmap.Config.ARGB_8888);

			dirty = false;
		}

		c.drawBitmap(bmp, null, new Rect(0, 0, width, height), null);
	}

	public boolean isDirty() {
		return dirty;
	}

	public void step(float dT) {
		time += dT;
		dirty = true;
	}

}

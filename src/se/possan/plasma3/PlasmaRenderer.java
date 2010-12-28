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
	int[] clampsintab;
	int[] xoffsets;
	int[] yoffsets;
	boolean dirty;
	int width;
	int height;
	int frame;
	int[] pixelbuffer;
	Random r = new Random();
	Bitmap bmp = null;
	Perlin p1,p2,p3;
	int[] ditherorderx;
	int[] ditherordery;

	public PlasmaRenderer(int w, int h) {
		time = .0f;
		dirty = true;
		width = 0;
		height = 0;
		coeff1 = 1.0f;
		coeff2 = 1.0f;
		frame = 0;
		sintab = new int[256];
		clampsintab = new int[256];
		for (int j = 0; j < 256; j++)
		{	
			sintab[j] = (int) Math.round(128.0f + 127.0f * Math.sin((float) j
					* Math.PI / 128.0f));
			clampsintab[j] = Math.max( 0, Math.min( 255, (int) Math.round(80.0f + 200.0f * Math.sin((float) j
					* Math.PI / 128.0f)) ));
		}
		ditherorderx = new int[16];
		ditherordery = new int[16];
		
		// 0 8 2 10
		// 12 4 14 6
		// 3 11 1 9
		// 15 7 13 5
		
		ditherorderx[0] = 0;
		ditherorderx[1] = 2;
		ditherorderx[2] = 2;
		ditherorderx[3] = 0;
		ditherorderx[4] = 1;
		ditherorderx[5] = 3;
		ditherorderx[6] = 3;
		ditherorderx[7] = 1;
		ditherorderx[8] = 1;
		ditherorderx[9] = 3;
		ditherorderx[10] = 3;
		ditherorderx[11] = 1;
		ditherorderx[12] = 0;
		ditherorderx[13] = 2;
		ditherorderx[14] = 2;
		ditherorderx[15] = 0;
		
		ditherordery[0] = 0;
		ditherordery[1] = 2;
		ditherordery[2] = 0;
		ditherordery[3] = 2;
		ditherordery[4] = 1;
		ditherordery[5] = 3;
		ditherordery[6] = 1;
		ditherordery[7] = 3;
		ditherordery[8] = 0;
		ditherordery[9] = 2;
		ditherordery[10] = 0;
		ditherordery[11] = 2;
		ditherordery[12] = 1;
		ditherordery[13] = 3;
		ditherordery[14] = 1;
		ditherordery[15] = 3;
		
		
		

		// 2 6 3
		// 5 0 8
		// 1 7 4

		ditherorderx[0] = 1;
		ditherorderx[1] = 0;
		ditherorderx[2] = 0;
		ditherorderx[3] = 2;
		ditherorderx[4] = 2;
		ditherorderx[5] = 3;
		ditherorderx[6] = 0;
		ditherorderx[7] = 1;
		ditherorderx[8] = 1;
		
		ditherordery[0] = 1;
		ditherordery[1] = 2;
		ditherordery[2] = 0;
		ditherordery[3] = 0;
		ditherordery[4] = 2;
		ditherordery[5] = 1;
		ditherordery[6] = 0;
		ditherordery[7] = 2;
		ditherordery[8] = 1;
		
		
		
		
		
		resize(w, h);
	}

	public void resize(int w, int h) {
		if (w == width && h == height)
			return;
		width = w;
		height = h;
		pixelbuffer = new int[w * h];
		xoffsets = new int[w];
		yoffsets = new int[h];
		p1 = new Perlin();
		p2 = new Perlin();
		p3 = new Perlin();
		dirty = true;
	}

	public void renderTo(Canvas c) {
		
		
		

		if (dirty) {

			int x0 = (int) Math.round(4.0f * time * coeff1);
			int x1 = (int) Math.round(3.0f * time * coeff2);
			int x2 = (int) Math.round(5.0f * time * coeff1);

			for( int i=0; i<width; i++ ){
				double t = p1.GetValue((double)i/15.0,0,(double)time / 3.0);
				xoffsets[i] = (int)(300 * t);
			}

			for( int i=0; i<height; i++ ){
				double t = p1.GetValue((double)i/15.0,0,(double)time / 3.0);
				yoffsets[i] = (int)(300 * t);
			}

			
			
			
			
			int subpixel0 = ditherorderx[ frame % 9 ];
			int subpixel1 = ditherordery[ frame % 9 ];
			frame++;
			
			for (int j = subpixel0; j < height; j += 3) {
				// int cc = r.nextInt();

				int o = j * width + subpixel1;

			//	int ta = 0;// j + 64 * sintab[(j + x1) & 255];
			//	int tb = 0;// (1024 * j) + 64 * sintab[(x0 + j) & 255];

			//	int dta = 0;// 1024;// + 64 * sintab[(j + x1) & 255];
			// 	int dtb = 0;// 64 * sintab[(j + x2) & 255];

				int ta = 0;// yoffsets[j];
				
				int o1 = sintab[ ((j>>1) + (x0>>5)) & 255 ];
				int o2 = sintab[ ((j>>3) + (x1>>4)) & 255 ];
				int o3 = sintab[ (o1 + (o2>>1)) & 255 ];
				int o4 = ((o1 * o2) >> 9) + ((o3 * o2)>>9);

				for (int i = subpixel1; i < width; i += 3) {
					
					int tb = 0;// xoffsets[i];

					int u2 = sintab[ ((i>>2) + (o1>>3)) & 255 ];
					int u3 = sintab[ ((i>>2) + (o2>>3)) & 255 ];
					
					// int sh = (sintab[(ta >> 8) & 255] ^ sintab[(tb >> 8) &
					// 255]);
			
					
					int xx = j >> 3;
					xx += o4 >> 2;
					xx &= 255;
					xx = sintab[ xx ];
					xx += o2 >> 4;
					xx += x0 >> 4;
					xx += j >> 2;
					
					int yy = i >> 2;
					yy += o3 >> 3;
					yy += j >> 2;
					yy += u2;
				//	yy += o4 >> 3;
					yy &= 255;
					yy = sintab[ yy ];
					yy += o1 >> 4;
					yy += j >> 1;
					yy += x1 >> 4;
					yy += i >> 1;

					int zz = j >> 1;
					zz += u3;
					zz += o1 >> 1;
					zz &= 255;
					zz = sintab[ zz ];
				//	zz += u2 >> 4;
					zz += i >> 2;
					zz += x2 >> 3;
					zz += o4 >> 3;

					int rr = clampsintab[ (xx + yy) & 255 ];
					int gg = clampsintab[ (yy + zz) & 255 ];
					int bb = clampsintab[ (zz + xx) & 255 ];

					pixelbuffer[o++] = (bb & 255) | ((gg & 255) << 8)
							| ((rr & 255) << 16) | 0xFF000000;

					o++;
					o++;
					// o++;
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

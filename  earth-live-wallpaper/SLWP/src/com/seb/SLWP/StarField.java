package com.seb.SLWP;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;

public class StarField {

	private static int NBSTARS = 600;
	private static float[] stars;

	static int VertSize = 6 * 2 * 4;
	static float squareVertices[] = { -0.2f, -0.2f, 0.2f, -0.2f, 0.2f, 0.2f,
			0.2f, 0.2f, -0.2f, 0.2f, -0.2f, -0.2f, };

	static int ColorSize = 6 * 4 * 2;
	static char squareColors[] = { 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 255 };

	private static final int UVsize = 6 * 2 * 4;
	private static final float MAXDIST = -20f;
	private final float UV[] = { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f,
			1.0f, 1.0f, 0.0f, 0.0f, 0.0f };
	/*
	 * private final short squareColors[] = { 255, 255, 0, 255, 0, 255, 255,
	 * 255, 0, 0, 0, 0, 255, 0, 255, 255 };
	 */
	private FloatBuffer bV;
	private CharBuffer bC;
	private int mc = 0;
	private int mv = 0;
	private int[] buffers;
	private Context mContext;
	private GLTextures tex;
	private FloatBuffer bU;
	private int mu = 0;
	public static float speedfactor=1.0f;
	public static float stardensity=1.0f;

	public StarField(Context context) {
		mContext = context;
		InitStars();

		ByteBuffer nbv = ByteBuffer.allocateDirect(VertSize);
		nbv.order(ByteOrder.nativeOrder());
		bV = nbv.asFloatBuffer();
		bV.put(squareVertices);
		bV.position(0);

		ByteBuffer nbc = ByteBuffer.allocateDirect(ColorSize);
		nbc.order(ByteOrder.nativeOrder());
		bC = nbc.asCharBuffer();
		bC.put(squareColors);
		bC.position(0);

		ByteBuffer nbu = ByteBuffer.allocateDirect(UVsize);
		nbu.order(ByteOrder.nativeOrder());
		bU = nbu.asFloatBuffer();
		bU.put(UV);
		bU.position(0);

	}

	public static void InitStars(){

		stars = new float[(int)(NBSTARS*stardensity) * 5];
		for (int i = 0; i < NBSTARS*stardensity; i += 5) {
			float t = (float) (Math.PI * 2.0f * Math.random());
			// stars[i] = (float) ((float)
			// Math.sin(t)>=0f?Math.sin(t)+2f:Math.sin(t)-2f);
			// stars[i + 1] = (float) ((float)
			// Math.cos(t)>=0f?Math.cos(t)+2f:Math.cos(t)-2f);
			stars[i] = (float) Math.sin(t) * 2.0f;
			stars[i + 1] = (float) Math.cos(t) * 2.0f;
			stars[i + 2] = (float) (Math.random() * MAXDIST);
			stars[i + 3] = (float) Math.max(0.01f, Math.random() * 0.1f);
			stars[i + 4] = (float) Math.max(0.8f, Math.random() * 2f);
		}
	}
	
	public void init(GL10 gl) {
		GL11 gl11 = (GL11) gl;
		tex = new GLTextures(gl, mContext);
		tex.add(R.drawable.starfield);
		tex.loadTextures();
		// if(buffers==null){
		buffers = new int[3];
		gl11.glGenBuffers(3, buffers, 0);
		mc = buffers[0];
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mc);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, ColorSize, bC,
				GL11.GL_STATIC_DRAW);
		mv = buffers[1];
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mv);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, VertSize, bV,
				GL11.GL_STATIC_DRAW);
		mu = buffers[2];
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mu);
		gl11
				.glBufferData(GL11.GL_ARRAY_BUFFER, UVsize, bU,
						GL11.GL_STATIC_DRAW);

		/*
		 * gl.glViewport(0, 0, 480, 800); gl.glMatrixMode(GL10.GL_PROJECTION);
		 * gl.glLoadIdentity(); float xmin, xmax, ymin, ymax; float aspect =
		 * (float) 480 / 800; float zNear = 0.1f; float zFar = 100.0f;
		 * 
		 * ymax = (float) (zNear * Math.tan(45.0f * Math.PI / 360.0)); ymin =
		 * -ymax; xmin = ymin * aspect; xmax = ymax * aspect;
		 * 
		 * gl.glFrustumf(xmin, xmax, ymin, ymax, zNear, zFar);
		 */

		// }
	}

	public void draw(GL10 gl) {
		GL11 gl11 = (GL11) gl;
		// gl.glEnableClientState(GL11.GL_COLOR_ARRAY);

		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glAlphaFunc(GL10.GL_GREATER, 0.5f);
		gl.glEnable(GL10.GL_ALPHA_TEST);
		tex.setTexture(R.drawable.starfield);
		gl.glPushMatrix();
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mv);
		gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mu);
		gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		for (int i = 0; i < NBSTARS*stardensity; i = i + 20) {
			
			gl.glLoadIdentity();
			gl.glTranslatef(stars[i], stars[i + 1], stars[i + 2]);
			gl.glScalef(stars[i + 4], stars[i + 4], 1.0f);
			stars[i + 2] = (float) (stars[i + 2] + stars[i + 3]*speedfactor);
			if (stars[i + 2] > 0) {
				stars[i + 2] = MAXDIST;
			}
			gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
			
			gl.glLoadIdentity();
			gl.glTranslatef(stars[i+5], stars[i + 6], stars[i + 7]);
			gl.glScalef(stars[i + 9], stars[i + 9], 1.0f);
			stars[i + 7] = (float) (stars[i + 7] + stars[i + 8]*speedfactor);
			if (stars[i + 7] > 0) {
				stars[i + 7] = MAXDIST;
			}
			gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
			

			gl.glLoadIdentity();
			gl.glTranslatef(stars[i+10], stars[i + 11], stars[i + 12]);
			gl.glScalef(stars[i + 14], stars[i + 14], 1.0f);
			stars[i + 12] = (float) (stars[i + 12] + stars[i + 13]*speedfactor);
			if (stars[i + 12] > 0) {
				stars[i + 12] = MAXDIST;
			}
			gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
			
			gl.glLoadIdentity();
			gl.glTranslatef(stars[i+15], stars[i + 16], stars[i + 17]);
			gl.glScalef(stars[i + 19], stars[i + 19], 1.0f);
			stars[i + 17] = (float) (stars[i + 17] + stars[i + 18]*speedfactor);
			if (stars[i + 17] > 0) {
				stars[i + 17] = MAXDIST;
			}
			gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
			
			/*
			 * gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bV); gl.glColorPointer(4,
			 * GL10.GL_UNSIGNED_BYTE, 0, bC);
			 * gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			 */
		}
		gl.glPopMatrix();
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_ALPHA_TEST);
		// gl.glDisableClientState(GL11.GL_COLOR_ARRAY);
	}

}

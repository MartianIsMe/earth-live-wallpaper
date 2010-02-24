package com.seb.SLWP;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class StarField {

	private final int NBSTARS = 2100;
	private float[] stars;

	private final float squareVertices[] = { -0.5f, -0.5f,
			0.5f,  -0.5f,
			-0.5f,  0.5f,
			0.5f,   0.5f };
	private final short squareColors[] = { 255, 255, 0, 255, 0, 255, 255, 255,
			0, 0, 0, 0, 255, 0, 255, 255 };
	private FloatBuffer bV;
	private ShortBuffer bC;

	public StarField() {
		stars = new float[NBSTARS];
		for (int i = 0; i < NBSTARS; i+=3) {
			float t = (float) (Math.PI * 2.0f * (Math.random())/32768);
			stars[i] = (float) (Math.sin(t) * 1.0f); // x-coordinate for this											// star
			stars[i + 1] = (float) (Math.cos(t) * 1.0f);
			stars[i + 2] = (float) ((Math.random()/32768) * (-50.0f));
		}
		
		ByteBuffer nbv = ByteBuffer.allocateDirect(squareVertices.length * 4 * 2);
		nbv.order(ByteOrder.nativeOrder());
		bV = nbv.asFloatBuffer();
		bV.put(squareVertices);
		
		ByteBuffer nbc = ByteBuffer.allocateDirect(squareColors.length * 2 * 4);
		nbc.order(ByteOrder.nativeOrder());
		bC = nbc.asShortBuffer();
		bC.put(squareColors);
	
	}

	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glPushMatrix();

		
		
		
		//for (int i = 0; i < NBSTARS; i = i + 3) {
			
			//gl.glLoadIdentity();
			/*gl.glTranslatef(stars[i], stars[i + 1], stars[i + 2]);
			stars[i + 2] = stars[i + 2] + 0.2f;
			if (stars[i + 2] > 0) {
				stars[i + 2] = -50.0f;
			}*/

			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bV);
			gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, bC);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		//}
		gl.glPopMatrix();
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
	}

}

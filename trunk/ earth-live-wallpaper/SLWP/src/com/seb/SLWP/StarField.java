package com.seb.SLWP;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;

public class StarField {

	
	private final int NBSTARS = 1200;
	private float[] stars;

	static int VertSize = 6 * 2 * 4;
	static float squareVertices[] = { -0.2f, -0.2f, 0.2f, -0.2f, 0.2f,
			0.2f, 0.2f, 0.2f, -0.2f, 0.2f, -0.2f, -0.2f, };

	static int ColorSize = 6 * 4 * 2;
	static char squareColors[] = {  255,  255,  255,  255,  255,  255,  255,  255,
			 255,  255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255,
			255, 255 };
	
	
	private static final int UVsize = 6 * 2 * 4;
	private final float UV[] = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 0.0f
	};
	/*private final short squareColors[] = { 255, 255, 0, 255, 0, 255, 255, 255,
			0, 0, 0, 0, 255, 0, 255, 255 };*/
	private FloatBuffer bV;
	private CharBuffer bC;
	private int mc=0;
	private int mv=0;
	private int[] buffers;
	private Context mContext;
	private GLTextures tex;
	private FloatBuffer bU;
	private int mu=0;

	public StarField(Context context) {
		mContext=context;
		stars = new float[NBSTARS*4];
		for (int i = 0; i < NBSTARS; i+=4) {
			float t = (float) (Math.PI * 2.0f * Math.random());
			stars[i] = (float) Math.sin(t); // x-coordinate for this											// star
			stars[i + 1] = (float) Math.cos(t);
			stars[i + 2] = (float) (Math.random() * -100.0f);
			stars[i + 3] = (float) (Math.random()*0.3f); 
		}
		
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

	public void init(GL10 gl){
		GL11 gl11=(GL11)gl;
		tex=new GLTextures(gl,mContext);
		tex.add(R.drawable.starfield);
		tex.loadTextures();
		//if(buffers==null){
			buffers=new int[3];
			gl11.glGenBuffers(3, buffers, 0);
			mc=buffers[0];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mc);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, ColorSize, bC, GL11.GL_STATIC_DRAW);
			mv=buffers[1];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mv);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, VertSize, bV, GL11.GL_STATIC_DRAW);
			mu=buffers[2];
			gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mu);
			gl11.glBufferData(GL11.GL_ARRAY_BUFFER, UVsize, bU, GL11.GL_STATIC_DRAW);
			
			/*gl.glViewport(0, 0, 480, 800);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			float xmin, xmax, ymin, ymax;
			float aspect = (float) 480 / 800;
			float zNear = 0.1f;
			float zFar = 100.0f;

			ymax = (float) (zNear * Math.tan(45.0f * Math.PI / 360.0));
			ymin = -ymax;
			xmin = ymin * aspect;
			xmax = ymax * aspect;

			gl.glFrustumf(xmin, xmax, ymin, ymax, zNear, zFar);*/
		
		//}
	}
	
	public void draw(GL10 gl) {
		GL11 gl11=(GL11)gl;
		//gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
		gl.glPushMatrix();
		
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glAlphaFunc( GL10.GL_GREATER, 0.5f ) ;
		gl.glEnable ( GL10.GL_ALPHA_TEST ) ;
		tex.setTexture(R.drawable.starfield);
		for (int i = 0; i < NBSTARS; i = i + 4) {
			gl.glScalef(stars[i+3], stars[i+3], 1.0f);
			gl.glLoadIdentity();
			gl.glTranslatef(stars[i], stars[i + 1], stars[i + 2]);
			stars[i + 2] = (float) (stars[i + 2] + stars[i+3]);
			if (stars[i + 2] > 0) {
				stars[i + 2] = -100.0f;
			}

		//gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mc);
		//gl11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 0, 0);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mv);
		gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, mu);
		gl11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		gl11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
		
			/*gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bV);
			gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, bC);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);*/
		}
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_BLEND);
		gl.glPopMatrix();
		//gl.glDisableClientState(GL11.GL_COLOR_ARRAY);
	}

}

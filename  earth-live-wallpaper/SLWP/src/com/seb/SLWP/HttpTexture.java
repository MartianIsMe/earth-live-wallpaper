package com.seb.SLWP;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class HttpTexture {
	private GL10 gl;
	private int[] textures = new int[1];
	private int[] mCropWorkspace;
	private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

	private Bitmap bitmap;


	public HttpTexture(GL10 gl) {
		if(gl==null)return;
		this.gl = gl;
		mCropWorkspace = new int[4];
		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

	}

	private void showloading(){
		InputStream is = null;
		is = SLWP.mContext.getResources().openRawResource(
				R.drawable.loading);
		try {
			bitmap = BitmapFactory.decodeStream(is, null, sBitmapOptions);
			createTexture();	
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Ignore.
			}
		}
	}
	
	public void loadTexture() {
		
		if (SLWP.Fcache.exists() && SLWP.Fcache.length() > 0 /*&& !SLWP.destroyed*/) {
			bitmap = BitmapFactory.decodeFile(SLWP.Fcache.getAbsolutePath());
			if (bitmap != null)
				createTexture();
			else{
				showloading();
			}
		} else {
			showloading();
		}
		// SLWP.mRedrawHandler.sleep(SLWP.SLEEPTIME);
	}

	private void createTexture() {
		if(gl==null)return;
		if(SLWP.visible){
			//Log.e("SLWP", "CreateTexture");
			//gl.glDeleteTextures(1, textures, 0);
			gl.glGenTextures(1, textures, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
			gl.glPixelStorei(GL10.GL_UNPACK_ALIGNMENT, 1);
			// GL_LINEAR / GL_LINEAR_MIPMAP_LINEAR
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
	
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
					GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
					GL10.GL_REPEAT);
	
			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
					GL10.GL_MODULATE);
	
			bitmap = Bitmap.createScaledBitmap(bitmap, 1024, 512, true);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	
			mCropWorkspace[0] = 0;
			mCropWorkspace[1] = bitmap.getHeight();
			mCropWorkspace[2] = bitmap.getWidth();
			mCropWorkspace[3] = -bitmap.getHeight();
	
			bitmap.recycle();
	
			((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
					GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);
	
			int error = gl.glGetError();
			if (error != GL10.GL_NO_ERROR) {
				Log.e("SLWP", "Texture Load GLError: " + error);
			}
		}
	}

	public void setTexture() {
		if(gl==null)return;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textures[0]);
	}
}

package com.seb.SLWP;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.opengl.GLUtils;
import android.util.Log;

class Background implements Serializable {

	private static final long serialVersionUID = -5579981347245166159L;
	private static int mTex;
	public boolean texok = false;
	private GL10 gl;
	public static float xpos = -160f;
	public static int vW;
	public static int vH;
	private int[] texf = new int[1];
	private int[] mCropWorkspace;
	private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();

	private Bitmap bitmap;
	private int curtex;
	private float bmpRatio;
	private int bmpW;
	private int bmpH;
	private float curratio;
	private float xoffset;
	private float scrw;

	public Background(Context context) {
		mContext = context;
		mCropWorkspace = new int[4];
		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
	}

	public void InitTex(GL10 gl, int t) {
		if(gl==null)return;
		this.gl = gl;
		curtex = t;
		if (t != -1) {
			textures = new GLTextures(gl, mContext);
			setTexture(t);
			textures.add(mTex);
			textures.loadTextures();
			bmpRatio=1f;
		} else {
			createTexture();
		}

		texok = true;
	}

	public void setDims(int w,int h){
		vW=w;
		vH=h;
		xoffset=(vH*bmpRatio-vH)/2;
		scrw=vH*bmpRatio;
	}
	
	public void Init(GL10 gl) {
		if(gl==null)return;
		InitTex(gl, SLWP.Bg);

	}

	public void draw(GL10 gl) {
		if(gl==null)return;
		//curratio=bmpRatio==1f?0f:bmpRatio;
		gl.glEnable(GL10.GL_TEXTURE_2D);
		if (curtex != -1)
			textures.setTexture(mTex);
		else
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texf[0]);
		if (vH >= vW)
			((GL11Ext) gl).glDrawTexfOES(xpos-xoffset, 0f, 0f, scrw , vH);
		else {
			((GL11Ext) gl).glDrawTexfOES(0f, 0f, 0f, vW, vW);

		}
	}

	public static void setTexture(int t) {
		switch (t) {
		case 0:
			mTex = R.drawable.bg1;
			break;
		case 1:
			mTex = R.drawable.bg2;
			break;
		case 2:
			mTex = R.drawable.bg3;
			break;

		}
	}

	private void createTexture() {
		if(gl==null)return;
		// Log.e("SLWP", "CreateTexture");
		// gl.glDeleteTextures(1, textures, 0);
		gl.glGenTextures(1, texf, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texf[0]);
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

		
		File Ftmp = new File(SLWP.cache.getAbsolutePath()+"/curbg.png");
		if (!Ftmp.exists()) {
			try {
				bitmap=BitmapFactory.decodeFile(SLWP.bgfile);
				if(bitmap==null)return;
				bmpW = bitmap.getWidth();
				bmpH = bitmap.getHeight();
				bmpRatio = (float) bmpW / (float) bmpH;
				bitmap = Bitmap.createScaledBitmap(bitmap, 512, (int) (512 * bmpRatio),
						true);
				Ftmp.createNewFile();
				FileOutputStream os = new FileOutputStream(Ftmp);
				bitmap.compress(CompressFormat.PNG, 100, os);
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			bitmap = BitmapFactory.decodeFile(Ftmp.getAbsolutePath());
			if (bitmap == null) return;
		}
		
		bmpW = bitmap.getWidth();
		bmpH = bitmap.getHeight();
		bmpRatio = (float) bmpW / (float) bmpH;
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

		mCropWorkspace[0] = 0;
		mCropWorkspace[1] = bmpH;
		mCropWorkspace[2] = bmpW;
		mCropWorkspace[3] = -bmpH;

		bitmap.recycle();

		((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D,
				GL11Ext.GL_TEXTURE_CROP_RECT_OES, mCropWorkspace, 0);

		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e("SLWP", "Background Load GLError: " + error);
		}
	}

	private static GLTextures textures;
	private Context mContext;

}

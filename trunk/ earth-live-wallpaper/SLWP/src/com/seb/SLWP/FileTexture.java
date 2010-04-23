package com.seb.SLWP;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class FileTexture {
	private GL10 gl;
	private int[] textures = new int[1];
	private int[] mCropWorkspace;
	private static BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
	private static final String baseurl="http://earth-live-wallpaper.googlecode.com/svn/trunk/%20earth-live-wallpaper/SLWP/maps/";
	private Bitmap bitmap;
	private String fname;


	public FileTexture(GL10 gl,String fname) {
		if(gl==null)return;
		this.gl = gl;
		this.fname=fname;
		mCropWorkspace = new int[4];
		sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
	}

	private void showloading(){
		HttpURLConnection conn;
		InputStream is = null;
		URL myFileUrl = null;
		String ext="jpg";
		if(fname.equalsIgnoreCase("dstartwo")) ext="png";
		File out=new File(SLWP.mapcache+"/"+fname+"."+ext);
		try {
			myFileUrl = new URL(baseurl+"/"+fname+"."+ext);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Log.i("SLWP", "Loading texture "+baseurl+"/"+fname+"."+ext);
		if(out.exists())out.delete();
		try {
			out.createNewFile();
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(1000*10);
			conn.setReadTimeout(1000*10);
			conn.connect();
			is = conn.getInputStream();

			DataOutputStream outd = new DataOutputStream(
					new BufferedOutputStream(
							new FileOutputStream(out), 1024));
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				outd.write(buf, 0, len);
			outd.close();

		} catch (Exception e) {
			Log.e("SLWP", "ERROR: " + e.getMessage());
		} 
		bitmap = BitmapFactory.decodeFile(out.getAbsolutePath());
		if (bitmap != null)
			createTexture();
	}
	
	public void loadTexture() {
		String ext="jpg";
		if(fname.equalsIgnoreCase("dstartwo")) ext="png";
		File f=new File(SLWP.mapcache+"/"+fname+"."+ext);
		if (f.exists()&&f.length()>100) {
			bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
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
				Log.w("SLWP", "Texture Load GLError: " + error);
			}
		}
	}

	public void setTexture() {
		//if(gl==null)return;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, this.textures[0]);
	}
}

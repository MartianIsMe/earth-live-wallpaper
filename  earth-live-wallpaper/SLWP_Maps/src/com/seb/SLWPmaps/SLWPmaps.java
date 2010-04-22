package com.seb.SLWPmaps;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;

public class SLWPmaps extends Activity {
	private final File sd = Environment.getExternalStorageDirectory();
	private File mapcache = new File(sd.getAbsolutePath() + "/EarthRot/maps");
    private  ProgressBar pb;
	private File cache;
	private Runnable r=new Runnable(){
		@Override
		public void run() {
			maps2sd();
		}
	};
	private Button bt;
	private Button bt2;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitCache();
        setContentView(R.layout.main);
        bt=(Button)findViewById(R.id.Button01);
        pb=(ProgressBar)findViewById(R.id.ProgressBar01);
        bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				runOnUiThread(r);
			}
        	
        });
        
        bt2=(Button)findViewById(R.id.Button02);
        bt2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Uri packageURI = Uri.parse("package:com.seb.SLWPmaps");
		        Intent intent = new Intent(Intent.ACTION_DELETE, packageURI);
		        startActivity(intent);
			}
        	
        });
        
        

    }
    
	private void InitCache() {
		File sd = Environment.getExternalStorageDirectory();
		cache = new File(sd.getAbsolutePath() + "/EarthRot");
		File imgnomedia = new File(sd.getAbsolutePath() + "/EarthRot/.nomedia");
		if (!cache.exists()) {
			if (!cache.mkdirs()) {
				Log.e("SLWP", "Cache error");
			} else {
				Log.i("SLWP", "Cache init ok");
				if (!imgnomedia.exists()) {
					try {
						imgnomedia.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			if (!imgnomedia.exists()) {
				try {
					imgnomedia.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		mapcache = new File(cache + "/maps");
		if (!mapcache.exists()) {
			mapcache.mkdir();
			//maps2sd();
		}
	}
    private void maps2sd() {
    	int i=0;
    	Object fp=null;
    	pb.setMax(dClass.getDeclaredFields().length-1);
		//try {
			Field[] fields = dClass.getDeclaredFields();
			new DTask().execute(fields);
			
		//} 
	}

	private void map2sd(int rid, String rname, String ext) {
		InputStream is = this.getResources().openRawResource(rid);
		File out = new File(mapcache + "/" + rname + "." + ext);
		if(out.exists())out.delete();
		try {
			out.createNewFile();
			DataOutputStream os = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(out), 1024));
			byte buf[] = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0)
				os.write(buf, 0, len);
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class DTask extends AsyncTask<Field, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Field... fields) {
			Object fp=null;
			int i=1;
			for (Field f : fields) {
				publishProgress(i++);
				String fName = f.getName();
				fp = null;
				try {
					fp = f.get(dClass);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				if (fp instanceof Integer) {
					
					if (!fName.equalsIgnoreCase("icon"))
						map2sd(((Integer) fp).intValue(), fName, "jpg");
					else if(fName.equalsIgnoreCase("dstartwo"))
						map2sd(((Integer) fp).intValue(), fName, "png");
					else
						continue;
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			pb.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			bt2.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			bt.setVisibility(View.INVISIBLE);
		}
		
	}
	
	static Class dClass;
	static {
		try {
			dClass = Class.forName("com.seb.SLWPmaps.R$drawable");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
}
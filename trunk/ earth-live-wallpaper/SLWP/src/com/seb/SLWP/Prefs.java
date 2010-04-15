package com.seb.SLWP;

import java.io.File;
import java.io.FileOutputStream;

import com.seb.SLWP.ListPreferenceMultiSelect;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Prefs extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private Preference st;
	private Preference tex;
	private Preference bg;
	private ListPreferenceMultiSelect randlist;
	public static Uri currImageURI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prefliste);
		addPreferencesFromResource(R.xml.preferences);
		st = this.getPreferenceManager().findPreference("Synctime");
		tex = this.getPreferenceManager().findPreference("Tex");
		bg = this.getPreferenceManager().findPreference("Bg");
		randlist=(ListPreferenceMultiSelect) findPreference("Randlist");
		
		if (SLWP.Tex != 0)
			st.setEnabled(false);
		else
			st.setEnabled(true);
		
		if (SLWP.Randomtex){
			randlist.setEnabled(true);
		}
		else{
			randlist.setEnabled(false);
		}
		tex.setOnPreferenceChangeListener(this);
		bg.setOnPreferenceChangeListener(this);
		/*Button bt = (Button) findViewById(R.id.Bt_donate);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri
								.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=H8G8WDKKXXYRA&lc=US&item_name=Seb%20Boyart&item_number=SLWP&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_LG%2egif%3aNonHosted")));
			}
		});*/
		
		
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference.getKey().compareToIgnoreCase("Tex")==0) {
			if (Integer.parseInt((String) newValue) == 0)
				st.setEnabled(true);
			else
				st.setEnabled(false);
			
			if (Integer.parseInt((String) newValue) == SLWP.RNDMAP){
				randlist.setEnabled(true);
			}
			else{
				randlist.setEnabled(false);
			}
			
		}
		else if (preference.getKey().compareToIgnoreCase("Bg")==0) {
			if (Integer.parseInt((String) newValue) == -1){
				showGallery();
			}
			else{
				currImageURI=null;
			}
		}
		return true;
		
	}
	
	private void showGallery(){
		// To open up a gallery browser
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),1);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				// currImageURI is the global variable I'm using to hold the content:// URI of the image
				currImageURI = data.getData();
				SLWP.bgfile=getRealPathFromURI(currImageURI);
				
					Bitmap bitmap;
					File Ftmp=new File(SLWP.cache.getAbsolutePath()+"/curbg.png");
					bitmap=BitmapFactory.decodeFile(SLWP.bgfile);
					if(bitmap==null){
						Toast.makeText(this, "Image Error", Toast.LENGTH_LONG);
						return;
					}
					float bmpRatio=(float)bitmap.getWidth()/(float)bitmap.getHeight();
				try {
					if(Ftmp.exists())Ftmp.delete();
					Ftmp.createNewFile();
					FileOutputStream os = new FileOutputStream(Ftmp);
					
					bitmap = Bitmap.createScaledBitmap(bitmap, (int) (512 * bmpRatio),512,
							true);
					
					bitmap.compress(CompressFormat.PNG, 100, os);
					os.close();
				} catch (Exception e) {
					Toast.makeText(this, "Image Error", Toast.LENGTH_SHORT);
				}
			}
		}
	}
	// And to convert the image URI to the direct file system path of the image file
	public String getRealPathFromURI(Uri contentUri) {
		// can post image
		String [] proj={MediaStore.Images.Media.DATA};
		Cursor cursor = managedQuery( contentUri,
				proj, // Which columns to return
				null,       // WHERE clause; which rows to return (all rows)
				null,       // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}

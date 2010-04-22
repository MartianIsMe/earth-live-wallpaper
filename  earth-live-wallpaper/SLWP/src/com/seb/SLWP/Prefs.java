package com.seb.SLWP;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;





import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

public class Prefs extends PreferenceActivity implements
		OnPreferenceChangeListener {
	private Preference st;
	private Preference tex;
	private Preference bg;
	private ListPreferenceMultiSelect randlist;
	private ListPreference ml;
	private int VersionRun=0;
	private int versionCode=0;
	private View dlgLayout;
	private WebView wv;
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
		initMapList();
		Button bt = (Button) findViewById(R.id.bt_donate);
		bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri
								.parse("market://search?q=pub:unixseb")));
			}
		});
		Button bth = (Button) findViewById(R.id.bt_help);
		bth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sHelp();
			}
		});
		// read current version information about this package
		PackageManager manager = getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo("com.seb.SLWP", 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//this.packageName = info.packageName;
		this.versionCode = info.versionCode;
		//this.versionName = info.versionName;
		
		if (PreferenceManager.getDefaultSharedPreferences(this).getInt("VersionRun", -1) != versionCode) {
			SharedPreferences.Editor editor = PreferenceManager
					.getDefaultSharedPreferences(this).edit();
			editor.putInt("VersionRun", this.versionCode);
			editor.commit();
			sHelp();
		
	}

	}

	private void sHelp() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = getLayoutInflater();
		dlgLayout = inflater.inflate(R.layout.helpscreen, null);
		wv = (WebView) dlgLayout.findViewById(R.id.webview01);
		wv.setWebChromeClient(new WebChromeClient());
		wv.loadUrl("file:///android_asset/help.html");
		builder.setMessage("Parametres ?").setCancelable(false)
				.setView(dlgLayout).setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								return;
							}
						}).setNegativeButton(null, null).setTitle(
						"Aide").show();

	}

	private void initMapList(){
		ml = (ListPreference) this.getPreferenceManager().findPreference("Tex");
		File m=new File(SLWP.cache+"/maps");
		FilenameFilter filter = new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.endsWith(".jpg");
		    }
		};
		
		String fnames[]=m.list(filter);
		//ml.setEntries(fnames);
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

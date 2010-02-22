package com.seb.SLWP;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public class SldDlgPref extends DialogPreference {

	private SeekBar sb;
	private Button bt;

	
	public SldDlgPref(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setDialogLayoutResource(R.layout.slider);
		
		
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// TODO Auto-generated method stub
		super.onDialogClosed(positiveResult);
		if(positiveResult){
			this.persistInt(sb.getProgress());
			
		}
	}

	@Override
	protected View onCreateDialogView() {
		// TODO Auto-generated method stub
		View v=super.onCreateDialogView();
		sb=(SeekBar)v.findViewById(R.id.SeekBar01);
		sb.setProgress(this.getPersistedInt(100));
		bt=(Button)v.findViewById(R.id.BtReset);
		bt.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				sb.setProgress(100);
			}
		});
		return v;
	}

}

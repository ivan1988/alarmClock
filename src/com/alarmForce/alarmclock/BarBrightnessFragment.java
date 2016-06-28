package com.alarmForce.alarmclock;

import com.ontimealarm.alarmclock.R;

import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class BarBrightnessFragment extends Fragment implements View.OnClickListener{
	 
	private ImageButton mLowBrightness;
	private ImageButton mMediumBrightness;
	private ImageButton mHighBrightness;
	private ImageButton mAutoBrightness;
	
	boolean mAutoBrightnessSelected;
	private int mBrightnessValue;
		
	private ContentResolver cResolver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup brightnessBarXML, Bundle savedInstanceState){
	
		View rootView = inflater.inflate(R.layout.bar_brightness_fragment, brightnessBarXML, false);
		
		cResolver = getActivity().getContentResolver();		
		
		mLowBrightness = (ImageButton) rootView.findViewById(R.id.lowBrightness);
		mLowBrightness.setOnClickListener(this);
				
		mMediumBrightness = (ImageButton) rootView.findViewById(R.id.mediumBrightness);
		mMediumBrightness.setOnClickListener(this);
				
		mHighBrightness = (ImageButton) rootView.findViewById(R.id.highBrightness);
		mHighBrightness.setOnClickListener(this);
		
		mAutoBrightness = (ImageButton) rootView.findViewById(R.id.autoBrightness);
		mAutoBrightness.setOnClickListener(this);
		
		return rootView;
		
	}
	
	public void onClick(View v){
		
		switch(v.getId()){
		case R.id.lowBrightness: mBrightnessValue = 0;
		mAutoBrightnessSelected = false;
		changeBrightness(mBrightnessValue);
		break;
			
			case R.id.mediumBrightness: mBrightnessValue = 125;
			mAutoBrightnessSelected = false;
			changeBrightness(mBrightnessValue);
			break;
	
			case R.id.highBrightness: mBrightnessValue = 255;
			mAutoBrightnessSelected = false;
			changeBrightness(mBrightnessValue);
			break;
	
			case R.id.autoBrightness: mAutoBrightnessSelected = true;
			changeBrightness(mBrightnessValue);
			break;
			
			default:
				break;
			}
	}

	public void changeBrightness(int brightness){

		try{
			Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception e) {
			Log.e("Error", "Cannot access system brightness");
			e.printStackTrace();
		}
	
		Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
		
		if(mAutoBrightnessSelected == true){
			
			try{
				Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
				Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
			} catch (Exception e) {
				Log.e("Error", "Cannot access system brightness");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
}

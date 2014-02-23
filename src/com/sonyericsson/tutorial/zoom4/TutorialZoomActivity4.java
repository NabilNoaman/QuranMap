/*
 * Copyright (c) 2010, Sony Ericsson Mobile Communication AB. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *    * Redistributions of source code must retain the above copyright notice, this 
 *      list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 *    * Neither the name of the Sony Ericsson Mobile Communication AB nor the names
 *      of its contributors may be used to endorse or promote products derived from
 *      this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sonyericsson.tutorial.zoom4;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.quranmap.android.About;
import com.quranmap.android.MainActivity;
import com.quranmap.android.R;
import com.quranmap.android.Share;
import com.quranmap.android.SurahDetails;
import com.sonyericsson.zoom.DynamicZoomControl;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.LongPressZoomListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for zoom tutorial 4
 */
public class TutorialZoomActivity4 extends SherlockActivity {

    /** Constant used as menu item id for resetting zoom state */
   // private static final int MENU_ID_RESET = 0;

	
	String surahName = "";
	String mapName = "";
	String surahNumber ="";
	int idOfMap =-1;
	private boolean isGeneralMap = false;
	
    /** Image zoom view */
    private ImageZoomView mZoomView;

    /** Zoom control */
    private DynamicZoomControl mZoomControl;

    /** Decoded bitmap image */
    private Bitmap mBitmap;

    /** On touch listener for zoom view */
    private LongPressZoomListener mZoomListener;
    
    //==================================================update to work with Quran Map ============
	// final static String stringMapName = "MAP_NAME";
	
	// final static String stringSurahName = "SURAH_NAME";
    
    //=====================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        
  
    	// setTheme(R.style.Theme_Sherlock);
    	

    	setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
    	
    	super.onCreate(savedInstanceState);

        
        setContentView(R.layout.map);//changed from main to map layout

        mZoomControl = new DynamicZoomControl();

       // mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image800x600);
        mBitmap = BitmapFactory.decodeResource(getResources(), getMapResourceID());

        mZoomListener = new LongPressZoomListener(getApplicationContext());
        mZoomListener.setZoomControl(mZoomControl);

        mZoomView = (ImageZoomView)findViewById(R.id.zoomview);
        mZoomView.setZoomState(mZoomControl.getZoomState());
        mZoomView.setImage(mBitmap);
        mZoomView.setOnTouchListener(mZoomListener);

        mZoomControl.setAspectQuotient(mZoomView.getAspectQuotient());

        resetZoomState();
    }

    
  private int getMapResourceID(){
	  Intent intent = getIntent();
		 mapName     = intent.getStringExtra(MainActivity.stringMapName);
		 surahName   = intent.getStringExtra(MainActivity.stringSurahName);
		 surahNumber = intent.getStringExtra(MainActivity.stringSurahNumber);
		 isGeneralMap= intent.getBooleanExtra(MainActivity.stringGeneralMap , true);
		System.out.println(" Activity 2, Selected map name :"+mapName);
		System.out.println(" Activity 2, Selected Surah name :"+surahName);
		
		 Drawable map = null;
	    	//to change ActionBar title to be with surah name


	     /*  int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");    
	        if ( 0 == titleId ) {
	        titleId = com.actionbarsherlock.R.id.abs__action_bar_title;
	        }
	        System.out.println(" Activity 2, titleId :"+titleId);
	      */
		 if(isGeneralMap){
			 surahName = getResources().getString(R.string.generalMap);
		 }
		 
	        getSherlock().getActionBar().setTitle(surahName);
	        //getSupportActionBar().setTitle(surahName);
	       // getActionBar().setTitle(surahName);
	     //   TextView actionBarTV = (TextView)findViewById(titleId);
	      //  actionBarTV.setText(surahName);
	        //============================

		 
		 
		 try{
			 
		     idOfMap =this.getResources().getIdentifier(mapName, "drawable", this.getPackageName());
		     map = getResources().getDrawable(idOfMap);
		}
		catch (Exception e) {
		
			//map photo not exist with this name
			e.printStackTrace();			
			//String allMapForSelectedLanguage = mapName.substring(0, 2)+"_all";
			//System.out.println(" Activity 2, allMapForSelectedLanguage :"+allMapForSelectedLanguage);			
			//Show all map for selected language
			//id =this.getResources().getIdentifier( allMapForSelectedLanguage, "drawable", this.getPackageName());
			// Show error message ,
		    // check message returned from server
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(TutorialZoomActivity4.this);
	        //Dialog Title
	        alertDialog.setTitle(R.string.noCurrentMapExist_TitleDialog);
	       
	        //Dialog Message
	        alertDialog.setMessage(R.string.noCurrentMapExist);
	     
	        
	        // Icon to Dialog
	        alertDialog.setIcon(R.drawable.error);
	 
	        // On pressing OK button
	        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        	
	            public void onClick(DialogInterface dialog,int which) {
	            	
	            	//Toast.makeText(getApplicationContext(),"OK pressed", Toast.LENGTH_LONG).show();
	                dialog.dismiss();
	                
	                //go back to main activity
	                finish();
	             }
	        });
	        
	        alertDialog.show();
			
			idOfMap = R.drawable.ic_launcher;
			
			//actionBarTV.setText(text);
		}
	   // map = getResources().getDrawable(id);
		//surahMapView.setImageDrawable(map);
		
		
		
		
		return idOfMap;

  }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        mBitmap.recycle();
        mZoomView.setOnTouchListener(null);
        mZoomControl.getZoomState().deleteObservers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  menu.add(Menu.NONE, MENU_ID_RESET, 2, R.string.menu_reset);
      //  return super.onCreateOptionsMenu(menu);
 
    	getSupportMenuInflater().inflate(R.menu.map, menu);
    	
		return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            
        case R.id.reset:
                resetZoomState();
                break;
        
        case R.id.details:
		
        	Intent detailsIntent = new Intent();
        	if(!isGeneralMap){
        	
        	detailsIntent.putExtra(MainActivity.stringSurahName, surahName);
        	detailsIntent.putExtra(MainActivity.stringSurahNumber, surahNumber);
        	detailsIntent.setClass(TutorialZoomActivity4.this, SurahDetails.class);
			startActivity(detailsIntent);
        	}else{
        		/*
        		detailsIntent.putExtra(MainActivity.stringGeneralMap , isGeneralMap);
        		detailsIntent.putExtra(MainActivity.stringSurahNumber , surahNumber);
        		detailsIntent.putExtra(MainActivity.stringSurahName , surahName);
        		
        		detailsIntent.setClass(TutorialZoomActivity4.this, SurahDetails.class);
    			
        		startActivity(detailsIntent);
        		*/
        	}
        	
			break;
				
		case R.id.share:
			
			Intent shareIntent = new Intent();
			
			shareIntent.putExtra(MainActivity.stringGeneralMap , isGeneralMap);
			shareIntent.putExtra(MainActivity.stringSurahNumber , surahNumber);
			shareIntent.putExtra(MainActivity.stringSurahName , surahName);
			shareIntent.putExtra(MainActivity.stringMapName , mapName);
			     
			  
			shareIntent.setClass(TutorialZoomActivity4.this, Share.class);
			startActivity(shareIntent);
			break;
		
        case R.id.extract:
			
		        //Google Analytic Track Event 
		        //Here we want to track extract to sdCard Event
		       //https://developers.google.com/analytics/devguides/collection/android/v3/events
		       EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

		       //first check if API version support access to SD card or not
        	if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO)
        	{
        		createExternalStoragePublicPicture(mapName, idOfMap);
        		// MapBuilder.createEvent().build() returns a Map of event fields and values
  		      // that are set and sent with the hit.
  		      easyTracker.send(MapBuilder
  		          .createEvent("ui_action",                   // Event category (required)
  		                       "menu_action_press",           // Event action (required)
  		                       "extract_toSdCard",            // Event label
  		                       Long.parseLong(surahNumber))   // Event value , i.e surah number
  		          .build()
  		      );
        		
        	}else{
        		//display dialog indicate that version of android did not support required feature
        		indicateToUserFeatureNotAvailable();
        	}
			
			break;
			
		case R.id.about:
			
			Intent aboutIntent = new Intent();
			
			aboutIntent.putExtra(MainActivity.stringGeneralMap , isGeneralMap);
			aboutIntent.putExtra(MainActivity.stringSurahNumber , surahNumber);
			aboutIntent.putExtra(MainActivity.stringSurahName , surahName);
			aboutIntent.putExtra(MainActivity.stringMapName , mapName);
			     
			
			aboutIntent.setClass(TutorialZoomActivity4.this, About.class);
			startActivity(aboutIntent);
			
			break;
    	/*		
    		case R.id.exit:
    			   Intent intent = new Intent(this, MainActivity.class);
			        intent.putExtra("finish", true);
			        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
			        startActivity(intent);
			        finish();
    			break;
    			
    			 */
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Reset zoom state and notify observers
     */
    private void resetZoomState() {
        mZoomControl.getZoomState().setPanX(0.5f);
        mZoomControl.getZoomState().setPanY(0.5f);
        mZoomControl.getZoomState().setZoom(1f);
        mZoomControl.getZoomState().notifyObservers();
    }
    
    //========================================Extract to external SD memory

    @SuppressLint("NewApi")
	void createExternalStoragePublicPicture(String mapName , int resourceOfMap) {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES +"/QuranMap/");
        File file = new File(path, mapName+".jpg");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(resourceOfMap);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
            
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.map_image_extracted)+" in Path:"+file.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
        	Toast.makeText(getApplicationContext(),"Unable to extract current Map , because :"+e.toString(), Toast.LENGTH_LONG).show();
            Log.w("ExternalStorage", "Error writing " + file, e);
            e.printStackTrace();
        }
    }
    
    /*
    void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        handleExternalStorageState(mExternalStorageAvailable,
                mExternalStorageWriteable);
    }
    */
    private void indicateToUserFeatureNotAvailable(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //Dialog Title
        alertDialog.setTitle(R.string.title_feature_Not_Available_Dialog);
       
        //Dialog Message
        alertDialog.setMessage(R.string.message_feature_Not_Available_Dialog);
     
        
        // Icon to Dialog
        alertDialog.setIcon(R.drawable.error);
 
        // On pressing OK button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	
            public void onClick(DialogInterface dialog,int which) {
            	
            	//Toast.makeText(getApplicationContext(),"OK pressed", Toast.LENGTH_LONG).show();
                dialog.dismiss();

             }
        });
        
        alertDialog.show();
    }
    
    @Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    //newConfig.setLocale(new Locale(language));
	    newConfig.locale =new Locale(MainActivity.language);
	   // newConfig.locale =new Locale(loadLanguageFromSharedPrefs(this));
		
	    
	    getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
	    //setContentView(R.layout.activity_about);
	    
	   // setTitle(R.string.app_name);

	    
	    System.out.println("TutorialZoomActivity4 activity , onConfigurationChanged , newConfig.locale:"+newConfig.locale);
	    
	   // initializeUI();
	    
	   
	}
    
    String loadLanguageFromSharedPrefs(Context context) {
		// Get the stored preferences	
				SharedPreferences prefsPrivate = context.getSharedPreferences(MainActivity.PREFS_PRIVATE_FILE_NAME , Context.MODE_PRIVATE);
				// Retrieve the saved values.
				String language = prefsPrivate.getString(MainActivity.PREFS_LANG_KEY , Locale.getDefault().getLanguage());//get default lang if app used for first time and did not conatins prefered lang before
					
				System.out.println("loadLanguageFromSharedPrefs(), language: "+language);
				
				return language;

			}
    
    
	@Override
	  public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Google Analytic
	  }

	@Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Google Analytic
	  }


}

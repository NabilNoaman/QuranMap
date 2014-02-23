package com.quranmap.android;

import java.util.Locale;

import org.amr.arabic.ArabicUtilities;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.sonyericsson.tutorial.zoom4.TutorialZoomActivity4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class SurahDetails extends SherlockActivity {

	
	private String surahName = "";
	private String surahNumber ="";
	private int surahIndex = -1;
	
	private String[] allSurahsGoal = null;
	private String[] allSurahsReasonOfNaming =null;
	
	private TextView goalOfSurahTV    = null;
	private TextView reasonOfNamingTV = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surah_details);
		
		//setTheme(R.style.Theme_Sherlock);
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		
		Intent intent = getIntent();
		 surahName   = intent.getStringExtra(MainActivity.stringSurahName);
		 surahNumber = intent.getStringExtra(MainActivity.stringSurahNumber);
		surahIndex = Integer.parseInt(surahNumber)-1;
		
	
		System.out.println(" Activity SurahDetails, Selected surahName :"+surahName);
		System.out.println(" Activity SurahDetails, Selected surahNumber :"+surahNumber);
		
		
		 getSherlock().getActionBar().setTitle(surahName);
	
		allSurahsGoal = getResources().getStringArray(R.array.goalOfSurah);
		allSurahsReasonOfNaming = getResources().getStringArray(R.array.reasonForNaming);
		
		goalOfSurahTV = (TextView) findViewById(R.id.goalOfSurahTV);
		reasonOfNamingTV = (TextView) findViewById(R.id.reasonOfNamingTV);
		
		goalOfSurahTV.setText(allSurahsGoal[surahIndex]);
		reasonOfNamingTV.setText(allSurahsReasonOfNaming[surahIndex]);
		
		//Arabic font for phones that did not conatins arabic
		//==================================================================================
		  if(MainActivity.language.equalsIgnoreCase("ar")){
		    	for(int i=0 ; i < allSurahsGoal.length ; i++){
		    		allSurahsGoal[i]= ArabicUtilities.reshapeSentence(allSurahsGoal[i]);
		    		allSurahsReasonOfNaming[i]= ArabicUtilities.reshapeSentence(allSurahsReasonOfNaming[i]);
		    	}
		    	
		    	surahName = ArabicUtilities.reshapeSentence(surahName);
		    	//getSherlock().getActionBar().setTitle(surahName);
		    	((TextView)findViewById(R.id.goalOfSurahLabel)).setText(ArabicUtilities.reshapeSentence(getString(R.string.goalOfSurahLabel)));
		    	((TextView)findViewById(R.id.reasonOfNamingLabel)).setText(ArabicUtilities.reshapeSentence(getString(R.string.reasonOfNamingLabel)));
		    	((TextView)findViewById(R.id.goalOfSurahTV)).setText(allSurahsGoal[surahIndex]);
		    	((TextView)findViewById(R.id.reasonOfNamingTV)).setText(allSurahsReasonOfNaming[surahIndex]);
		    	
		    	
		    }
		
		//====================================================================================================
	
			
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_surah_details, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.share:
			
            Intent shareIntent = new Intent();
			
			shareIntent.putExtra(MainActivity.stringGeneralMap , MainActivity.isGeneralMap);
			shareIntent.putExtra(MainActivity.stringSurahNumber , surahNumber);
			shareIntent.putExtra(MainActivity.stringSurahName , surahName);
			shareIntent.putExtra(MainActivity.stringMapName , MainActivity.selectedMapName);
			     
			  
			shareIntent.setClass(this, Share.class);
			startActivity(shareIntent);
			return true;
			
		case R.id.about:
			
			Intent aboutIntent = new Intent();
			aboutIntent.setClass(this, About.class);
			startActivity(aboutIntent);
			
			return true;
			
		
		
		}
		return super.onOptionsItemSelected(item);
	}

	
	 @Override
		public void onConfigurationChanged(Configuration newConfig) {
		    super.onConfigurationChanged(newConfig);

		    //newConfig.setLocale(new Locale(language));
		    newConfig.locale =new Locale(MainActivity.language);
		  //  newConfig.locale =new Locale(loadLanguageFromSharedPrefs(this));
			   
		    
		    getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
		    //setContentView(R.layout.activity_about);
		    
		   // setTitle(R.string.app_name);

		    
		    System.out.println("surah details activity , onConfigurationChanged , newConfig.locale:"+newConfig.locale);
		    
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

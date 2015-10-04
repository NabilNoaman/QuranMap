package com.quranmap.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.quranmap.android.R;

import org.amr.arabic.ArabicUtilities;

public class SurahDetails extends AppCompatActivity {


	private String surahName = "";
	private int surahIndex = 0;
	
	private String[] allSurahsGoal = null;
	private String[] allSurahsReasonOfNaming =null;
	
	private TextView goalOfSurahTV    = null;
	private TextView reasonOfNamingTV = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surah_details);

		Intent intent = getIntent();
		surahIndex = intent.getIntExtra(MainActivity.SELECTED_SURAH_INDEX_KEY, 0);

		surahName = MainActivity.allSurahsNames[surahIndex];

		allSurahsGoal = getResources().getStringArray(R.array.goalOfSurah);
		allSurahsReasonOfNaming = getResources().getStringArray(R.array.reasonForNaming);
		
		goalOfSurahTV = (TextView) findViewById(R.id.goalOfSurahTV);
		reasonOfNamingTV = (TextView) findViewById(R.id.reasonOfNamingTV);
		
		goalOfSurahTV.setText(allSurahsGoal[surahIndex]);
		reasonOfNamingTV.setText(allSurahsReasonOfNaming[surahIndex]);
		
		//Arabic font for phones that did not contains arabic support
		//==================================================================================
		  if(MainActivity.language.equalsIgnoreCase("ar")){
		    	for(int i=0 ; i < allSurahsGoal.length ; i++){
		    		allSurahsGoal[i]= ArabicUtilities.reshapeSentence(allSurahsGoal[i]);
		    		allSurahsReasonOfNaming[i]= ArabicUtilities.reshapeSentence(allSurahsReasonOfNaming[i]);
		    	}
		    	
		    	surahName = ArabicUtilities.reshapeSentence(surahName);

		    	((TextView)findViewById(R.id.goalOfSurahLabel)).setText(ArabicUtilities.reshapeSentence(getString(R.string.goalOfSurahLabel)));
		    	((TextView)findViewById(R.id.reasonOfNamingLabel)).setText(ArabicUtilities.reshapeSentence(getString(R.string.reasonOfNamingLabel)));
		    	((TextView)findViewById(R.id.goalOfSurahTV)).setText(allSurahsGoal[surahIndex]);
		    	((TextView)findViewById(R.id.reasonOfNamingTV)).setText(allSurahsReasonOfNaming[surahIndex]);
		    	
		    }

		setTitle(surahName);
		

	}
	 
		@Override
		  public void onStart() {
		    super.onStart();
		    //EasyTracker.getInstance(this).activityStart(this);  // Google Analytic
		  }

		@Override
		  public void onStop() {
		    super.onStop();
		    //EasyTracker.getInstance(this).activityStop(this);  // Google Analytic
		  }

}

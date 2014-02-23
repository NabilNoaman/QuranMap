package com.quranmap.android;

import java.util.Locale;

import org.amr.arabic.ArabicUtilities;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;

public class About extends SherlockActivity{

	//ListView referancesLV = null;
	//ListView usefulLV = null;
	
	TextView referancesListTV = null;
	TextView usefulLinksListTV =null;
	
	String[] referances = null;
	String[] usefulLinksNames = null;
	String[] usefulLinksURLs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		
		referancesListTV =  (TextView) findViewById(R.id.referancesList);
		usefulLinksListTV =  (TextView) findViewById(R.id.usefuLinksList);
		
		
		
		referances = getResources().getStringArray(R.array.referances);
		usefulLinksNames = getResources().getStringArray(R.array.usefuLinksNames);
		usefulLinksURLs = getResources().getStringArray(R.array.usefuLinksURL);
		
		
			
		
		
		//Arabic font for phones that did not conatins arabic
		//==================================================================================
		  if(MainActivity.language.equalsIgnoreCase("ar")){
			  
			  getSherlock().getActionBar().setTitle(ArabicUtilities.reshapeSentence(getString(R.string.title_activity_about)));
			  for(int i=0 ; i < usefulLinksNames.length ; i++){
				  usefulLinksNames[i]= ArabicUtilities.reshapeSentence(usefulLinksNames[i]);		  
		    	}
		    ((TextView)findViewById(R.id.quranMapGoalLable)).setText(ArabicUtilities.reshapeSentence(getString(R.string.quranMapGoalLableString)));
		    ((TextView)findViewById(R.id.programDescribtionTV)).setText(ArabicUtilities.reshapeSentence(getString(R.string.quranMapDescription)));
		    ((TextView)findViewById(R.id.referancesLabel)).setText(ArabicUtilities.reshapeSentence(getString(R.string.referancesLabelString)));
		    ((TextView)findViewById(R.id.usefuLinksLabel)).setText(ArabicUtilities.reshapeSentence(getString(R.string.usefuLinksLabelString)));
		  
		    for(int i=0 ; i< referances.length ; i++){
		    	referances[i] = ArabicUtilities.reshapeSentence(referances[i]);
			}
		  
		  }
		
		//====================================================================================================
		  String referancesList = "";
			for(int i=0 ; i< referances.length ; i++){
				referancesList = referancesList + "\n" +referances[i]+ "<br>----------<br>";
			}

		  referancesListTV.setText(Html.fromHtml(referancesList));
			
	
		  String usefulLinksList = "";
			for(int i=0 ; i< usefulLinksNames.length ; i++){
				usefulLinksList = usefulLinksList + "\n" + usefulLinksNames[i] + ":<br>" + "\n"+"<a href="+usefulLinksURLs[i] +">"+usefulLinksURLs[i] + "</a>"+"<br>";
				//usefulLinksList = usefulLinksList + "\n" + usefulLinksNames[i] + ":" + "\n"+usefulLinksURLs[i] + "\n----------------";
				
			}
			
			//http://stackoverflow.com/questions/11062508/html-formatting-for-textview
			//tvMyTextView.setText(Html.fromHtml("Click <a href="http://www.poon-world.com">here</a> to switch on the red light.\n"));
			usefulLinksListTV.setText(Html.fromHtml(usefulLinksList));
			
			usefulLinksListTV.setMovementMethod(LinkMovementMethod.getInstance());
			
	
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_about, menu);
		return true;
	}

	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	         
	     /*   
	        case R.id.home:
   			 
    			Intent intent = new Intent(this, MainActivity.class);
    			startActivity(intent);
    			finish();
    			
    			break; 
	       */ 
	            case R.id.share:
	    			
	            	  Intent shareIntent = new Intent();
	      			
	      			shareIntent.putExtra(MainActivity.stringGeneralMap , MainActivity.isGeneralMap);
	      			shareIntent.putExtra(MainActivity.stringSurahNumber , MainActivity.selectedSurahMapNumber);
	      			shareIntent.putExtra(MainActivity.stringSurahName , MainActivity.selectedSurahName);
	      			shareIntent.putExtra(MainActivity.stringMapName , MainActivity.selectedMapName);
	      			     
	      			  
	      			shareIntent.setClass(this, Share.class);
	      			startActivity(shareIntent);
	    			break;
	    			
	    	/*		
	    		case R.id.exit:
	    			 
	    			Intent closeIntent = new Intent(this, MainActivity.class);
	    			closeIntent.putExtra("finish", true);
	    			closeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
	    			startActivity(closeIntent);
	    			finish();
	    			
	    			break; 
	    			*/
	        }

	        return super.onOptionsItemSelected(item);
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

		    
		    System.out.println("about activity , onConfigurationChanged , newConfig.locale:"+newConfig.locale);
		    
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
}

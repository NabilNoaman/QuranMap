package com.quranmap.android;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.amr.arabic.ArabicUtilities;


import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.sonyericsson.tutorial.zoom4.TutorialZoomActivity4;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources.Theme;
import android.text.GetChars;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SherlockActivity implements OnItemSelectedListener {

	//public static String language = Locale.getDefault().getLanguage();
	public static String language = "ar";
	static String selectedSurahMapNumber = "all";
	static String selectedSurahName;
	static String selectedMapName ;
	
	public final static String stringMapName = "MAP_NAME";
	public final static String stringSurahName = "SURAH_NAME";
	public final static String stringSurahNumber = "SURAH_NUMBER";
	public final static String stringGeneralMap = "General_MAP";
	static boolean isGeneralMap = false;
	private Button generalMapButton = null;
	
	
	String[] allSurahsNames = null;
	
	
	final Intent mapIntent = new Intent();
	
	
	 
    public final static String PREFS_PRIVATE_FILE_NAME = "prferances";
	public final static String PREFS_LANG_KEY = "language";
	
	//Arabic font
	//http://mdictionary.wordpress.com/2011/02/10/connected-arabic-characters-for-android-apps/
	//http://blog.amr-gawish.com/39/
	AssetManager manager= null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	/*	
	    // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.quranmap.android",PackageManager.GET_SIGNATURES);
            
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
               // Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                
                System.out.println("KeyHash:"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
                
                }
        } catch (NameNotFoundException e) {
        	e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();

        }
		*/
		//to exit from any activity inside app
		 boolean finish = getIntent().getBooleanExtra("finish", false);
	        if (finish) {
	            finish();
	            return;
	        }
	        
	        
	    //For Arabic Font
	    	try
	    	{
	    	manager=this.getAssets();
	    	manager.open("DejaVuSans.ttf");
	    	//TextView tv=(TextView)this.findViewById(R.id.testMe);
	    	//tv.setTypeface(Typeface.createFromAsset(manager, "tahoma.ttf"));
	    	//tv.setTextSize(50f);
	    	//tv.setText(ArabicUtilities.reshape("adsdads الحمد لله asdad"));
	    	}catch(Exception ex){
	    	  ex.printStackTrace();
	    	}
	    	
	        
	    ///////////////////////////////////    
	    allSurahsNames = getResources().getStringArray(R.array.allSurahNames);
	    	
	   /////////////////////
	    setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
		setContentView(R.layout.activity_main);
		
		
		
		System.out.println("Locale.getDefault().getLanguage():"+Locale.getDefault().getLanguage());
		//System.out.println("Locale.getDefault().getLanguage().getDisplayLanguage():"+Locale.getDefault().getDisplayLanguage());
		
		loadPreferences(getApplicationContext());
		
		//http://developer.android.com/guide/topics/ui/controls/spinner.html
		initializeUI();

	}


	private void initializeUI(){
		//Arabic font for phones that did not conatins arabic
		//==================================================================================
		  if(language.equalsIgnoreCase("ar")){
		    	for(int i=0 ; i < allSurahsNames.length ; i++){
		    		allSurahsNames[i]= ArabicUtilities.reshapeSentence(allSurahsNames[i]);
		    	}
		    	
		    	getSherlock().getActionBar().setTitle(ArabicUtilities.reshapeSentence(getString(R.string.app_name)));
		    	((TextView)findViewById(R.id.chooseSurahTV)).setText(ArabicUtilities.reshapeSentence(getString(R.string.chooseSurah)));
		    	((Button)findViewById(R.id.generalMapButton)).setText(ArabicUtilities.reshapeSentence(getString(R.string.generalMap)));
		    	
		    }
		
		//====================================================================================================
		selectedMapName = getResources().getString(R.string.generalMapNameInEachLangauge);
		
		Spinner allSurahsSpinner = (Spinner) findViewById(R.id.allSurahSpinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		//ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.allSurahNames, android.R.layout.simple_spinner_item);
		
		
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, allSurahsNames);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		allSurahsSpinner.setAdapter(adapter);
		//listener
		allSurahsSpinner.setOnItemSelectedListener(this);
		
		
		//Language
		Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(this,
		        R.array.allLanguage, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		languageSpinner.setAdapter(languageAdapter);
		//listener
		languageSpinner.setOnItemSelectedListener(this);
		//For Arabic Only
		//languageSpinner.setVisibility(View.GONE);
		
		
		//general Map button
		generalMapButton = (Button) findViewById(R.id.generalMapButton);
		generalMapButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				isGeneralMap = true;
			/*	String */ selectedMapName = language+"_"+"all";
				
				System.out.println(" Activity 1, Selected general Map :"+selectedMapName);
				mapIntent.putExtra(stringMapName, selectedMapName);
				mapIntent.putExtra(stringGeneralMap, isGeneralMap);
				
				// Navigate to map Activity
				mapIntent.setClass(MainActivity.this, TutorialZoomActivity4.class);
				startActivity(mapIntent);
				
			}});

		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.share:
			
            Intent shareIntent = new Intent();
			
			shareIntent.putExtra(MainActivity.stringGeneralMap , isGeneralMap);
			shareIntent.putExtra(MainActivity.stringSurahNumber , selectedSurahMapNumber);
			shareIntent.putExtra(MainActivity.stringSurahName , selectedSurahName);
			shareIntent.putExtra(MainActivity.stringMapName , selectedMapName);
			     
			  
			shareIntent.setClass(this, Share.class);
			
			startActivity(shareIntent);
			return true;
			
		case R.id.about:
			
			Intent aboutIntent = new Intent();
			aboutIntent.setClass(MainActivity.this, About.class);
			startActivity(aboutIntent);
			
			return true;
			
		case R.id.exit:
			 finish();
			return true;
		
		
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)  {
		// An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		System.out.println("Activity 1 , slected index:"+pos);
		System.out.println("Activity 1 ,parent.getCount():"+parent.getCount());
		System.out.println("Activity 1 ,parent.getId():"+parent.getId());
		
		
		
		//i.e if one of Surah choosed from surahs spinner
		if(parent.getCount() == 115){
		if(pos > 0){
			
			selectedSurahMapNumber = pos+"";

			//get Surah name to be shown in Action Bar when map will be shown
			
			
			isGeneralMap = false;
			mapIntent.putExtra(stringGeneralMap, isGeneralMap);
			selectedSurahName = allSurahsNames[pos];
			mapIntent.putExtra(stringSurahName, selectedSurahName);
			mapIntent.putExtra(stringSurahNumber, selectedSurahMapNumber);//to be used in details activity
			
			
			if(pos == 2)
			{
				showAlbaqarahDialog();
		
				
			}else{
			
			
			
			
		    selectedMapName = language+"_"+selectedSurahMapNumber;
			System.out.println(" Activity 1, Selected map name :"+selectedMapName);
			mapIntent.putExtra(stringMapName, selectedMapName);
			
			// Navigate to map Activity
				//i.setClass(MainActivity.this, MapFullscreenActivity.class);
			mapIntent.setClass(MainActivity.this, TutorialZoomActivity4.class);
				startActivity(mapIntent);
			}
		}
		}else//i.e language choosed from language spinner
		{
			//For Arabic Only
			
			if(pos>0)
			{
			String[] allLangAppreviation = getResources().getStringArray(R.array.allLanguageAbbreviation);
			language= allLangAppreviation[pos-1];
			System.out.println("Langauge Selected:"+language);
			
			  Configuration newConfig = new Configuration();
			  newConfig.locale =new Locale(language);
			  System.out.println("newConfig.locale:"+newConfig.locale);
	          
	          //write slected lang to shared preferances
	          writeLanguageToSharedPreferences(getApplicationContext(), language);
	        
	          onConfigurationChanged(newConfig);
		        
	          
	          getSherlock().dispatchInvalidateOptionsMenu();
			
			}
			
			
		}
	}

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    //newConfig.setLocale(new Locale(language));
	    newConfig.locale =new Locale(language);
	    
	    getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
	    setContentView(R.layout.activity_main);
	    
	    setTitle(R.string.app_name);

	    
	    System.out.println("onConfigurationChanged , newConfig.locale:"+newConfig.locale);
	    
	    /*
	    if(language.equalsIgnoreCase("ar")){
	    	for(int i=0 ; i < allSurahsNames.length ; i++){
	    		allSurahsNames[i]= ArabicUtilities.reshapeSentence(allSurahsNames[i]);
	    	}
	    	
	    	getSherlock().getActionBar().setTitle(ArabicUtilities.reshapeSentence(getString(R.string.app_name)));
	    	((TextView)findViewById(R.id.chooseSurahTV)).setText(ArabicUtilities.reshapeSentence(getString(R.string.chooseSurah)));
	    	((Button)findViewById(R.id.generalMapButton)).setText(ArabicUtilities.reshapeSentence(getString(R.string.generalMap)));
	    	
	    }
	    */
	    
	    initializeUI();
	    
	 
	}
	
	
	
	public  void showAlbaqarahDialog(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        //Dialog Title
        alertDialog.setTitle(R.string.alBaqarah_TitleDialog);
       
        selectedSurahMapNumber = "2_all";/*default selected*/
        
        String[] albaqarahlList = getResources().getStringArray(R.array.albaqarahList);
        if(language.equalsIgnoreCase("ar")){
        	
        	 alertDialog.setTitle(ArabicUtilities.reshapeSentence(getString(R.string.alBaqarah_TitleDialog)));
        	for(int i=0 ; i < albaqarahlList.length ; i++){
        		albaqarahlList[i]= ArabicUtilities.reshapeSentence(albaqarahlList[i]);
	    	}
        }
        //Dialog Message
        alertDialog.setSingleChoiceItems(albaqarahlList , 3/*default selected*/, new DialogInterface.OnClickListener() {
        	
            public void onClick(DialogInterface dialog,int which) {

            	System.out.println("Activity 1 ,setSingleChoiceItems , which:"+which);
            	if(which == 3)
            	{
            		selectedSurahMapNumber = "2_all";
            	}
            	else
            	{
            		selectedSurahMapNumber = "2"+"_"+(which+1);
            	}
            	
            	
             }
        });
       
        
        
        // Icon to Dialog
        //alertDialog.setIcon(R.drawable.error);
 
        // On pressing OK button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        	
            public void onClick(DialogInterface dialog,int which) {
            	
            	//Toast.makeText(getApplicationContext(),"OK pressed", Toast.LENGTH_LONG).show();
                dialog.dismiss();
              /*  String */ selectedMapName = language+"_"+selectedSurahMapNumber;
				System.out.println(" Activity 1, Selected map name :"+selectedMapName);
				//Intent i= new Intent();
			
				mapIntent.putExtra(stringMapName, selectedMapName);
				// Navigate to map Activity
					//i.setClass(MainActivity.this, MapFullscreenActivity.class);
				mapIntent.setClass(MainActivity.this, TutorialZoomActivity4.class);
					startActivity(mapIntent);
				
                
             }
        });
        
        
        alertDialog.show();
		
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
		
	}
	
	//To store prefered language of user
	 static void writeLanguageToSharedPreferences(Context context ,String lang)
	{
		SharedPreferences prefsPrivate = context.getSharedPreferences( PREFS_PRIVATE_FILE_NAME , Context.MODE_PRIVATE);
		Editor editor = prefsPrivate.edit();
		
		// variables needed to be persist
     	if(lang != null)
		{
			editor.putString(PREFS_LANG_KEY, lang);
		}else{
			//i.e lang =null
			editor.putString(PREFS_LANG_KEY , "");
		}


		editor.commit();
	}
	
	 void loadPreferences(Context context) {
		// Get the stored preferences	
		SharedPreferences prefsPrivate = context.getSharedPreferences( PREFS_PRIVATE_FILE_NAME , Context.MODE_PRIVATE);
		
		//For Arabic Only
		// Retrieve the saved values.	 
		//language = prefsPrivate.getString(PREFS_LANG_KEY , Locale.getDefault().getLanguage());//get default lang if app used for first time and did not conatins prefered lang before		
		language = prefsPrivate.getString(PREFS_LANG_KEY , "ar");//For Arabic Only
		
		System.out.println("loadPreferences(), language: "+language);
		
		if(!language.equalsIgnoreCase(Locale.getDefault().getLanguage())){
			//Locale.setDefault(new Locale(language));
			 Configuration newConfig = new Configuration();
			  newConfig.locale =new Locale(language);
			  System.out.println("newConfig.locale:"+newConfig.locale);
	          
	          //write slected lang to shared preferances
	          writeLanguageToSharedPreferences(getApplicationContext(), language);
	        
	          onConfigurationChanged(newConfig);
		        
	          
	          getSherlock().dispatchInvalidateOptionsMenu();
		
		}

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

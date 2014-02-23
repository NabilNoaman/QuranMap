package com.quranmap.android;

import java.util.Arrays;
import java.util.Locale;

import org.amr.arabic.ArabicUtilities;


import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;




import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;


public class Share extends SherlockActivity {

	
	//facebook
	private UiLifecycleHelper uiHelper;
		
	LoginButton login_facebook_button = null;
	
	
	private ProfilePictureView profilePictureView;
	private TextView userNameView;
	private Button postOnFacebookButton;
	
	
	String surahNumber;
	String surahName;
    int surahIndex = -1;
	
	String mapURL ;
	String description;

	String mapName;

	boolean isGeneralMap;

	 Drawable map = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		try{
		initilaiztionForMapURL_Description();		 
		}
		catch (Exception e) {
			// TODO: handle exception
			mapURL = getResources().getString(R.string.generalMapURLOnFacebook);
			description = getResources().getString(R.string.quranMapDescription);
			
		}
		
		//check if there is internet connection
		if(!isThereAreInternetConnection()){
		
			showSettingsAlertForInternet();
		
		}
		//*****************************
		
		login_facebook_button =  (LoginButton) findViewById(R.id.facebook_login_button);
		//update by nabil to use web view dialog only and did not use Facebook Android app if installed
	    login_facebook_button.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
	    
	    uiHelper = new UiLifecycleHelper( this , callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	 // Find the user's profile picture custom view
	 		profilePictureView = (ProfilePictureView)findViewById(R.id.profilePicture);
	 		profilePictureView.setCropped(true);

	 		// Find the user's name view
	 		userNameView = (TextView)findViewById(R.id.userName);
	 		postOnFacebookButton = (Button) findViewById(R.id.postStatusUpdateButton);
	 		postOnFacebookButton.setOnClickListener(new View.OnClickListener() {
	 		    @Override
	 		    public void onClick(View v) {
	 		        // Trigger the Facebook feed dialog
	 				//https://developers.facebook.com/docs/reference/dialogs/feed/
	 		    	//int idOfMap =getApplicationContext().getResources().getIdentifier(mapName, "drawable", getApplicationContext().getPackageName());
	 			    // map = getResources().getDrawable(idOfMap);
	 		        facebookFeedDialog(surahName, mapURL, description );
	 		        
	 		        //Google Analytic Track Event 
	 		        //Here we want to track share on Facebook Event
	 		       //https://developers.google.com/analytics/devguides/collection/android/v3/events
	 		       EasyTracker easyTracker = EasyTracker.getInstance(getApplicationContext());

	 		      // MapBuilder.createEvent().build() returns a Map of event fields and values
	 		      // that are set and sent with the hit.
	 		      easyTracker.send(MapBuilder
	 		          .createEvent("ui_action",                  // Event category (required)
	 		                       "button_press",               // Event action (required)
	 		                       "facebook_share_button",      // Event label
	 		                       Long.parseLong(surahNumber))  // Event value , 
	 		          .build()
	 		      );

	 		    }
	 		});
	 		
		
	 		//Arabic font for phones that did not conatins arabic
			//==================================================================================
			  if(MainActivity.language.equalsIgnoreCase("ar")){
			    	
			    	surahName = ArabicUtilities.reshapeSentence(surahName);
			    	 getSherlock().getActionBar().setTitle(ArabicUtilities.reshapeSentence(getString(R.string.title_activity_share)));
			    	 postOnFacebookButton.setText(ArabicUtilities.reshapeSentence(getString(R.string.facebookShareButton)));
			    	
			    	
			    }
			
			//====================================================================================================
		
				
	}

	private void initilaiztionForMapURL_Description() {
		// TODO Auto-generated method stub

		Intent i = getIntent();
		 mapName     = i.getStringExtra(MainActivity.stringMapName);//it is important only incase Albaqarah to know which 
		 surahName   = i.getStringExtra(MainActivity.stringSurahName);
		 surahNumber = i.getStringExtra(MainActivity.stringSurahNumber);
		 isGeneralMap= i.getBooleanExtra(MainActivity.stringGeneralMap , false);
		System.out.println(" Activity Share , Selected map name :"+mapName);
		System.out.println(" Activity Share , Selected Surah name :"+surahName);
	  	//to change ActionBar title to be with surah name

	
		if (isGeneralMap){
			
			mapURL = getResources().getString(R.string.generalMapURLOnFacebook);
			description = getResources().getString(R.string.quranMapDescription);
			
			return;
		}
		
		
		 surahIndex = Integer.parseInt(surahNumber)-1;
		
		 String[]	allSurahsGoal = getResources().getStringArray(R.array.goalOfSurah);
		 String[]	allSurahsReasonOfNaming = getResources().getStringArray(R.array.reasonForNaming);
		 
		 description = getResources().getString(R.string.goalOfSurahLabel)+":"+allSurahsGoal[surahIndex] + "\t\n" + getResources().getString(R.string.reasonOfNamingLabel)+":"+allSurahsReasonOfNaming[surahIndex];
		 if(surahIndex == 1) //i.e Albaqarah
		 {
			 String[] allSurahMapURLOnFacebook = getResources().getStringArray(R.array.surahMapURLOnFacebook_AlbaqarahList);
		    
			 String[] albaqarahList = {"2_1","2_2","2_3","2_all"};
			 
			 String mapNameSubString = mapName.substring(3);//ar_2_1 , ar_2_2 ,ar_2_3 , ar_2_all , we remove language abbreviation plus _
			 
			 int idexOFMapURLOfAlbaqarah = Arrays.binarySearch(albaqarahList, mapNameSubString);
			 
			 mapURL = allSurahMapURLOnFacebook[idexOFMapURLOfAlbaqarah];
			 
		 }else{
		     String[] allSurahMapURLOnFacebook = getResources().getStringArray(R.array.surahMapURLOnFacebook);
		     mapURL = allSurahMapURLOnFacebook[surahIndex];
		 }
		 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.activity_share, menu);
		return true;
	}
	
	
	   @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            
	        case R.id.about:
				
	        	Intent aboutIntent = new Intent();
				
				aboutIntent.putExtra(MainActivity.stringGeneralMap , isGeneralMap);
				aboutIntent.putExtra(MainActivity.stringSurahNumber , surahNumber);
				aboutIntent.putExtra(MainActivity.stringSurahName , surahName);
				aboutIntent.putExtra(MainActivity.stringMapName , mapName);
				     
				
				aboutIntent.setClass(this, About.class);
				startActivity(aboutIntent);
				
				break;
	    
	        }

	        return super.onOptionsItemSelected(item);
	   }
	//Facebook
		private Session.StatusCallback callback = new Session.StatusCallback() {
		    @Override
		    public void call(Session session, SessionState state, Exception exception) {
		        onSessionStateChange(session, state, exception);
		    }
		};

		//to prevent launching EventsActivity more than once when onSessionStateChange called > 1
		//private boolean isFirstTimeToEnterHere = true;
		
		private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		    if (state.isOpened()) {
		       // Log.i(TAG, "Logged in...");
		   
		    	
		    	System.out.println("Logged in...");
		    		
		    
		          // make request to the /me API
		          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {


					// callback after Graph API response with user object
		            public void onCompleted(GraphUser user, Response response) {
		              if (user != null) {
		               
		            	  Toast.makeText(getApplicationContext(),"Logged in...", Toast.LENGTH_LONG).show();
		            	  
		       
		                String name = user.getName();
		                System.out.println("Name:"+name);
		                String userName = user.getUsername();
		                System.out.println("userName:"+userName);
		               
		               // userNameView.setText("Hello " + name + "!"+"\nUserName:"+userName);
		                userNameView.setEnabled(true);
		                postOnFacebookButton.setEnabled(true);
		                profilePictureView.setEnabled(true);
		       
						
		                // Set the id for the ProfilePictureView
	                    // view that in turn displays the profile picture.
	                    profilePictureView.setProfileId(user.getId());
	                    // Set the Textview's text to the user's name.
	                    userNameView.setText(user.getName());
		                
		         
		              }
		            }
		            
		            
		          });
		          
		          
		         
		    
		    } else if (state.isClosed()) {
		      //  Log.i(TAG, "Logged out...");
		    	System.out.println("Logged out...");
		    	userNameView.setEnabled(false);
                postOnFacebookButton.setEnabled(false);
                profilePictureView.setEnabled(false);
		    }
		}
		
		@Override
		public void onResume() {
		    super.onResume();
		    
		 // For scenarios where the main activity is launched and user
		    // session is not null, the session state change notification
		    // may not be triggered. Trigger it if it's open/closed.
		    Session session = Session.getActiveSession();
		    if (session != null &&
		           (session.isOpened() || session.isClosed()) ) {
		        onSessionStateChange(session, session.getState(), null);
		    }

		    uiHelper.onResume();
		   
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);
		    uiHelper.onActivityResult(requestCode, resultCode, data);
		}

		@Override
		public void onPause() {
		    super.onPause();
		    uiHelper.onPause();
		}

		@Override
		public void onDestroy() {
		    super.onDestroy();
		    uiHelper.onDestroy();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
		    super.onSaveInstanceState(outState);
		    uiHelper.onSaveInstanceState(outState);
		}

		
		
		private void facebookFeedDialog(String surahName, String mapURL ,String description) {
			//https://developers.facebook.com/docs/reference/dialogs/feed/
		    // Set the dialog parameters
		    Bundle params = new Bundle();
		    params.putString("name", getResources().getString(R.string.app_name));
		    params.putString("caption", surahName);
		    params.putString("description", description);
		    params.putString("link", mapURL);
		  //  params.putString("picture", "pic.twitter.com/hZdtKTEC");

		    // Invoke the dialog
		    WebDialog feedDialog = (
		        new WebDialog.FeedDialogBuilder(this,Session.getActiveSession(),params))
		        .setOnCompleteListener(new OnCompleteListener() {
		            @Override
		            public void onComplete(Bundle values, FacebookException error) {
		                if (error == null) {
		                    // When the story is posted, echo the success
		                    // and the post Id.
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                        Toast.makeText(Share.this,
		                         "Shared ,postId : "+postId,
		                        Toast.LENGTH_SHORT).show();
		                    }
		                }
		            }

		        })
		        .build();
		    feedDialog.show();
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

			    
			    System.out.println("share activity , onConfigurationChanged , newConfig.locale:"+newConfig.locale);
			    
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
		
		 private boolean isThereAreInternetConnection(){
		    	ConnectivityManager manager = (ConnectivityManager)getSystemService(this.CONNECTIVITY_SERVICE);

		        /*
		         * 3G confirm
		         */
		        Boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();// isConnectedOrConnecting();

		        System.out.println("In onCreate in Thread,checking internet ,is3g: "+is3g);
		        /*
		         * wifi confirm
		         */
		        Boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		        System.out.println("In onCreate in Thread,checking internet ,isWifi: "+isWifi);

		        if(is3g | isWifi)
		        {
		        	//Ok and do nothing
		            return true;
		        }else {
		        	//there is no internet activity and return false to let app show dialog to enable interner
		        	 return false;
		        }    
		           
		        
		    }
		 
		    private void showSettingsAlertForInternet(){
		        
		    	//System.out.println("In onCreate in Thread, showSettingsAlertForInternet() ");
		    	//AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
		    	//getApplicationContext() fire exception"Unable to add window - token null is not for an application"
		    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(Share.this);
		 
		    	String dialogTitle = getString(R.string.show_internet_settings_dialog_title);
		    	String dialogMessage= getString(R.string.show_internet_settings_dialog_message);
		    	String positiveButton= getString(R.string.show_internet_settings_dialog_positive_button);
		    	String negativeButton= getString(R.string.show_internet_settings_dialog_negative_button);
		    	
		    	//Arabic font for phones that did not conatins arabic
				//==================================================================================
				  if(MainActivity.language.equalsIgnoreCase("ar")){
				    	
					  dialogTitle = ArabicUtilities.reshapeSentence(dialogTitle);
					  dialogMessage = ArabicUtilities.reshapeSentence(dialogMessage);
					  positiveButton = ArabicUtilities.reshapeSentence(positiveButton);
					  negativeButton = ArabicUtilities.reshapeSentence(negativeButton);
					  
				    	
				    }
				
				//====================================================================================================
			
		        // Setting Dialog Title
		        alertDialog.setTitle(dialogTitle);
		 
		        // Setting Dialog Message
		        alertDialog.setMessage(dialogMessage);
		 
		        // Internet Setting Icon to Dialog
		        alertDialog.setIcon(R.drawable.error);
		 
		        // On pressing Settings button
		        alertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
		        	
		            public void onClick(DialogInterface dialog,int which) {
		            	
		            	//Toast.makeText(getApplicationContext(),"Settings pressed", Toast.LENGTH_LONG).show();
		            	//android.settings.WIRELESS_SETTINGS
		                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
		            	//Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		               // getApplicationContext().startActivity(intent);
		               // SplachActivity.this.startActivityForResult(intent, 0);
		                Share.this.startActivity(intent);
		             }
		        });
		 
		        // on pressing cancel button
		        alertDialog.setNegativeButton(negativeButton , new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            	//Toast.makeText(getApplicationContext(),"Cancel pressed", Toast.LENGTH_LONG).show();
		            	
		            dialog.cancel();
		            finish();
		            
		            }
		        });
		 
		        // Showing Alert Message
		        alertDialog.show();
		        System.out.println("In onCreate in Thread, showSettingsAlertForInternet()  after show");
		      
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

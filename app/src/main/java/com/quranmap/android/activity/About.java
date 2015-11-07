package com.quranmap.android.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.quranmap.android.R;

import org.amr.arabic.ArabicUtilities;

public class About extends AppCompatActivity{

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

		getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.actionbar_background));

		referancesListTV =  (TextView) findViewById(R.id.referancesList);
		usefulLinksListTV =  (TextView) findViewById(R.id.usefuLinksList);
		
		
		
		referances = getResources().getStringArray(R.array.referances);
		usefulLinksNames = getResources().getStringArray(R.array.usefuLinksNames);
		usefulLinksURLs = getResources().getStringArray(R.array.usefuLinksURL);
		
		
			
		
		
		//Arabic font for phones that did not conatins arabic
		//==================================================================================
		  if(MainActivity.language.equalsIgnoreCase("ar")){
			  
			  setTitle(ArabicUtilities.reshapeSentence(getString(R.string.title_activity_about)));
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
	    //EasyTracker.getInstance(this).activityStart(this);  // Google Analytic
	  }

	@Override
	  public void onStop() {
	    super.onStop();
	    //EasyTracker.getInstance(this).activityStop(this);  // Google Analytic
	  }

}

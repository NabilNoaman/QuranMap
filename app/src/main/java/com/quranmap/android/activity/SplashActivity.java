package com.quranmap.android.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.appsee.Appsee;
import com.quranmap.android.R;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends Activity {

	protected int _WaitTime = 1000;
    private Thread splashTread;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		Appsee.start(getString(R.string.com_appsee_apikey));
		setContentView(R.layout.activity_splash);
		
		
		final SplashActivity splashActivity = this;
		
		// thread for displaying the SplashScreen
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(_WaitTime);
					}

				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				finally {
				
					finish();

					Intent i = new Intent();
					i.setClass(splashActivity, MainActivity.class);
					startActivity(i);
					
					return;
				}
			}
		};

		splashTread.start();
	}



}

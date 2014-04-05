package com.quranmap.android;


import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class SplashActivity extends Activity {

	protected int _WaitTime = 1000;
    private Thread splashTread;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
					
					return;// because stop() is deprecated on Android 4
					// stop();
				}
			}
		};

		splashTread.start();
	}



}

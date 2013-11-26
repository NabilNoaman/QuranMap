package com.quranmap.android;



import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

public class AlertDialogManager {
	/**
	 * Function to display simple Alert Dialog
	 * @param context - application context
	 * @param title - alert dialog title
	 * @param message - alert message
	 * @param status - success/failure (used to set icon)
	 * 				 - pass null if you don't want icon
	 * */
	private static AlertDialog alertDialog = null;
	
	@SuppressLint("NewApi")
	public void showAlertDialog(Context context, String title, String message,
			Boolean status) {
		//AlertDialog alertDialog = null;//new AlertDialog.Builder(context).create();
		
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) { 
				
			  alertDialog =  new AlertDialog.Builder(context,R.style.Theme_Sherlock_Dialog).create();
		  }else{
			  alertDialog =  new AlertDialog.Builder(context).create();
		  }

		// Setting Dialog Title
		alertDialog.setTitle(title);

		// Setting Dialog Message
		alertDialog.setMessage(message);

		if(status != null)
			// Setting alert dialog icon
			alertDialog.setIcon((status) ? R.drawable.about : R.drawable.error);

		// Setting OK Button
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
			}
		});

		// Showing Alert Message
		alertDialog.show();
	}
}

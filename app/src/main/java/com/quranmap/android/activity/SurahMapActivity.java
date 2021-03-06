package com.quranmap.android.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quranmap.android.R;

import org.amr.arabic.ArabicUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.co.senab.photoview.PhotoViewAttacher;

public class SurahMapActivity extends AppCompatActivity {

    private ImageView mapIV;
    private PhotoViewAttacher mAttacher;

    private int selectedSurahIndex = -1;
    private int mapResourceID = R.drawable.splash;

    private String surahName;
    private String[] allSurahsGoal = null;
    private String[] allSurahsReasonOfNaming =null;

    private TextView goalOfSurahTV    = null;
    private TextView reasonOfNamingTV = null;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surah_map);

        getSupportActionBar().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.actionbar_background));

        mapIV = (ImageView) findViewById(R.id.surah_map);
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(mapIV);

        loadSelectedSurahMap(getIntent());
    }

    private void loadSelectedSurahMap(Intent intent) {
        String mapNameAsInDrawable = "";


        SharedPreferences sp;
        SharedPreferences.Editor editor;
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();


        selectedSurahIndex = intent.getIntExtra(MainActivity.SELECTED_SURAH_INDEX_KEY, -1);

        //If selectedSurahIndex -1 i.e we back from Details/About Activity so read the selectedSurahIndex from SharedPrefernces
        if(selectedSurahIndex != -1){
            mapNameAsInDrawable = intent.getStringExtra(MainActivity.SELECTED_SURAH_MAP_NAME_KEY);
            //Write selectedSurahIndex and mapNameAsInDrawable to SharedPreferecnes
             editor.putInt(MainActivity.SELECTED_SURAH_INDEX_KEY , selectedSurahIndex);
             editor.putString(MainActivity.SELECTED_SURAH_MAP_NAME_KEY, mapNameAsInDrawable);
             editor.commit();
        }else {
            //Back from  Details/About Activity via up button
            selectedSurahIndex = sp.getInt(MainActivity.SELECTED_SURAH_INDEX_KEY , 0);
            mapNameAsInDrawable = sp.getString(MainActivity.SELECTED_SURAH_MAP_NAME_KEY ,"ar_all");
        }

        surahName = MainActivity.allSurahsNames[selectedSurahIndex];
        mapResourceID = getMapResourceID(mapNameAsInDrawable);
        mapIV.setImageResource(mapResourceID);
        mAttacher.update();

        //------


        allSurahsGoal = getResources().getStringArray(R.array.goalOfSurah);
        allSurahsReasonOfNaming = getResources().getStringArray(R.array.reasonForNaming);

        goalOfSurahTV = (TextView) findViewById(R.id.goalOfSurahTV);
        reasonOfNamingTV = (TextView) findViewById(R.id.reasonOfNamingTV);

        goalOfSurahTV.setText(allSurahsGoal[selectedSurahIndex]);
        reasonOfNamingTV.setText(allSurahsReasonOfNaming[selectedSurahIndex]);

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
            goalOfSurahTV.setText(allSurahsGoal[selectedSurahIndex]);
            reasonOfNamingTV.setText(allSurahsReasonOfNaming[selectedSurahIndex]);

        }


        //-------


        setTitle(surahName);

    }


    private int getMapResourceID(String mapNameAsInDrawable){
        try{

            mapResourceID = getResources().getIdentifier(mapNameAsInDrawable, "drawable", this.getPackageName());
            return mapResourceID;
        }
        catch (Exception e) {

            //map photo not exist with this name
            e.printStackTrace();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SurahMapActivity.this);
            //Dialog Title
            alertDialog.setTitle(R.string.noCurrentMapExist_TitleDialog);

            //Dialog Message
            alertDialog.setMessage(R.string.noCurrentMapExist);

            // On pressing OK button
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                    //go back to main activity
                    finish();
                }
            });

            alertDialog.show();
            return R.mipmap.ic_launcher;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_surah_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_share) {
            setShareIntent();
            return true;
        }

        if (id == R.id.action_extract) {
            //first check if API version support access to SD card or not
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO)
            {// then check if SDK version requires request for permissions (sdk 23 and higher)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Then check if permission was granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        createExternalStoragePublicPicture(MainActivity.allSurahsNames[selectedSurahIndex] , mapResourceID);
                    }
                } else {
                    createExternalStoragePublicPicture(MainActivity.allSurahsNames[selectedSurahIndex] , mapResourceID);
                }

            }else{
                //display dialog indicates this version of android does not support required feature
                indicateToUserFeatureNotAvailable();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setShareIntent() {
        // Get access to the URI for the bitmap
        Uri bmpUri = getLocalBitmapUri(mapIV);
        // Construct a ShareIntent with link to image
        Intent shareIntent = new Intent();
        // Construct a ShareIntent with link to image
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("*/*");
        StringBuilder shareString = new StringBuilder(getResources().getString(R.string.surah_label)+ " " +surahName);
        if( allSurahsGoal[selectedSurahIndex] != null && allSurahsGoal[selectedSurahIndex].length() != 0 ) {
            shareString.append("\n\n"+ getString(R.string.goalOfSurahLabel) + "\n" + allSurahsGoal[selectedSurahIndex]);
        }

        if( allSurahsReasonOfNaming[selectedSurahIndex] != null && allSurahsReasonOfNaming[selectedSurahIndex].length() !=0 ){
            shareString.append("\n\n"+ getString(R.string.reasonOfNamingLabel) + "\n" + allSurahsReasonOfNaming[selectedSurahIndex]);
        }

        // url for Quran Map on Google Play
        String googlePlayLinkForAPP = getResources().getString(R.string.google_play_app_link);
        String sharedBy = getResources().getString(R.string.shared_by)+ "\n" + googlePlayLinkForAPP;

        shareString.append( "\n\n" + sharedBy);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString.toString());
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        // Launch share menu
        startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)));
    }

    // Returns the URI path to the Bitmap displayed in cover imageview
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to Picture directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES  + "/QuranMap/"), "Quran Map_"+surahName+".png");

            file.getParentFile().mkdirs();


            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }


    //========================================Extract to external SD memory
    void createExternalStoragePublicPicture(String mapName , int resourceOfMap) {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/QuranMap/");
        File file = new File(path, mapName+".png");

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
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.map_image_extracted)/* + " in Path:" + file.getPath()*/, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Toast.makeText(getApplicationContext(),"Unable to extract current Map , because :"+e.toString(), Toast.LENGTH_LONG).show();
            Log.w("ExternalStorage", "Error writing " + file, e);
            e.printStackTrace();
        }
    }

    private void indicateToUserFeatureNotAvailable(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //Dialog Title
        alertDialog.setTitle(R.string.title_feature_Not_Available_Dialog);

        //Dialog Message
        alertDialog.setMessage(R.string.message_feature_Not_Available_Dialog);

        // On pressing OK button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted! Do the task
                    createExternalStoragePublicPicture(MainActivity.allSurahsNames[selectedSurahIndex] , mapResourceID);

                } else {
                    Toast.makeText(getApplicationContext(), R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}

package com.quranmap.android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.quranmap.android.R;

import org.amr.arabic.ArabicUtilities;

public class MainActivity extends AppCompatActivity {

    final static String SELECTED_SURAH_INDEX_KEY = "selectedSurahIndex";
    final static String SELECTED_SURAH_MAP_NAME_KEY = "selectedSurahMapName";
    public static String language = "ar";

    static String[] allSurahsNames;
    private ListView surahLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allSurahsNames = getResources().getStringArray(R.array.allSurahNames);

        setupActionBar();
        setupSurahListView();
    }

    private void setupSurahListView() {
        surahLV = (ListView) findViewById(R.id.surahListView);
        // Binding resources Array to ListAdapter
        surahLV.setAdapter(new ArrayAdapter<String>(this, R.layout.surah_item, R.id.surah_name, allSurahsNames));
        surahLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the Map view passing selected Surah Index as an extra
                Intent intent = new Intent(MainActivity.this, SurahMapActivity.class);
                intent.putExtra(SELECTED_SURAH_INDEX_KEY, position);

                String selectedSurahMapNumber = (position+1)+ "";
                if (position == 1) {
                    //selectedSurahMapNumber = showAlbaqarahDialog();
                    selectedSurahMapNumber = "2_all";
                }

                String mapNameAsInDrawable = language + "_" + selectedSurahMapNumber;
                intent.putExtra(SELECTED_SURAH_MAP_NAME_KEY, mapNameAsInDrawable);

                startActivity(intent);
            }
        });

    }

    private String showAlbaqarahDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        //Dialog Title
        alertDialog.setTitle(R.string.alBaqarah_TitleDialog);

        final String[] selectedSurahMapNumber = {"2_all"};/*default selection*/

        String[] albaqarahlList = getResources().getStringArray(R.array.albaqarahList);
        if(language.equalsIgnoreCase("ar")){

            alertDialog.setTitle(ArabicUtilities.reshapeSentence(getString(R.string.alBaqarah_TitleDialog)));
            for(int i=0 ; i < albaqarahlList.length ; i++){
                albaqarahlList[i]= ArabicUtilities.reshapeSentence(albaqarahlList[i]);
            }
        }
        //Dialog Message
        alertDialog.setSingleChoiceItems(albaqarahlList, 3 /* default selected*/, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                if (which == 3) {
                    selectedSurahMapNumber[0] = "2_all";
                } else {
                    selectedSurahMapNumber[0] = "2" + "_" + (which + 1);
                }


            }
        });


        // On pressing OK button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog,int which) {

                //Toast.makeText(getApplicationContext(),"OK pressed", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                selectedSurahMapNumber[0] = language+"_"+ selectedSurahMapNumber[0];

            }
        });


        alertDialog.show();

        return selectedSurahMapNumber[0];
    }


    private void setupActionBar() {

        final View customView = LayoutInflater.from(this).inflate( R.layout.main_activity_action_bar , null);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        ActionBar.LayoutParams params = new
        ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
        ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        actionBar.setCustomView(customView, params);
        //Remove the thin strip on the left which appear in Android L
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

    }


}

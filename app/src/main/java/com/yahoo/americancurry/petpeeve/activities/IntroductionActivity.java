package com.yahoo.americancurry.petpeeve.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yahoo.americancurry.petpeeve.R;
import com.yahoo.americancurry.petpeeve.views.TypeWriter;

public class IntroductionActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_introduction);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        TypeWriter typeWriter = (TypeWriter)findViewById(R.id.view);

        //Add a character every 150ms
        typeWriter.setCharacterDelay(100);
        typeWriter.animateText("Pin Cards . . .");
    }

    public void onAnnimationOver() {
        Intent intent = new Intent(this, PinListActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_introduction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

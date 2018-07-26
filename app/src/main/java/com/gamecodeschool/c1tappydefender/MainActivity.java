package com.gamecodeschool.c1tappydefender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getApplicationContext();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(TDView.HIGH_SCORES, MODE_PRIVATE);
        long fastestTime = sharedPreferences.getLong(TDView.FASTEST_TIME, 100000);

        final Button playButton = (Button) findViewById(R.id.play_button);
        playButton.setOnClickListener(this);

        final TextView fastestTimeTextView = (TextView) findViewById(R.id.fastest_time_textview);
        fastestTimeTextView.setText(getString(R.string.fastest_time, fastestTime));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
    }
}

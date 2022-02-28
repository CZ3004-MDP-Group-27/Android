package com.example.mdp.modes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdp.R;
import com.example.mdp.SplashActivity;
import com.example.mdp.bluetooth.BTService;
import com.example.mdp.bluetooth.messageActivity;

public class FastestModeActivty extends AppCompatActivity {
    Button startStopwatch, stopStopwatch, resetStopwatch;
    TextView stopwatchText, statusText;
    private boolean isResume;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int milliSec, sec, min;
    Context context;
    String command, outgoing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastest_mode);
        context = this;

        stopwatchText = findViewById(R.id.stopwatchText);
        startStopwatch = findViewById(R.id.startStopwatch);
        stopStopwatch = findViewById(R.id.stopStopwatch);
        resetStopwatch = findViewById(R.id.resetStopwatch);
        statusText = findViewById(R.id.statusText);
        statusText.setText(SplashActivity.status);
        handler = new Handler();

        if (BTService.BluetoothConnectionStatus) {
            startStopwatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FASTEST MODE","INITIATE FAST CAR CHALLENGE MODE");
                    command = "startFast";
                    BTService.sendMessage(command);
                    outgoing = "twenty-seven: " + command + "\n";
                    messageActivity.chatHistory += outgoing;
                    if (!isResume) {
                        stopwatchText.setText("");
                        tStart = SystemClock.uptimeMillis();
                        handler.postDelayed(runnable, 0);
                        isResume = true;
                        startStopwatch.setVisibility(View.GONE);
                        stopStopwatch.setVisibility(View.VISIBLE);
                    }
                }
            });

            stopStopwatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FASTEST MODE","END FAST CAR CHALLENGE MODE");
                    command = "stopFast";
                    BTService.sendMessage(command);
                    outgoing = "twenty-seven: " + command + "\n";
                    messageActivity.chatHistory += outgoing;
                    if (isResume) {
                        tBuff += tMilliSec;
                        handler.removeCallbacks(runnable);
                        isResume = false;
                        stopStopwatch.setVisibility(View.GONE);
                        resetStopwatch.setVisibility(View.VISIBLE);
                    }
                }
            });

            resetStopwatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FASTEST MODE","STOPWATCH RESET");
                    if (!isResume) {
                        tMilliSec = tBuff = tStart = tUpdate = 0L;
                        milliSec = sec = min = 0;
                        stopwatchText.setText(R.string.stopwatch_default);
                        resetStopwatch.setVisibility(View.GONE);
                        startStopwatch.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            Toast.makeText(context, "App is not connected to robot.", Toast.LENGTH_SHORT).show();
        }
    }

    public Runnable runnable = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + tMilliSec;
            sec = (int) (tUpdate / 1000);
            min = sec / 60;
            sec = sec % 60;
            milliSec = (int) (tUpdate % 100);
            stopwatchText.setText(String.format("%02d", min) + ":" + String.format("%02d", sec) + "." +String.format("%02d", milliSec));
            handler.postDelayed(this, 60);
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
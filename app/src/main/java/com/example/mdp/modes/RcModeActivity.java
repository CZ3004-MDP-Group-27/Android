package com.example.mdp.modes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdp.R;
import com.example.mdp.SplashActivity;
import com.example.mdp.bluetooth.BTService;
import com.example.mdp.bluetooth.messageActivity;

import java.io.IOException;

public class RcModeActivity extends AppCompatActivity {
    TextView statusText;
    Button forward_btn, reverse_btn, left_btn, right_btn;
    String command, outgoing;
    Context context;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rc_mode);
        context = this;

        forward_btn = findViewById(R.id.forward_btn);
        reverse_btn = findViewById(R.id.reverse_btn);
        left_btn = findViewById(R.id.left_btn);
        right_btn = findViewById(R.id.right_btn);
        statusText = findViewById(R.id.statusText);
        statusText.setText(SplashActivity.status);

        if (BTService.BluetoothConnectionStatus) {
            forward_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //MotionEvent.ACTION_DOWN is when you hold a button down
                        command = "STM-forward";
                        Log.d("RC MODE","GO FORWARD");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) { //MotionEvent.ACTION_UP is when you release a button
                        command = "STM-stop";
                        Log.d("RC MODE","STOP CAR");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    }
                    return false;
                }
            });

            reverse_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //MotionEvent.ACTION_DOWN is when you hold a button down
                        command = "STM-reverse";
                        Log.d("RC MODE","GO BACKWARD");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) { //MotionEvent.ACTION_UP is when you release a button
                        command = "STM-stop";
                        Log.d("RC MODE","STOP CAR");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    }
                    return false;
                }
            });

            left_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //MotionEvent.ACTION_DOWN is when you hold a button down
                        command = "STM-left";
                        Log.d("RC MODE","TURN WHEEL LEFT");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) { //MotionEvent.ACTION_UP is when you release a button
                        command = "STM-straight";
                        Log.d("RC MODE","TURN WHEEL STRAIGHT");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    }
                    return false;
                }
            });

            right_btn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) { //MotionEvent.ACTION_DOWN is when you hold a button down
                        command = "STM-right";
                        Log.d("RC MODE","TURN WHEEL RIGHT");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) { //MotionEvent.ACTION_UP is when you release a button
                        command = "STM-straight";
                        Log.d("RC MODE","TURN WHEEL STRAIGHT");
                        BTService.sendMessage(command);
                        outgoing = "twenty-seven: " + command + "\n";
                        messageActivity.chatHistory += outgoing;
                    }
                    return false;
                }
            });
        } else {
            Toast.makeText(context, "App is not connected to robot.", Toast.LENGTH_SHORT).show();
        }
    }

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
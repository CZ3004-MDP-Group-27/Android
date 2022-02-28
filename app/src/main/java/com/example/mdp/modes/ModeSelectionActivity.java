package com.example.mdp.modes;

import static com.example.mdp.R.menu.menu_chat_mode_selction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mdp.R;
import com.example.mdp.SplashActivity;
import com.example.mdp.bluetooth.BTService;
import com.example.mdp.bluetooth.messageActivity;
import com.example.mdp.bluetooth.scanningActivity;

public class ModeSelectionActivity extends AppCompatActivity{
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        statusText = findViewById(R.id.statusText);
        statusText.setText(SplashActivity.status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        statusText.setText(SplashActivity.status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_chat_mode_selction, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent switchActivityIntent;
        switch (item.getItemId()) {
            case R.id.btConnectionsBtn:
                switchActivityIntent = new Intent(this, scanningActivity.class);
                startActivity(switchActivityIntent);
                return true;
            case R.id.chatBtn:
                switchActivityIntent = new Intent(this, messageActivity.class);
                startActivity(switchActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRcModeBtn(View view) {
        Intent switchActivityIntent = new Intent(this, RcModeActivity.class);
        startActivity(switchActivityIntent);
    }

    public void onFastestModeBtn(View view) {
        Intent switchActivityIntent = new Intent(this, FastestModeActivty.class);
        startActivity(switchActivityIntent);
    }

    public void onMapModeBtn(View view) {
        Intent switchActivityIntent = new Intent(this, MapModeActivity.class);
        startActivity(switchActivityIntent);
    }
}
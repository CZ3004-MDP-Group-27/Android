package com.example.mdp.bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdp.R;
import com.example.mdp.SplashActivity;
import com.example.mdp.modes.MapModeActivity;
import com.example.mdp.modes.ModeSelectionActivity;

import java.util.regex.PatternSyntaxException;

public class messageActivity extends AppCompatActivity {

    private TextView statusText,messageBoxText;
    private ScrollView messageBoxScroll;
    private EditText messageText;
    private ImageButton sendMessageBtn;
    private Context context;
    private static boolean flag = true;

    public static String chatHistory = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        context = this;

        statusText = findViewById(R.id.statusText);
        statusText.setText(SplashActivity.status);
        messageBoxText = findViewById(R.id.messageBoxText);
        messageBoxScroll = findViewById(R.id.messageBoxScroll);
        messageText = findViewById(R.id.messageText);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);

        if (!chatHistory.trim().equals("")) {
            messageBoxText.setText(chatHistory);
        }

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageText.getText().toString();

                if (!message.trim().equals("")) {
                    if (BTService.BluetoothConnectionStatus == true) {
                        String outgoing = "twenty-seven: " + message + "\n";
                        messageBoxText.append(outgoing);
                        chatHistory += outgoing;
                        messageBoxScroll.fullScroll(View.FOCUS_DOWN);
                        BTService.sendMessage("pc-" + message);
                        messageText.setText("");
                    } else {
                        Toast.makeText(context, "No connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (flag) {
            LocalBroadcastManager.getInstance(context).registerReceiver(messageReceiver, new IntentFilter("incomingMessage"));
            flag = false;
        }
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent switchActivityIntent;
        switch (item.getItemId()) {
            case android.R.id.home:
                switchActivityIntent = new Intent(this, ModeSelectionActivity.class);
                startActivity(switchActivityIntent);
//                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    Handler handler = new Handler();
    BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("receivedMessage");
            String received = "RPI: " + message + "\n";

            messageBoxText.append(received);
            chatHistory += received;
            messageBoxScroll.fullScroll(View.FOCUS_DOWN);
            Log.d("Message", "onReceive: " + message);
            String[] messageArray = new String[2];
            try {
                messageArray = message.split(":",2);
            } catch (PatternSyntaxException e) {
                messageArray[0] = message;
            }
            switch(messageArray[0]) {
                case "TARGET":
                    MapModeActivity.updateTarget(messageArray[1]);
                    break;
                case "ROBOT":
                    MapModeActivity.moveRobot(messageArray[1]);
                    break;
                default:
                    SplashActivity.status = "Status: " + message;
                    statusText.setText(SplashActivity.status);
            }
        }
    };

}
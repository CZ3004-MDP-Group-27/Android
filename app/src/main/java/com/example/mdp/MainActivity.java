// No longer in use

//package com.example.mdp;
//
//import android.Manifest;
//import android.bluetooth.BluetoothAdapter;
//import android.content.Context;
//import android.content.Intent;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//
//import com.example.mdp.bluetooth.scanningActivity;
//import com.example.mdp.modes.ModeSelectionActivity;
//
//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
//    private static final int REQUEST_ID = 101;
//    private int androidVersion; //define at top of code as a variable
//    private static final int REQUEST_ENABLE_BT = 0;
//    private BluetoothAdapter btAdapter;
//    private LocationManager locationManager;
//
//    @Override
//    public void onClick(View view) {
//        initBt();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.abs_layout);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        requestPermissions();
//        ImageButton connectBluetooth = findViewById(R.id.connectBluetooth);
//
//        connectBluetooth.setOnClickListener(this);
//
//
//    }
//
//    private void requestPermissions() {
//        androidVersion = Build.VERSION.SDK_INT;
//        if (androidVersion >= 23){
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.ACCESS_COARSE_LOCATION,
//                    }, REQUEST_ID);
//        }
//    };
//
//    private void initBt() {
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//
//
//        if (btAdapter == null) {
//            // Device doesn't support Bluetooth
//            Toast.makeText(this, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
//        }
//        if (!btAdapter.isEnabled()) {
//            Toast.makeText(this, "Enabling Bluetooth", Toast.LENGTH_SHORT).show();
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        } if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "Please enable Location to scan for devices.", Toast.LENGTH_SHORT).show();
//        }
//
//
//
//        Intent switchActivityIntent = new Intent(this, scanningActivity.class);
//        startActivity(switchActivityIntent);
//    }
//
//    public void onSkipBtn(View view) {
//        Intent switchActivityIntent = new Intent(this, ModeSelectionActivity.class);
//        startActivity(switchActivityIntent);
//    }
//}
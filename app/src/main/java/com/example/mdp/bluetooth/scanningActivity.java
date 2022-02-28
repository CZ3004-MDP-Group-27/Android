package com.example.mdp.bluetooth;

import static com.example.mdp.R.menu.menu_bluetooth_scan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdp.R;
import com.example.mdp.SplashActivity;
import com.example.mdp.modes.ModeSelectionActivity;
import com.example.mdp.modes.RcModeActivity;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class scanningActivity extends AppCompatActivity {
    private final int LOCATION_PERMISSION_REQUEST = 101;
    private ListView lvPairedDevices, lvAvailableDevices;
    private ArrayList<BluetoothDevice> pairedDevicesList= new ArrayList<>();
    private ArrayList<BluetoothDevice> availableDevicesList = new ArrayList<>();
    private ArrayList<String> uniqueAvailableDevices;
    private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices;
    private Context context;
    private BluetoothAdapter btAdapter;
    private ProgressBar scanningCircle;
    private TextView scanningText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean retryConnection = false;

    boolean retryFlag = false;
    Handler handler = new Handler();

    BTService mBluetoothConnection;
    public static BluetoothDevice mBTDevice;

    private static final UUID APP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        context = this;

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_bluetooth_scan, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scanBtn:
                scanDevices();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init() {
        lvPairedDevices = findViewById(R.id.pairedDevicesList);
        lvAvailableDevices = findViewById(R.id.availableDevicesList);
        scanningCircle = findViewById(R.id.scanningCircle);
        scanningText = findViewById(R.id.scanningText);
        adapterPairedDevices = new ArrayAdapter<String>(context, R.layout.device_list_item);
        adapterAvailableDevices = new ArrayAdapter<String>(context, R.layout.device_list_item);
        uniqueAvailableDevices = new ArrayList<>();
        lvPairedDevices.setAdapter(adapterPairedDevices);
        lvAvailableDevices.setAdapter(adapterAvailableDevices);

        lvPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btAdapter.cancelDiscovery();
                BTService.mBTDevice = pairedDevicesList.get(i);
                mBluetoothConnection = new BTService(scanningActivity.this);
                mBTDevice = pairedDevicesList.get(i);
                connect();
                boolean connected = false;
                while (!connected) {
                    try {
//                        BTService.sendMessage("Connection Established");
                        SplashActivity.status = "Status: Connected";
                        messageActivity.chatHistory += "Connection Established\n";
                        connected = true;
                    } catch (NullPointerException e) {
                        Log.d("BTService", "Connecting");
                    }
                }
                Toast.makeText(scanningActivity.this, "Device connected.", Toast.LENGTH_SHORT).show();
                Intent switchActivityIntent = new Intent(scanningActivity.this, messageActivity.class);
                startActivity(switchActivityIntent);
            }
        });

        lvAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btAdapter.cancelDiscovery();
                scanningCircle.setVisibility(View.GONE);
                scanningText.setVisibility(View.GONE);
                BluetoothDevice deviceToConnect = availableDevicesList.get(i);
                deviceToConnect.createBond();
                mBluetoothConnection = new BTService(scanningActivity.this);
                boolean paired = false;
                while (!paired) {
                    if (deviceToConnect.getBondState() == BluetoothDevice.BOND_BONDED) {
                        paired = true;
                    }
                }
                mBTDevice = deviceToConnect;
                adapterAvailableDevices.remove(deviceToConnect.getName() + "\n" + deviceToConnect.getAddress());
                adapterPairedDevices.add(deviceToConnect.getName() + "\n" + deviceToConnect.getAddress());
                Toast.makeText(scanningActivity.this, "Device paired. Please connect to device.", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
            }
        });

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                adapterPairedDevices.add(deviceName + "\n" + deviceHardwareAddress);
                Log.d("PAIRED", deviceName + "\n" + deviceHardwareAddress);
                pairedDevicesList.add(device);
            }
        } else {
            adapterPairedDevices.add("No paired devices.");
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(btDeviceListener, filter1);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(btDeviceListener, filter2);

        //Connection Status
        IntentFilter filter3 = new IntentFilter("ConnectionStatus");
        LocalBroadcastManager.getInstance(this).registerReceiver(statusReceiver, filter3);
    }

    public void connect(){
        mBluetoothConnection.startClientThread(mBTDevice,APP_UUID);
    }

    private final BroadcastReceiver btDeviceListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                if (deviceName != null) {
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    String deviceDetail = deviceName + "\n" + deviceHardwareAddress;
                    Log.d("FOUND", deviceDetail);
                    if (!uniqueAvailableDevices.contains(deviceDetail)) {
                        uniqueAvailableDevices.add(deviceDetail);
                        adapterAvailableDevices.add(deviceDetail);
                        availableDevicesList.add(device);
                    }

                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                scanningCircle.setVisibility(View.GONE);
                scanningText.setVisibility(View.GONE);
                if (adapterAvailableDevices.getCount() == 0) {
                    adapterAvailableDevices.add("No devices found.");
                } else {
                    Toast.makeText(context, "Scanning Complete.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void scanDevices() {
        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoveryIntent);
        }


        Toast.makeText(context, "Start Scan", Toast.LENGTH_SHORT).show();
        Log.d("StartSCAN", "scanDevices: start");
        scanningCircle.setVisibility(View.VISIBLE);
        scanningText.setVisibility(View.VISIBLE);
        uniqueAvailableDevices.clear();
        adapterAvailableDevices.clear();

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }

        btAdapter.startDiscovery();
        Log.d("StartSCAN", "scanDevices: Discovery");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        try {
            unregisterReceiver(btDeviceListener);
            unregisterReceiver(statusReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();

                // Permission granted.
            } else {
                new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setMessage("Location permission is required")
                        .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                checkPermissions();
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                scanningActivity.this.finish();
                            }
                        })
                        .create();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // handles connection status changes
    private BroadcastReceiver statusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice mDevice = intent.getParcelableExtra("Device");
            String status = intent.getStringExtra("Status");
            sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();

            if(status.equals("connected")){
                Toast.makeText(scanningActivity.this, "Device now connected to "+mDevice.getName(), Toast.LENGTH_SHORT).show();
                editor.putString("connStatus", "Connected to " + mDevice.getName());
//                statusTextView.setText("Connected to " + mDevice.getName());
            }
            else if(status.equals("disconnected") && retryConnection == false){
                Toast.makeText(scanningActivity.this, "Disconnected from "+mDevice.getName(), Toast.LENGTH_SHORT).show();
                mBluetoothConnection = new BTService(scanningActivity.this);

                sharedPreferences = getApplicationContext().getSharedPreferences("Shared Preferences", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("connStatus", "Disconnected");

//                statusTextView.setText("Disconnected");
                editor.commit();

                retryConnection = true;

                //try reconnect
                handler.postDelayed(reconnect, 7000);
            }
            if(status.equals("disconnected"))
//                statusTextView.setText("Disconnected");
            editor.commit();
        }
    };

    Runnable reconnect = new Runnable() {
        @Override
        public void run() {
            try {

                if (!BTService.BluetoothConnectionStatus) {
                    connect();
                    Toast.makeText(scanningActivity.this, "Reconnecting", Toast.LENGTH_SHORT).show();
                }
                handler.removeCallbacks(reconnect);
                retryFlag = false;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(scanningActivity.this, "Unable to reconnect.", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
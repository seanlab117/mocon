package com.example.esp32campicturereceiver;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.Manifest;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;

import kotlinx.coroutines.android.HandlerDispatcher;

public class MainActivity extends AppCompatActivity {
    String TAG = "bluetooth setup";
    int REQUEST_ENABLE_BT=1;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice ESP32CAMdevice;
    TextView tv;
    ImageView iv;
    BluetoothSocket socket = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        tv = findViewById(R.id.textView1);
        iv = findViewById(R.id.imageView);
        Log.d(TAG, "checking permissions scan and connect...");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) ==PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) ==PackageManager.PERMISSION_GRANTED)
        {
            // You can use the API that requires the permission.
            Log.d(TAG, "    permissions OK");
            checkBluetoothActivation();
        }
        else{
            Log.d(TAG, "    permissions NOK, asking for them...");
            requestPermissionLauncher.launch(new String[]{Manifest.permission.BLUETOOTH_SCAN,Manifest.permission.BLUETOOTH_CONNECT});
            // after this goes to onRequestPermissionsResult
        }
        // Register for broadcasts when a device is discovered.

    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.containsValue(false)) {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                } else {

                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    checkBluetoothActivation();

                }
            });

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (
                grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "    permissions granted");
            checkBluetoothActivation();
        }
    }

    @SuppressLint("MissingPermission")
    void checkBluetoothActivation(){
        Log.d(TAG,"checking if bluetooth is available...");
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(TAG, "    it is not. app will stop there");
        }
        else{
            Log.d(TAG, "    it is .");
            Log.d(TAG, "Checking if bluetooth is enabled...");
            if (!bluetoothAdapter.isEnabled()) {
                Log.d(TAG, "    it is not, requesting its activation.. .");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT); //after this goes to onActivityResult
            }
            else{
                Log.d(TAG, "    it is .");
                scanForBluetoothDevices();
            }
        }
    }

    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        scanForBluetoothDevices();
    }

    @SuppressLint("MissingPermission")
    void scanForBluetoothDevices() {
        Log.d(TAG, "Begining to scan for bluetooth devices...");
        tv.setText("begining to scan");
        bluetoothAdapter.startDiscovery();

    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Found device ! trying to connect...");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                if (deviceName != null && socket==null) {
                    if (deviceName.contains("ESP32")){
                        ESP32CAMdevice = device;
                        tv.append("\n"+deviceName+" : found ! trying to connect...");
                        bluetoothAdapter.cancelDiscovery();
                        try {
                            socket = ESP32CAMdevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                            socket.connect();
                            Handler handler = new Handler(){
                                public void handleMessage(Message msg){



                                    if(msg.arg2==1){ // receiving an image
                                        Log.d(TAG,"handler has detected picture arriving");
                                        iv.setImageBitmap((Bitmap) msg.obj);
                                    }
                                    else{ // receiving text
                                        Log.d(TAG, "text msg received");

                                        tv.append((String)msg.obj);
                                    }
                                }
                            };
                            ConnectedThread connectedThread = new ConnectedThread(socket, handler);
                            connectedThread.start();
                            Log.d(TAG, "Connexion was sucessfull");
                            tv.append("\nconnexion was succesfull. messages will display below...");
                        } catch (IOException e) {
                            Log.d(TAG,"problem with creating RFCOMM socket");
                            throw new RuntimeException(e);
                        }}

                    else{
                        tv.append("\n"+deviceName);
                    }
                }
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
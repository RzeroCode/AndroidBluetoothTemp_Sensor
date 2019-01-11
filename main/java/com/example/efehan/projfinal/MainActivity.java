package com.example.efehan.projfinal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.os.SystemClock.sleep;

public class MainActivity extends Activity {
    private final String DEVICE_NAME = "HC06";
    private final String DEVICE_ADDRESS="00:21:13:03:F4:64";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private final String TAG1="debug_thread";
    private final String TAG2="debug_socket";
    private final String TAG3="debug_inputstream";
    private final String TAG4="debug_close";
    private BluetoothDevice device;
    private BluetoothSocket socket;
    //private OutputStream outputStream;
    private InputStream inputStream;
    //Button startButton, sendButton, clearButton, stopButton;
    TextView textView;
    //EditText editText;
    boolean deviceConnected = false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    boolean dumpData=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.textView);
        textView.setEnabled(true);
        if (BTinit()) {
            if (BTconnect()) {
                deviceConnected = true;
                beginListenForData();
            }
        }
    }
    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device dosent Support Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG1, "message", e);
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            Log.e(TAG2, "message", e);
            connected = false;
        }
        if (connected) {

            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG3, "message", e);
            }
        }
        return connected;
    }
    void beginListenForData() {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopThread) {
                    try {
                        int byteCount = inputStream.available();
                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);


                            final String string = new String(rawBytes, "UTF-8");
                            handler.post(new Runnable() {
                                public void run() {
                                    if(!dumpData)
                                    {
                                        textView.setText(string+"°C");
                                    }
                                    else
                                    {
                                        dumpData=false;
                                    }
                                }
                            });

                        }
                    } catch (IOException ex) {
                        stopThread = true;
                    }

                    sleep(1000);
                }
            }
        });
        thread.start();
    }

   @Override
   protected void onDestroy()
   {
       super.onDestroy();
       stopThread = true;

       try
       {
           inputStream.close();
       }
       catch (IOException e)
       {
           Log.e(TAG4, "couldnt close inputstream", e);
       }
       try
       {
           socket.close();
       }
       catch (IOException e)
       {
           Log.e(TAG4, "couldnt close socket", e);
       }
       deviceConnected = false;
   }
}


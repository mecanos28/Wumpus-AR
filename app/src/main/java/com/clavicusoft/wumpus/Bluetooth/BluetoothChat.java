package com.clavicusoft.wumpus.Bluetooth;

import android.app.Activity;
import android.app.ActivityOptions;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.clavicusoft.wumpus.Database.AdminSQLite;
import com.clavicusoft.wumpus.Select.MainActivity;
import com.clavicusoft.wumpus.Select.Multiplayer;
import com.clavicusoft.wumpus.R;

public class BluetoothChat extends Activity {

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "";
    public static final String TOAST = "";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    public String laberinto = "";
    public String nombreLaberinto = "";
    public String funcion = "";
    private Button mSendButton;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;
    public int counter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        funcion = getIntent().getStringExtra("funcion").toString();

        if(funcion.equals("enviar")){
            setContentView(R.layout.send_labs);
            laberinto = getIntent().getStringExtra("laberinto");
            nombreLaberinto = getIntent().getStringExtra("nombreLaberinto");
        }else{
            setContentView(R.layout.searching_labs);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *Override onStart
     */
    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null){
                setupChat();
            }
        }
    }

    /**
     *Override onResume
     */
    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    /**
     *Recognize if the button send is pressed and send the lab.
     */
    private void setupChat() {
        if(funcion.equals("enviar")){
            mSendButton = (Button) findViewById(R.id.button_send);
            mSendButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    String message = laberinto;
                    sendMessage(message);
                }
            });
        }

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     *Override onPause
     */
    @Override
    public synchronized void onPause() {
        super.onPause();
    }

    /**
     *Override onStop
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     *Override onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null){
            mChatService.stop();
        }
    }

    /**
     * Put the bluetooth in discoverable mode
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Take the message and send it to mChatService.write
     * @param message
     */
    private void sendMessage(String message) {

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case MESSAGE_READ:

                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    final String [] splitMessage = tokenizer(readMessage);

                    AlertDialog.Builder alert = new AlertDialog.Builder(BluetoothChat.this);
                    alert.setTitle("Invitación para compartir laberinto");
                    alert.setMessage("¿Quiere aceptar el laberinto recibido?\nNombre: "+ splitMessage[2] + "\nRelaciones: " + splitMessage[0]+"\nNúmero de cuevas: "+ splitMessage[1]);
                    alert.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AdminSQLite admin = new AdminSQLite(BluetoothChat.this, "WumpusDB", null, 6);
                            SQLiteDatabase db = admin.getWritableDatabase();

                            ContentValues data = new ContentValues();
                            data.put("name", splitMessage[2]);
                            data.put("relations", splitMessage[0]);
                            data.put("number_of_caves", splitMessage[1]);
                            db.insert("GRAPH", null, data);

                            db.close();
                            AlertDialog.Builder newDialog = new AlertDialog.Builder(BluetoothChat.this);
                            newDialog.setTitle("Se ha guardado el laberinto");
                            newDialog.setMessage("¿Desea continuar intercambiando laberintos?");
                            newDialog.setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    Intent i = new Intent(BluetoothChat.this, Multiplayer.class);
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(BluetoothChat.this, R.anim.fade_in, R.anim.fade_out);
                                    startActivity(i, options.toBundle());
                                }
                            });
                            newDialog.setNegativeButton("No", new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                    Intent i = new Intent(BluetoothChat.this, MainActivity.class);
                                    ActivityOptions options = ActivityOptions.makeCustomAnimation(BluetoothChat.this, R.anim.slide_in_up, R.anim.slide_out_up);
                                    startActivity(i, options.toBundle());
                                }
                            });
                            newDialog.show();
                            dialog.dismiss();
                        }
                    });
                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Conectado a " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
        }
    }

    /**
     *
     * @param v
     */
    public void connect(View v) {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    /**
     *
     * @param v
     */
    public void discoverable(View v) {
        ensureDiscoverable();
    }

    /**
     *
     * @param msj
     * @return
     */
    public String[] tokenizer(String msj){
        String[] mensaje = msj.split("%");
        return mensaje;
    }

    /**
     * Sets the animation for the onBackPressed function.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
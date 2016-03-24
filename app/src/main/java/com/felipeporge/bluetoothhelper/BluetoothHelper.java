package com.felipeporge.bluetoothhelper;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import br.edu.ifpb.breath.R;

/**
 * This class represents the BluetoothHelper.
 *
 * @author Felipe Porge Xavier - felipefpx@gmail.com
 * --- www.felipeporge.com ---
 *
 * @category Connection Helper
 * Throws BluetoothConnectionErrorException, BluetoothDeviceNotFoundException, BluetoothNotEnabledException, 
 * BluetoothCloseConnectionException, BluetoothNotFoundException and BluetoothIsNotConnectedException.
 *
 *
 * 1- BluetoothConnectionErrorException;
 * An error occurs while establishing the Bluetooth connection.
 *
 * 2- BluetoothDeviceNotFoundException;
 * Bluetooth Device isn't found.
 *
 * 3- BluetoothNotEnabledException;
 * Bluetooth is disabled.
 *
 * 4- BluetoothCloseConnectionException;
 * An error occurs while closing the Bluetooth Connection.
 *
 * 5- BluetoothNotFoundException;
 * Device doesn't have Bluetooth Adapter.
 *
 * 6- BluetoothIsNotConnectedException;
 * Bluetooth isn't connected but the user tried to send or disconnect.
 */
public class BluetoothHelper{

    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 999;
    public static final int CONNECTION_ERROR = 998;
    public static final int DEVICE_NOT_FOUND_ERROR = 997;
    public static final int RECONNECT_AFTER_PAIR_ERROR = 996;
    private static final String DEFAULT_UUID = "00001101-0000-1000-8000-00805f9b34fb";

    private boolean mIsConnected = false;

    private BluetoothAdapter mBtAdapter;
	private BluetoothDevice mBtDevice;
	private UUID mUUID;
    private BluetoothSocket mSocket;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private Thread mWorkerThread;
    private volatile boolean nStopWorker = true;
    private String mReceivedData = "";
    private boolean mDataAvailable = false;
    private ArrayList<BluetoothDevice> mPairedDevices;
    private DeviceArrayAdapter mDevicesArrayAdapter;

    private Activity mContext;
    private OnConnectionListener mConnectionListener;

    private Dialog mDialog;
    private ProgressBar mDialogProgress;
    private Button mScanButton;

    /**
     * This method allows to create a new instance of the BluetoothHelper.
     * @param activity - Current context.
     * @param connectionListener - Interface that calls the connection notification methods.
     * @param connectionUUID - Custom UUID
     * @throws BluetoothNotFoundException
     */
	public BluetoothHelper(String connectionUUID, Activity activity, OnConnectionListener connectionListener) throws BluetoothNotFoundException{
		mUUID = UUID.fromString(connectionUUID);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = activity;
        mConnectionListener = connectionListener;

        if(mBtAdapter == null){
			throw new BluetoothNotFoundException();
		}
	}

    /**
     * This method allows to create a new instance of the BluetoothHelper.
     * @param activity - Current context.
     * @param connectionListener - Interface that calls the connection notification methods.
     * @throws BluetoothNotFoundException
     */
    public BluetoothHelper(Activity activity, OnConnectionListener connectionListener) throws BluetoothNotFoundException{
        mUUID = UUID.fromString(DEFAULT_UUID);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mContext = activity;
        mConnectionListener = connectionListener;

        if(mBtAdapter == null){
            throw new BluetoothNotFoundException();
        }
    }

    /**
     * This method allows to verify if the user's Bluetooth device is connected.
     * @return - Boolean symbolizing if some data is available.
     */
	public boolean isConnected(){
		return mIsConnected;
	}

    /**
     * This method allows to verify if exists available data to read.
     * @return - Boolean symbolizing if some data is available.
     */
	public boolean isAvailable(){
        if(mReceivedData == null)
            mDataAvailable = false;

		return mDataAvailable;
	}

    /**
     * This method allows to read Bluetooth data.
     * @return - It is the received data.
     */
	public String read(){
        String result = mReceivedData;
        mDataAvailable = false;
        mReceivedData = "";

        Log.w("BluetoothHelper", "Bluetooth data was read. Available:" + mDataAvailable);
        return result;
	}

    /**
     * This method allows to get the user's Bluetooth adapter.
     * @return - User's Bluetooth adapter.
     */
	public BluetoothAdapter getBluetoothAdapter(){
		return mBtAdapter;
	}

    /**
     * This method allows to get the current Bluetooth device.
     * @return - Bluetooth device in which the connection was made.
     */
	public BluetoothDevice getBluetoothDevice(){
		return mBtDevice;
	}

    /**
     * This method allows to get the Connection UUID.
     * @return - Connection UUID.
     */
	public UUID getConnectionUUID(){
		return mUUID;
	}

    /**
     * This method allows to verify if the Bluetooth adapter is enabled.
     * @return - It is the status of the Bluetooth device.
     */
	public boolean isEnabled(){
		return mBtAdapter.isEnabled();
	}

    /**
     * This method can be used to finalize the Bluetooth connection.
     * @throws BluetoothCloseConnectionException
     * @throws BluetoothIsNotConnectedException
     */
	public void disconnect() throws BluetoothCloseConnectionException, BluetoothIsNotConnectedException{
		if (!mIsConnected){
			throw new BluetoothIsNotConnectedException();
		}else{
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        close();
                    } catch (BluetoothCloseConnectionException e) {
                        e.printStackTrace();
                    }
                    mConnectionListener.onSuccessfullyDisconnected();
                }
            }, 200);
		}
	}

    /**
     * This method make the Bluetooth connection with other Bluetooth device.
     * @param deviceToConnect - Device to connect.
     */
	private void connectBT(BluetoothDevice deviceToConnect){
        if(mDialog.isShowing())
            mDialog.dismiss();

        new AsyncTask<BluetoothDevice, Void, Integer>(){

            private Dialog pDialog;

            @Override
            protected void onPreExecute(){
                pDialog = new Dialog(mContext);
                pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                pDialog.setContentView(R.layout.connect_bt_connecting_dialog);
                pDialog.setCancelable(false);
                pDialog.show();
            }

            @Override
            protected Integer doInBackground(BluetoothDevice... deviceToConnect) {

                Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
                if(pairedDevices.size() > 0){
                    for(BluetoothDevice device : pairedDevices){
                        if(device.getName().toString().toUpperCase().equals(deviceToConnect[0].getName().toUpperCase())) {
                            mBtDevice = device;
                            break;
                        }
                    }

                    if (mBtAdapter.isDiscovering()) {
                        mBtAdapter.cancelDiscovery();
                    }
                }

                if(mBtDevice != null){
                    try {
                        mSocket = mBtDevice.createRfcommSocketToServiceRecord(mUUID);
                        mSocket.connect();
                        mOutputStream = mSocket.getOutputStream();
                        mInputStream = mSocket.getInputStream();
                        beginListenForData();
                        mIsConnected = true;
                        return 0;
                    } catch (IOException e) {
                        return 1;
                    }
                }else{
                    return 2;
                }
            }

            @Override
            public void onPostExecute(final Integer result){
                pDialog.dismiss();

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch(result){
                            case 0:
                                Log.w("connectBT()", "Connected!");
                                mConnectionListener.onSuccessfullyConnected(mBtDevice);
                                break;
                            case 1:
                                Log.w("connectBT()", "Connection error.");
                                mConnectionListener.onErrorOccurred(CONNECTION_ERROR);
                                mDialog.show();
                                break;
                            case 2:
                                Log.w("connectBT()", "Device not found.");
                                mConnectionListener.onErrorOccurred(DEVICE_NOT_FOUND_ERROR);
                                break;
                        }
                    }
                });
            }
        }.execute(deviceToConnect);
	}

    /**
     * This method allows to send some data via Bluetooth.
     * @param data - Data to send.
     * @throws BluetoothIsNotConnectedException
     */
	public void send(String data) throws BluetoothIsNotConnectedException{
		if (!mIsConnected){
			throw new BluetoothIsNotConnectedException();
		}
        try {
            mOutputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    /**
     * This method closes the Bluetooth connection.
     * @throws BluetoothCloseConnectionException
     */
	private void close() throws BluetoothCloseConnectionException{
		try{
			nStopWorker = true;
		    mOutputStream.close();
			mInputStream.close();
			mSocket.close();
		}catch(IOException e){
			throw new BluetoothCloseConnectionException();
		}
		mIsConnected = false;
		Log.d("Bluetooth", "Bluetooth Connection Closed.");
	}

    /**
     * This method starts the data listening.
     */
    private void beginListenForData(){
        nStopWorker = false;
        mWorkerThread = new Thread(new Runnable(){
            public void run() {
                if((!nStopWorker) && isConnected()){
                    byte[] buffer = new byte[1024];  // buffer store for the stream
                    int bytes = 0; // bytes returned from read()

                    // Keep listening to the InputStream until an exception occurs
                    while (true) {
                        if(mDataAvailable)
                            continue;

                        try {
                            // Read from the InputStream
                            bytes = mInputStream.read(buffer);        // Get number of bytes and message in "buffer"

                            if(bytes > 0){
                                String tmp = new String(buffer, 0, bytes, "ISO-8859-1");
                                if(tmp != null){
                                    mReceivedData += tmp;
                                    if(mReceivedData.contains("\n")){
                                        mReceivedData.replace("\n", "");
                                        mDataAvailable = true;
                                        Log.w("BluetoothHelper", "Received: " + mReceivedData + " Available:" + mDataAvailable);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            mDataAvailable = false;
                            break;
                        }
                    }
                }
            }
        });

        mWorkerThread.start();
    }

    /**
     * This method allows to pair with a new Bluetooth device.
     * @param device - Device to pair
     */
    private void pairDevice(BluetoothDevice device) {
        try {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e) {
            Log.e("pairDevice()", e.getMessage());
        }finally {
            mConnectionListener.onErrorOccurred(RECONNECT_AFTER_PAIR_ERROR);
            mDialog.show();
        }

    }

    /**
     * This method allows to unpair a Bluetooth device.
     * @param device - Device to unpair.
     */
    private void unpairDevice(BluetoothDevice device) {
        try {
            Log.d("unpairDevice()", "Start Un-Pairing...");
            Method m = device.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("unpairDevice()", "Un-Pairing finished.");
        } catch (Exception e) {
            Log.e("unpairDevice()", e.getMessage());
        }
    }

    /**
     * This method shows the ConnectDialog, where the user can make a new Bluetooth connection.
     */
    public void connect(){
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.connect_bt_dialog);
        mDialogProgress = (ProgressBar) mDialog.findViewById(R.id.connect_bt_progress);

        // Initialize the button to perform device discovery
        mScanButton = (Button) mDialog.findViewById(R.id.connect_bt_dialog_scan);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevices = new ArrayList<BluetoothDevice>();
        mDevicesArrayAdapter = new DeviceArrayAdapter(mContext, R.layout.connect_bt_device_list_item);

        // Find and set up the ListView for newly discovered devices
        ListView devicesListView = (ListView) mDialog.findViewById(R.id.connect_bt_dialog_devices);
        devicesListView.setAdapter(mDevicesArrayAdapter);
        devicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, addDevice each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevices.add(device);
            }
        }

        mDialog.show();
        doDiscovery();
    }

    /**
     * This method finalize the Bluetooth.
     */
    public void onDestroyBluetooth(){
        if((mBtAdapter != null) && (mBtAdapter.isDiscovering())){
            mBtAdapter.cancelDiscovery();
            mContext.unregisterReceiver(mReceiver);
        }
    }


    /**
     * Start device discover with the BluetoothAdapter.
     */
    private void doDiscovery() {
        Log.d(getClass().getSimpleName(), "doDiscovery()");

        // Indicate scanning in the title
        mDialogProgress.setVisibility(View.VISIBLE);
        mScanButton.setVisibility(View.GONE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
        if(mDevicesArrayAdapter.getCount() > 0)
            mDevicesArrayAdapter.clean();
        mDevicesArrayAdapter.addAllDevices(mPairedDevices);
    }

    /**
     * The on-click listener in the ListViews.
     */
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int i, long l) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();
            mScanButton.setVisibility(View.VISIBLE);
            mDialogProgress.setVisibility(View.GONE);

            if(!((TextView) v).getText().toString()
                    .equals(mContext.getResources().getString(R.string.connect_bt_device_not_found))) {

                if (mPairedDevices.indexOf(mDevicesArrayAdapter.getDeviceItem(i)) == -1)
                    pairDevice(mDevicesArrayAdapter.getDeviceItem(i));
                else
                    connectBT(mDevicesArrayAdapter.getDeviceItem(i));
            }
        }
    };

    /**
     * The BroadcastReceiver search for devices and
     * changes the title when discovery is finished.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mDevicesArrayAdapter.addDevice(device);
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mDialogProgress.setVisibility(View.INVISIBLE);
                mScanButton.setVisibility(View.VISIBLE);

                if (mDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = mContext.getResources().getString(R.string.connect_bt_device_not_found);
                    mDevicesArrayAdapter.add(Html.fromHtml(noDevices));
                }
            }
        }
    };

    /**
     * This class represents the adapter of available devices list.
     */
    private static class DeviceArrayAdapter extends ArrayAdapter<Spanned> {

        private ArrayList<BluetoothDevice> devicesList = new ArrayList<BluetoothDevice>();

        public DeviceArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        public BluetoothDevice getDeviceItem(int pos){
            return devicesList.get(pos);
        }
        public void addDevice(BluetoothDevice device){
            devicesList.add(device);
            add(Html.fromHtml("<b>" + device.getName() + "</b><br/>" + device.getAddress()));
        }

        public void addAllDevices(ArrayList<BluetoothDevice> devices) {
            for(int i = 0; i < devices.size(); i++)
                addDevice(devices.get(i));
        }

        public void clean(){
            devicesList.clear();
            clear();
        }
    }
}
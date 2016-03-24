package com.felipeporge.bluetoothhelper;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Felipe on 10/09/2014.
 */
public interface OnConnectionListener {

    public void onSuccessfullyConnected(BluetoothDevice device);

    public void onSuccessfullyDisconnected();

    public void onErrorOccurred(int errorCode);
}

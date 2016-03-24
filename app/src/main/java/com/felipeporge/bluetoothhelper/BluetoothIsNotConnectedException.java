package com.felipeporge.bluetoothhelper;

/**
 * This class represents a Bluetooth isn't connected exception.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class BluetoothIsNotConnectedException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Constructor method.
     */
    public BluetoothIsNotConnectedException() {}

    /**
     * Constructor method.
     * @param message - Exception message.
     */
    public BluetoothIsNotConnectedException(String message){
       super(message);
    }
}
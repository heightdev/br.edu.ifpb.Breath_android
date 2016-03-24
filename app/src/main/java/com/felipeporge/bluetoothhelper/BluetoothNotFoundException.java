package com.felipeporge.bluetoothhelper;

/**
 * This class represents a Bluetooth Not Found exception.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class BluetoothNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Constructor Method.
     */
    public BluetoothNotFoundException() {}

    /**
     * Constructor method.
     * @param message - Exception message.
     */
    public BluetoothNotFoundException(String message){
       super(message);
    }
}

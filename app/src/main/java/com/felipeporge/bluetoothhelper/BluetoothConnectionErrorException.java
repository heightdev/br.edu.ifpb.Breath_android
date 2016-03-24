package com.felipeporge.bluetoothhelper;

/**
 * This class represents a Bluetooth Connection Error Exception.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class BluetoothConnectionErrorException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Constructor Method.
     */
    public BluetoothConnectionErrorException() {}

    /**
     * Constructor Method.
     * @param message - Exception message.
     */
    public BluetoothConnectionErrorException(String message){
       super(message);
    }
}
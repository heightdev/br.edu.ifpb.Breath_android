package com.felipeporge.bluetoothhelper;

/**
 * This class represents a Bluetooth Close Connection Exception.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class BluetoothCloseConnectionException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Constructor Method.
     */
    public BluetoothCloseConnectionException() {}

    /**
     * Constructor method.
     * @param message - Exception message.
     */
    public BluetoothCloseConnectionException(String message){
       super(message);
    }
}
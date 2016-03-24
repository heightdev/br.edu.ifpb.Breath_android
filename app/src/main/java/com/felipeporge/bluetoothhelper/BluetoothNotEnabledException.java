package com.felipeporge.bluetoothhelper;

/**
 * This class represents a Bluetooth Not Enabled Exception.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class BluetoothNotEnabledException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Constructor method.
     */
    public BluetoothNotEnabledException() {}

    /**
     * Constructor method.
     * @param message - Exception message.
     */
    public BluetoothNotEnabledException(String message){
       super(message);
    }
}

package com.felipeporge.bluetoothhelper;

/**
 * This class represents a Bluetooth Device Not Found Exception.
 *
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class BluetoothDeviceNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

    /**
     * Constructor Method.
     */
    public BluetoothDeviceNotFoundException() {}

    /**
     * Constructor Method.
     * @param message - Exception message.
     */
    public BluetoothDeviceNotFoundException(String message){
       super(message);
    }
}
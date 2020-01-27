package com.nexenio.rxandroidbleserver.exception;

public class BluetoothNotAvailableException extends RxBleServerException {

    public BluetoothNotAvailableException() {
    }

    public BluetoothNotAvailableException(String message) {
        super(message);
    }

    public BluetoothNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BluetoothNotAvailableException(Throwable cause) {
        super(cause);
    }

}

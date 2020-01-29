package com.nexenio.rxandroidbleserver.exception;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

import androidx.annotation.NonNull;

public class ValueNotAvailableException extends RxBleServerException {

    public ValueNotAvailableException() {
    }

    public ValueNotAvailableException(@NonNull RxBleClient client) {
        this("No value available for client: " + client);
    }

    public ValueNotAvailableException(String message) {
        super(message);
    }

    public ValueNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueNotAvailableException(Throwable cause) {
        super(cause);
    }

}

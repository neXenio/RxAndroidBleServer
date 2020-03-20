package com.nexenio.rxandroidbleserver.exception;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

public class ClientNotConnectedException extends RxBleServerException {

    public ClientNotConnectedException() {
    }

    public ClientNotConnectedException(String message) {
        super(message);
    }

    public ClientNotConnectedException(RxBleClient client) {
        super("Client is not connected: " + client);
    }

    public ClientNotConnectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientNotConnectedException(Throwable cause) {
        super(cause);
    }

}

package com.nexenio.rxandroidbleserver.client;

public interface RxBleConnectionState {

    void update(int state);

    boolean isConnected();

    boolean isDisconnected();

}

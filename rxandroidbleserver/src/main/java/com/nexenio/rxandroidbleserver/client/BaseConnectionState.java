package com.nexenio.rxandroidbleserver.client;

import android.bluetooth.BluetoothGattServer;

public class BaseConnectionState implements RxBleConnectionState {

    private static final int UNKNOWN = -1;

    private int state;

    public BaseConnectionState() {
        this(UNKNOWN);
    }

    public BaseConnectionState(int state) {
        this.state = state;
    }

    @Override
    public void update(int state) {
        this.state = state;
    }

    @Override
    public boolean isConnected() {
        return state == BluetoothGattServer.STATE_CONNECTED;
    }

    @Override
    public boolean isDisconnected() {
        return state == BluetoothGattServer.STATE_CONNECTED;
    }

    @Override
    public String toString() {
        return getReadableState(state);
    }

    private static String getReadableState(int state) {
        switch (state) {
            case BluetoothGattServer.STATE_CONNECTING:
                return "Connecting";
            case BluetoothGattServer.STATE_CONNECTED:
                return "Connected";
            case BluetoothGattServer.STATE_DISCONNECTING:
                return "Disconnecting";
            case BluetoothGattServer.STATE_DISCONNECTED:
                return "Disconnected";
            default:
                return "Unknown";
        }
    }

}

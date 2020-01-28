package com.nexenio.rxandroidbleserver.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

public class BaseClient implements RxBleClient {

    private static final int CONNECTION_STATE_UNKNOWN = -1;

    protected final BluetoothDevice bluetoothDevice;

    protected int connectionState;

    public BaseClient(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.connectionState = CONNECTION_STATE_UNKNOWN;
    }

    @Override
    public boolean isConnected() {
        return connectionState == BluetoothGatt.STATE_CONNECTED;
    }

    @Override
    public boolean isDisconnected() {
        return connectionState == BluetoothGatt.STATE_CONNECTED;
    }

    @Override
    public void setConnectionState(int connectionState) {
        this.connectionState = connectionState;
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public String toString() {
        return "BaseClient{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", connectionState=" + getReadableConnectionState(connectionState) +
                '}';
    }

    public static String getReadableConnectionState(int state) {
        switch (state) {
            case BluetoothGatt.STATE_CONNECTING:
                return "Connecting";
            case BluetoothGatt.STATE_CONNECTED:
                return "Connected";
            case BluetoothGatt.STATE_DISCONNECTING:
                return "Disconnecting";
            case BluetoothGatt.STATE_DISCONNECTED:
                return "Disconnected";
            default:
                return "Unknown";
        }
    }

}

package com.nexenio.rxandroidbleserver.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import java.util.Objects;

import androidx.annotation.NonNull;

public class BaseClient implements RxBleClient {

    private static final int CONNECTION_STATE_UNKNOWN = -1;

    protected final BluetoothDevice bluetoothDevice;

    protected int connectionState;

    public BaseClient(@NonNull BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.connectionState = CONNECTION_STATE_UNKNOWN;
    }

    @Override
    public boolean isConnected() {
        return connectionState == BluetoothGatt.STATE_CONNECTED;
    }

    @Override
    public boolean isDisconnected() {
        return connectionState == BluetoothGatt.STATE_DISCONNECTED;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseClient client = (BaseClient) o;
        return bluetoothDevice.equals(client.bluetoothDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bluetoothDevice);
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

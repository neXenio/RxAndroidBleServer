package com.nexenio.rxandroidbleserver.client;

import android.bluetooth.BluetoothDevice;

public class BaseClient implements RxBleClient {

    protected final BluetoothDevice bluetoothDevice;

    protected final RxBleConnectionState connectionState;

    public BaseClient(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
        this.connectionState = new BaseConnectionState();
    }

    @Override
    public RxBleConnectionState getConnectionState() {
        return connectionState;
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public String toString() {
        return "BaseClient{" +
                "bluetoothDevice=" + bluetoothDevice +
                ", connectionState=" + connectionState +
                '}';
    }

}

package com.nexenio.rxandroidbleserver.client;

import android.bluetooth.BluetoothDevice;

public interface RxBleClient {

    RxBleConnectionState getConnectionState();

    BluetoothDevice getBluetoothDevice();
}

package com.nexenio.rxandroidbleserver.client;

import android.bluetooth.BluetoothDevice;

public interface RxBleClient {

    boolean isConnected();

    boolean isDisconnected();

    void setConnectionState(int connectionState);

    BluetoothDevice getBluetoothDevice();

}

package com.nexenio.rxandroidbleserver;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Single;

public interface RxBleServerMapper {

    Single<RxBleClient> getClient(@NonNull BluetoothDevice bluetoothDevice);

    Single<RxBleService> getService(@NonNull BluetoothGattService gattService);

    Single<RxBleCharacteristic> getCharacteristic(@NonNull BluetoothGattCharacteristic gattCharacteristic);

    Single<RxBleDescriptor> getDescriptor(@NonNull BluetoothGattDescriptor gattDescriptor);

}

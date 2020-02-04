package com.nexenio.rxandroidbleserver.service;

import android.bluetooth.BluetoothGattService;

import com.nexenio.rxandroidbleserver.RxBleServer;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

public interface RxBleService {

    UUID getUuid();

    Completable addCharacteristic(@NonNull RxBleCharacteristic characteristic);

    Set<RxBleCharacteristic> getCharacteristics();

    BluetoothGattService getGattService();

    RxBleServer getParentServer();

    void setParentServer(@NonNull RxBleServer parentServer);

}

package com.nexenio.rxandroidbleserver.service;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.nexenio.rxandroidbleserver.exception.RxBleServerException;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;

public class BaseService implements RxBleService {

    protected final Set<RxBleCharacteristic> characteristics;
    protected final BluetoothGattService gattService;

    public BaseService(@NonNull UUID uuid, int type) {
        this.characteristics = new HashSet<>();
        this.gattService = new BluetoothGattService(uuid, type);
    }

    public BaseService(@NonNull BluetoothGattService gattService) {
        this.characteristics = new HashSet<>();
        this.gattService = gattService;
        // TODO: 1/26/2020 add characteristics if available
    }

    @Override
    public UUID getUuid() {
        return gattService.getUuid();
    }

    @Override
    public Completable addCharacteristic(@NonNull RxBleCharacteristic characteristic) {
        return Completable.defer(() -> {
            BluetoothGattCharacteristic gattCharacteristic = characteristic.getGattCharacteristic();
            boolean success = gattService.addCharacteristic(gattCharacteristic);
            if (success) {
                return Completable.complete();
            } else {
                return Completable.error(new RxBleServerException("Unable to add GATT characteristic"));
            }
        }).doOnComplete(() -> characteristics.add(characteristic));
    }

    @Override
    public Set<RxBleCharacteristic> getCharacteristics() {
        return Collections.unmodifiableSet(characteristics);
    }

    @Override
    public BluetoothGattService getGattService() {
        return gattService;
    }

    @Override
    public String toString() {
        return "BaseService{" +
                "uuid=" + gattService.getUuid() +
                ", characteristics=" + characteristics +
                '}';
    }

}

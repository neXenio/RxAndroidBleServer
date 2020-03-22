package com.nexenio.rxandroidbleserver.service;

import android.bluetooth.BluetoothGattService;

import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Observable;

public class ServiceBuilder {

    private final UUID uuid;

    private int type = BluetoothGattService.SERVICE_TYPE_PRIMARY;

    private final Set<RxBleCharacteristic> characteristics;

    public ServiceBuilder(@NonNull UUID uuid) {
        this.uuid = uuid;
        characteristics = new HashSet<>();
    }

    public BaseService build() {
        BaseService service = new BaseService(uuid, type);

        Observable.fromIterable(characteristics)
                .flatMapCompletable(service::addCharacteristic)
                .blockingAwait();

        return service;
    }

    public ServiceBuilder withCharacteristic(@NonNull RxBleCharacteristic characteristics) {
        this.characteristics.add(characteristics);
        return this;
    }

    public ServiceBuilder isPrimaryService() {
        type = BluetoothGattService.SERVICE_TYPE_PRIMARY;
        return this;
    }

    public ServiceBuilder isSecondaryService() {
        type = BluetoothGattService.SERVICE_TYPE_SECONDARY;
        return this;
    }

}

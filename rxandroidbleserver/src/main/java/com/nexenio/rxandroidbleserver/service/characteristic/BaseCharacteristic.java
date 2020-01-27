package com.nexenio.rxandroidbleserver.service.characteristic;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.RxBleServerException;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.response.BaseServerResponse;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.response.ServerErrorResponse;
import com.nexenio.rxandroidbleserver.response.ServerWriteResponse;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class BaseCharacteristic implements RxBleCharacteristic {

    protected final Set<RxBleDescriptor> descriptors;
    protected final BluetoothGattCharacteristic gattCharacteristic;

    public BaseCharacteristic(@NonNull UUID uuid, int properties, int permissions) {
        this.descriptors = new HashSet<>();
        this.gattCharacteristic = new BluetoothGattCharacteristic(uuid, properties, permissions);
    }

    public BaseCharacteristic(@NonNull BluetoothGattCharacteristic gattCharacteristic) {
        this.descriptors = new HashSet<>();
        this.gattCharacteristic = gattCharacteristic;
        // TODO: 1/26/2020 add descriptors if available
    }

    @Override
    public UUID getUuid() {
        return gattCharacteristic.getUuid();
    }

    @Override
    public Completable addDescriptor(@NonNull RxBleDescriptor descriptor) {
        return Completable.defer(() -> {
            BluetoothGattDescriptor gattDescriptor = descriptor.getGattDescriptor();
            boolean success = gattCharacteristic.addDescriptor(gattDescriptor);
            if (success) {
                return Completable.complete();
            } else {
                return Completable.error(new RxBleServerException("Unable to add GATT descriptor"));
            }
        }).doOnComplete(() -> descriptors.add(descriptor));
    }

    @Override
    public Set<RxBleDescriptor> getDescriptors() {
        return Collections.unmodifiableSet(descriptors);
    }

    @Override
    public BluetoothGattCharacteristic getGattCharacteristic() {
        return gattCharacteristic;
    }

    @Override
    public Single<byte[]> getValue() {
        return Single.defer(() -> Single.just(gattCharacteristic.getValue()));
    }

    @Override
    public Completable setValue(@NonNull byte[] value) {
        return Completable.fromAction(() -> gattCharacteristic.setValue(value));
    }

    @Override
    public Single<byte[]> getValue(@NonNull RxBleClient client) {
        return getValue();
    }

    @Override
    public Completable setValue(@NonNull byte[] value, @NonNull RxBleClient client) {
        return setValue(value);
    }

    @Override
    public Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleCharacteristicReadRequest request) {
        return getValue(request.getClient())
                .map(value -> new BaseServerResponse(request, value))
                .cast(RxBleServerResponse.class)
                .onErrorReturnItem(new ServerErrorResponse(request));
    }

    @Override
    public Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleCharacteristicWriteRequest request) {
        Completable writeValue = Completable.defer(() -> {
            if (request.getOffset() == 0) {
                return setValue(request.getValue(), request.getClient());
            } else {
                // TODO: 1/26/2020 implement long writes support
                return Completable.error(new RxBleServerException("Long writes are not yet supported"));
            }
        });

        Maybe<RxBleServerResponse> createResponse = Maybe.just(new ServerWriteResponse(request))
                .cast(RxBleServerResponse.class);

        return writeValue.andThen(Maybe.defer(() -> {
            if (request.isResponseNeeded()) {
                return createResponse;
            } else {
                return Maybe.empty();
            }
        })).onErrorReturnItem(new ServerErrorResponse(request));
    }

}

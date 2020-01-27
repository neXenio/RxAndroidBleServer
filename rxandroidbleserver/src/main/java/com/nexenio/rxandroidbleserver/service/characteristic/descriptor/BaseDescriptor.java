package com.nexenio.rxandroidbleserver.service.characteristic.descriptor;

import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.RxBleServerException;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorReadRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.response.BaseServerResponse;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.response.ServerErrorResponse;
import com.nexenio.rxandroidbleserver.response.ServerWriteResponse;

import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class BaseDescriptor implements RxBleDescriptor {

    protected final BluetoothGattDescriptor gattDescriptor;

    public BaseDescriptor(@NonNull UUID uuid, int permissions) {
        this.gattDescriptor = new BluetoothGattDescriptor(uuid, permissions);
    }

    public BaseDescriptor(@NonNull BluetoothGattDescriptor gattDescriptor) {
        this.gattDescriptor = gattDescriptor;
    }

    @Override
    public UUID getUuid() {
        return gattDescriptor.getUuid();
    }

    @Override
    public BluetoothGattDescriptor getGattDescriptor() {
        return gattDescriptor;
    }

    @Override
    public Single<byte[]> getValue() {
        return Single.defer(() -> Single.just(gattDescriptor.getValue()));
    }

    @Override
    public Completable setValue(@NonNull byte[] value) {
        return Completable.fromAction(() -> gattDescriptor.setValue(value));
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
    public Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleDescriptorReadRequest request) {
        return getValue(request.getClient())
                .map(value -> new BaseServerResponse(request, value))
                .cast(RxBleServerResponse.class)
                .onErrorReturnItem(new ServerErrorResponse(request));
    }

    @Override
    public Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleDescriptorWriteRequest request) {
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

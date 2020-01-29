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
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public class BaseDescriptor implements RxBleDescriptor {

    protected RxBleValue value = new BaseValue();

    protected final BluetoothGattDescriptor gattDescriptor;

    public BaseDescriptor(@NonNull UUID uuid, int permissions) {
        this.gattDescriptor = new BluetoothGattDescriptor(uuid, permissions);
    }

    public BaseDescriptor(@NonNull BluetoothGattDescriptor gattDescriptor) {
        this.gattDescriptor = gattDescriptor;
        updateValueFromDescriptor().blockingAwait();
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
    public Single<RxBleValue> getValue() {
        return updateValueFromDescriptor()
                .andThen(Single.just(value));
    }

    @Override
    public Completable setValue(@NonNull RxBleValue value) {
        return Completable.fromCallable(() -> this.value = value)
                .andThen(updateDescriptorFromValue());
    }

    @Override
    public Single<RxBleValue> getValue(@NonNull RxBleClient client) {
        return getValue();
    }

    @Override
    public Completable setValue(@NonNull RxBleValue value, @NonNull RxBleClient client) {
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

    private Completable updateValueFromDescriptor() {
        return Completable.fromAction(() -> {
            if (gattDescriptor.getValue() != null) {
                value.setBytes(gattDescriptor.getValue());
            }
        });
    }

    private Completable updateDescriptorFromValue() {
        return Completable.fromAction(() -> gattDescriptor.setValue(value.getBytes()));
    }

    @Override
    public String toString() {
        return "BaseDescriptor{" +
                "value=" + value +
                '}';
    }

}

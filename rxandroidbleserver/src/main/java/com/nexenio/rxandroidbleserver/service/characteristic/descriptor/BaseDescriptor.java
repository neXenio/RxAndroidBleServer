package com.nexenio.rxandroidbleserver.service.characteristic.descriptor;

import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.request.RxBleReadRequest;
import com.nexenio.rxandroidbleserver.request.RxBleWriteRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorReadRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.BaseValueContainer;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class BaseDescriptor extends BaseValueContainer implements RxBleDescriptor {

    protected final BluetoothGattDescriptor gattDescriptor;

    public BaseDescriptor(@NonNull UUID uuid, int permissions) {
        this(new BluetoothGattDescriptor(uuid, permissions));
    }

    public BaseDescriptor(@NonNull BluetoothGattDescriptor gattDescriptor) {
        this.gattDescriptor = gattDescriptor;
        if (gattDescriptor.getValue() != null) {
            RxBleValue initialValue = new BaseValue(gattDescriptor.getValue());
            if (this.shareValues) {
                sharedValueProvider.setValue(initialValue).blockingAwait();
            } else {
                // TODO: 2020-01-29 initialize client values?
            }
        }
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
    public Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleDescriptorReadRequest request) {
        return createReadRequestResponse((RxBleReadRequest) request);
    }

    @Override
    public Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleDescriptorWriteRequest request) {
        return createWriteRequestResponse((RxBleWriteRequest) request);
    }

    @Override
    public String toString() {
        return "BaseDescriptor{" +
                "uuid=" + gattDescriptor.getUuid() +
                ", sharedValue=" + sharedValueProvider.getValue().blockingGet() +
                '}';
    }

}

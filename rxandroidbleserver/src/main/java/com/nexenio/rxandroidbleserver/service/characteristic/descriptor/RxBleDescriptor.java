package com.nexenio.rxandroidbleserver.service.characteristic.descriptor;

import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorReadRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.value.RxBleValueContainer;

import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface RxBleDescriptor extends RxBleValueContainer {

    UUID getUuid();

    BluetoothGattDescriptor getGattDescriptor();

    Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleDescriptorReadRequest request);

    Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleDescriptorWriteRequest request);

}

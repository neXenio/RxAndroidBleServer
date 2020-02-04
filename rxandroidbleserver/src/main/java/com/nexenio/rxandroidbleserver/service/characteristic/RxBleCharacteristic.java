package com.nexenio.rxandroidbleserver.service.characteristic;

import android.bluetooth.BluetoothGattCharacteristic;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.service.value.RxBleValueContainer;

import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface RxBleCharacteristic extends RxBleValueContainer {

    UUID getUuid();

    Completable addDescriptor(@NonNull RxBleDescriptor descriptor);

    Set<RxBleDescriptor> getDescriptors();

    BluetoothGattCharacteristic getGattCharacteristic();

    Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleCharacteristicReadRequest request);

    Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleCharacteristicWriteRequest request);

    Completable notify(@NonNull RxBleClient client);

    RxBleService getParentService();

    void setParentService(@NonNull RxBleService parentService);

}

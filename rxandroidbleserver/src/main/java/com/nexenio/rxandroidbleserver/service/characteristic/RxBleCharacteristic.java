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
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface RxBleCharacteristic extends RxBleValueContainer {

    UUID getUuid();

    Completable addDescriptor(@NonNull RxBleDescriptor descriptor);

    Set<RxBleDescriptor> getDescriptors();

    BluetoothGattCharacteristic getGattCharacteristic();

    Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleCharacteristicReadRequest request);

    Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleCharacteristicWriteRequest request);

    Completable sendNotifications();

    Completable sendNotification(@NonNull RxBleClient client);

    Completable sendIndication(@NonNull RxBleClient client);

    RxBleService getParentService();

    void setParentService(@NonNull RxBleService parentService);

    boolean hasProperty(int property);

    boolean hasPermission(int permission);

}

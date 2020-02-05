package com.nexenio.rxandroidbleserver.service.characteristic;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.RxBleServerException;
import com.nexenio.rxandroidbleserver.request.RxBleReadRequest;
import com.nexenio.rxandroidbleserver.request.RxBleWriteRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.ClientCharacteristicConfiguration;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.BaseValueContainer;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import timber.log.Timber;

public class BaseCharacteristic extends BaseValueContainer implements RxBleCharacteristic {

    protected RxBleService parentService;
    protected final Set<RxBleDescriptor> descriptors;
    protected final BluetoothGattCharacteristic gattCharacteristic;

    public BaseCharacteristic(@NonNull UUID uuid, int properties, int permissions) {
        this(new BluetoothGattCharacteristic(uuid, properties, permissions));
    }

    public BaseCharacteristic(@NonNull BluetoothGattCharacteristic gattCharacteristic) {
        this.descriptors = new HashSet<>();
        this.gattCharacteristic = gattCharacteristic;
        if (gattCharacteristic.getValue() != null) {
            RxBleValue initialValue = new BaseValue(gattCharacteristic.getValue());
            if (this.shareValues) {
                sharedValueProvider.setValue(initialValue).blockingAwait();
            } else {
                // TODO: 2020-01-29 initialize client values?
            }
        }
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
        }).doOnComplete(() -> {
            descriptor.setParentCharacteristic(this);
            descriptors.add(descriptor);
            Timber.d("Added descriptor: %s", descriptor);
        });
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
    public Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleCharacteristicReadRequest request) {
        return createReadRequestResponse((RxBleReadRequest) request);
    }

    @Override
    public Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleCharacteristicWriteRequest request) {
        return createWriteRequestResponse((RxBleWriteRequest) request);
    }

    @Override
    public Completable setValue(@NonNull RxBleValue value) {
        return super.setValue(value);
    }

    @Override
    public Completable setValue(@NonNull RxBleClient client, @NonNull RxBleValue value) {
        return super.setValue(client, value);
    }

    @Override
    public Completable notifyClients() {
        return getClientCharacteristicNotification()
                .flatMapCompletable(ClientCharacteristicConfiguration::notifyClientsIfEnabled);
    }

    @Override
    public Completable notifyClient(@NonNull RxBleClient client) {
        return parentService.getParentServer().getGattServer()
                .flatMapCompletable(gattServer -> Completable.fromAction(() -> {
                    BluetoothDevice bluetoothDevice = client.getBluetoothDevice();
                    gattServer.notifyCharacteristicChanged(bluetoothDevice, gattCharacteristic, false);
                    // TODO: 2020-02-04 wait for onNotificationSent callback
                }));
    }

    @Override
    public RxBleService getParentService() {
        return parentService;
    }

    @Override
    public void setParentService(@NonNull RxBleService parentService) {
        this.parentService = parentService;
    }

    @Override
    public boolean hasProperty(int property) {
        return (gattCharacteristic.getProperties() & property) == property;
    }

    @Override
    public boolean hasPermission(int permission) {
        return (gattCharacteristic.getPermissions() & permission) == permission;
    }

    protected Maybe<ClientCharacteristicConfiguration> getClientCharacteristicNotification() {
        return Observable.defer(() -> Observable.fromIterable(descriptors))
                .filter(descriptor -> descriptor instanceof ClientCharacteristicConfiguration)
                .cast(ClientCharacteristicConfiguration.class)
                .firstElement();
    }

    @Override
    public String toString() {
        return "BaseCharacteristic{" +
                "uuid=" + gattCharacteristic.getUuid() +
                ", descriptors=" + descriptors +
                ", sharedValue=" + sharedValueProvider.getValue().blockingGet() +
                '}';
    }

}

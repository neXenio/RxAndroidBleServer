package com.nexenio.rxandroidbleserver.callback;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;

import com.nexenio.rxandroidbleserver.RxBleServerMapper;
import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.characteristic.BaseCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.BaseCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class RxBleServerCallbackMediator {

    private final BluetoothGattServerCallback androidCallback;
    private final RxBleServerCallback serverCallback;
    private final RxBleServerMapper serverMapper;

    private CompositeDisposable callbackDisposable;

    public RxBleServerCallbackMediator(@NonNull RxBleServerCallback serverCallback, @NonNull RxBleServerMapper serverMapper) {
        this.serverCallback = serverCallback;
        this.serverMapper = serverMapper;
        this.androidCallback = createAndroidCallback();
        this.callbackDisposable = new CompositeDisposable();
    }

    public BluetoothGattServerCallback getAndroidCallback() {
        return androidCallback;
    }

    private Single<RxBleClient> getClient(@NonNull BluetoothDevice device) {
        return serverMapper.getClient(device);
    }

    private Single<RxBleService> getService(@NonNull BluetoothGattService service) {
        return serverMapper.getService(service);
    }

    private Single<RxBleCharacteristic> getCharacteristic(@NonNull BluetoothGattCharacteristic characteristic) {
        return serverMapper.getCharacteristic(characteristic);
    }

    private Single<RxBleDescriptor> getDescriptor(@NonNull BluetoothGattDescriptor descriptor) {
        return serverMapper.getDescriptor(descriptor);
    }

    private Single<RxBleCharacteristicReadRequest> createCharacteristicReadRequest(BluetoothDevice device, BluetoothGattCharacteristic gattCharacteristic, int id, int offset) {
        return Single.zip(getClient(device), getCharacteristic(gattCharacteristic),
                (client, characteristic) -> new BaseCharacteristicReadRequest(client, characteristic, id, offset));
    }

    private Single<RxBleCharacteristicWriteRequest> createCharacteristicWriteRequest(BluetoothDevice device, BluetoothGattCharacteristic gattCharacteristic, int id, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
        return Single.zip(getClient(device), getCharacteristic(gattCharacteristic),
                (client, characteristic) -> new BaseCharacteristicWriteRequest(client, characteristic, id, preparedWrite, responseNeeded, offset, new BaseValue(value)));
    }

    private void handleCallbackError(@NonNull Throwable throwable) {
        // TODO: 1/26/2020 handle callback error
        System.out.println("Callback error: " + throwable);
    }

    private BluetoothGattServerCallback createAndroidCallback() {
        return new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                callbackDisposable.add(getClient(device)
                        .map(client -> {
                            client.setConnectionState(newState);
                            return client;
                        })
                        .subscribe(
                                client -> serverCallback.getClientConnectionStateChangePublisher().onNext(client),
                                RxBleServerCallbackMediator.this::handleCallbackError
                        ));
            }

            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int id, int offset, BluetoothGattCharacteristic gattCharacteristic) {
                super.onCharacteristicReadRequest(device, id, offset, gattCharacteristic);
                callbackDisposable.add(createCharacteristicReadRequest(device, gattCharacteristic, id, offset)
                        .subscribe(
                                request -> serverCallback.getCharacteristicReadRequestPublisher().onNext(request),
                                RxBleServerCallbackMediator.this::handleCallbackError
                        ));
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int id, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onCharacteristicWriteRequest(device, id, characteristic, preparedWrite, responseNeeded, offset, value);
                callbackDisposable.add(createCharacteristicWriteRequest(device, characteristic, id, preparedWrite, responseNeeded, offset, value)
                        .subscribe(
                                request -> serverCallback.getCharacteristicWriteRequestPublisher().onNext(request),
                                RxBleServerCallbackMediator.this::handleCallbackError
                        ));
            }

            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            }

            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                super.onExecuteWrite(device, requestId, execute);
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                super.onNotificationSent(device, status);
            }

            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {
                super.onMtuChanged(device, mtu);
            }

            @Override
            public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
                super.onPhyUpdate(device, txPhy, rxPhy, status);
            }

            @Override
            public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
                super.onPhyRead(device, txPhy, rxPhy, status);
            }
        };
    }

}

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
import timber.log.Timber;

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
        Timber.w(throwable, "handleCallbackError() called");
        // TODO: 1/26/2020 handle callback error
    }

    private BluetoothGattServerCallback createAndroidCallback() {
        return new BluetoothGattServerCallback() {
            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                Timber.d("onConnectionStateChange() called with: device = [%s], status = [%s], newState = [%s]", device, status, newState);
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
                Timber.d("onServiceAdded() called with: status = [%s], service = [%s]", status, service);
                super.onServiceAdded(status, service);
            }

            @Override
            public void onCharacteristicReadRequest(BluetoothDevice device, int id, int offset, BluetoothGattCharacteristic gattCharacteristic) {
                Timber.d("onCharacteristicReadRequest() called with: device = [%s], id = [%s], offset = [%s], gattCharacteristic = [%s]", device, id, offset, gattCharacteristic);
                super.onCharacteristicReadRequest(device, id, offset, gattCharacteristic);
                callbackDisposable.add(createCharacteristicReadRequest(device, gattCharacteristic, id, offset)
                        .subscribe(
                                request -> serverCallback.getCharacteristicReadRequestPublisher().onNext(request),
                                RxBleServerCallbackMediator.this::handleCallbackError
                        ));
            }

            @Override
            public void onCharacteristicWriteRequest(BluetoothDevice device, int id, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                Timber.d("onCharacteristicWriteRequest() called with: device = [%s], id = [%s], characteristic = [%s], preparedWrite = [%s], responseNeeded = [%s], offset = [%s], value = [%s]", device, id, characteristic, preparedWrite, responseNeeded, offset, value);
                super.onCharacteristicWriteRequest(device, id, characteristic, preparedWrite, responseNeeded, offset, value);
                callbackDisposable.add(createCharacteristicWriteRequest(device, characteristic, id, preparedWrite, responseNeeded, offset, value)
                        .subscribe(
                                request -> serverCallback.getCharacteristicWriteRequestPublisher().onNext(request),
                                RxBleServerCallbackMediator.this::handleCallbackError
                        ));
            }

            @Override
            public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
                Timber.d("onDescriptorReadRequest() called with: device = [%s], requestId = [%s], offset = [%s], descriptor = [%s]", device, requestId, offset, descriptor);
                super.onDescriptorReadRequest(device, requestId, offset, descriptor);
            }

            @Override
            public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
                Timber.d("onDescriptorWriteRequest() called with: device = [%s], requestId = [%s], descriptor = [%s], preparedWrite = [%s], responseNeeded = [%s], offset = [%s], value = [%s]", device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
                super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
            }

            @Override
            public void onExecuteWrite(BluetoothDevice device, int requestId, boolean execute) {
                Timber.d("onExecuteWrite() called with: device = [%s], requestId = [%s], execute = [%s]", device, requestId, execute);
                super.onExecuteWrite(device, requestId, execute);
            }

            @Override
            public void onNotificationSent(BluetoothDevice device, int status) {
                Timber.d("onNotificationSent() called with: device = [%s], status = [%s]", device, status);
                super.onNotificationSent(device, status);
            }

            @Override
            public void onMtuChanged(BluetoothDevice device, int mtu) {
                Timber.d("onMtuChanged() called with: device = [%s], mtu = [%s]", device, mtu);
                super.onMtuChanged(device, mtu);
            }

            @Override
            public void onPhyUpdate(BluetoothDevice device, int txPhy, int rxPhy, int status) {
                Timber.d("onPhyUpdate() called with: device = [%s], txPhy = [%s], rxPhy = [%s], status = [%s]", device, txPhy, rxPhy, status);
                super.onPhyUpdate(device, txPhy, rxPhy, status);
            }

            @Override
            public void onPhyRead(BluetoothDevice device, int txPhy, int rxPhy, int status) {
                Timber.d("onPhyRead() called with: device = [%s], txPhy = [%s], rxPhy = [%s], status = [%s]", device, txPhy, rxPhy, status);
                super.onPhyRead(device, txPhy, rxPhy, status);
            }
        };
    }

}

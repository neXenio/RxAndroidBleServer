package com.nexenio.rxandroidbleserver.service.characteristic.descriptor;

import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;

public class ClientCharacteristicConfiguration extends BaseDescriptor {

    public static final UUID UUID = java.util.UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    private static final int PERMISSIONS = BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE;

    private final RxBleValue ENABLE_NOTIFICATION_VALUE = new BaseValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
    private final RxBleValue DISABLE_NOTIFICATION_VALUE = new BaseValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

    private final Set<RxBleClient> clientsWithNotificationsEnabled;

    public ClientCharacteristicConfiguration() {
        super(UUID, PERMISSIONS);
        clientsWithNotificationsEnabled = new HashSet<>();
        RxBleValue value = new BaseValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        sharedValueProvider.setValue(value).blockingAwait();
    }

    @Override
    public Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleDescriptorWriteRequest request) {
        return updateClient(request)
                .andThen(super.createWriteRequestResponse(request));
    }

    private Completable updateClient(@NonNull RxBleDescriptorWriteRequest request) {
        return Completable.defer(() -> {
            if (ENABLE_NOTIFICATION_VALUE.equals(request.getValue())) {
                return enableNotifications(request.getClient());
            } else if (DISABLE_NOTIFICATION_VALUE.equals(request.getValue())) {
                return disableNotifications(request.getClient());
            } else {
                return Completable.complete();
            }
        });
    }

    private Completable enableNotifications(@NonNull RxBleClient client) {
        return setValue(client, ENABLE_NOTIFICATION_VALUE)
                .doOnComplete(() -> {
                    clientsWithNotificationsEnabled.add(client);
                    Timber.d("Enabled notifications for: %s", client);
                });
    }

    private Completable disableNotifications(@NonNull RxBleClient client) {
        return setValue(client, DISABLE_NOTIFICATION_VALUE)
                .doOnComplete(() -> {
                    clientsWithNotificationsEnabled.remove(client);
                    Timber.d("Disabled notifications for: %s", client);
                });
    }

    public Set<RxBleClient> getClientsWithNotificationsEnabled() {
        return Collections.unmodifiableSet(clientsWithNotificationsEnabled);
    }

    public boolean hasNotificationsEnabled(@NonNull RxBleClient client) {
        return clientsWithNotificationsEnabled.contains(client);
    }

    public Completable notifyClientsIfEnabled() {
        return Observable.defer(() -> Observable.fromIterable(clientsWithNotificationsEnabled))
                .filter(RxBleClient::isConnected)
                .flatMapCompletable(client -> notifyClient(client)
                        .doOnError(throwable -> Timber.w(throwable, "Unable to notify client: %s", client))
                        .onErrorComplete());
    }

    public Completable notifyClientIfEnabled(@NonNull RxBleClient client) {
        return Completable.defer(() -> {
            if (!hasNotificationsEnabled(client) || !client.isConnected()) {
                return Completable.complete();
            } else {
                return notifyClient(client);
            }
        });
    }

    public Completable notifyClient(@NonNull RxBleClient client) {
        return Completable.defer(() -> parentCharacteristic.sendNotification(client));
    }

    public Completable indicateClient(@NonNull RxBleClient client) {
        return Completable.defer(() -> parentCharacteristic.sendIndication(client));
    }

}

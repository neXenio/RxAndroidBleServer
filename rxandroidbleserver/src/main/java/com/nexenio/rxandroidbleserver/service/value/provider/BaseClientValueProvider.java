package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.ValueNotAvailableException;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;

public class BaseClientValueProvider implements RxBleClientValueProvider {

    private final ReplaySubject<RxBleClient> clientPublisher;

    private final Map<RxBleClient, RxBleValue> valueMap;
    private final Map<RxBleClient, PublishSubject<RxBleValue>> valuePublisherMap;

    public BaseClientValueProvider() {
        this.valueMap = new HashMap<>();
        this.valuePublisherMap = new HashMap<>();
        this.clientPublisher = ReplaySubject.create();
    }

    @Override
    public Single<RxBleValue> getValue(@NonNull RxBleClient client) {
        return Single.defer(() -> {
            synchronized (valueMap) {
                if (valueMap.containsKey(client)) {
                    return Single.just(valueMap.get(client));
                } else {
                    return Single.error(new ValueNotAvailableException(client));
                }
            }
        });
    }

    @Override
    public Observable<RxBleValue> getValuesFromAllClients() {
        return getCurrentClients().flatMapSingle(this::getValue);
    }

    @Override
    public Completable setValue(@NonNull RxBleClient client, @NonNull RxBleValue value) {
        return Completable.fromAction(() -> {
            boolean isNewClient;
            synchronized (valueMap) {
                isNewClient = !valueMap.containsKey(client);
                valueMap.put(client, value);
            }
            if (isNewClient) {
                clientPublisher.onNext(client);
            }
            getOrCreateValuePublisher(client).onNext(value);
        });
    }

    @Override
    public Completable setValueForAllClients(@NonNull RxBleValue value) {
        return getCurrentClients().flatMapCompletable(client -> setValue(client, value));
    }

    @Override
    public Observable<RxBleValue> getValueChanges(@NonNull RxBleClient client) {
        return getOrCreateValuePublisher(client);
    }

    @Override
    public Observable<RxBleValue> getValueChangesFromAllClients() {
        return getCurrentAndFutureClients().flatMap(this::getValueChanges);
    }

    private Observable<RxBleClient> getCurrentClients() {
        return Observable.defer(() -> {
            Collection<RxBleClient> clients;
            synchronized (valueMap) {
                clients = new HashSet<>(valueMap.keySet());
            }
            return Observable.fromIterable(clients);
        });
    }

    private Observable<RxBleClient> getCurrentAndFutureClients() {
        return clientPublisher;
    }

    private PublishSubject<RxBleValue> getOrCreateValuePublisher(@NonNull RxBleClient client) {
        synchronized (valuePublisherMap) {
            if (valuePublisherMap.containsKey(client)) {
                return valuePublisherMap.get(client);
            } else {
                PublishSubject<RxBleValue> valuePublisher = PublishSubject.create();
                valuePublisherMap.put(client, valuePublisher);
                return valuePublisher;
            }
        }
    }

    @Override
    public String toString() {
        return "BaseClientValueProvider{" +
                "valueMap=" + valueMap +
                '}';
    }

}

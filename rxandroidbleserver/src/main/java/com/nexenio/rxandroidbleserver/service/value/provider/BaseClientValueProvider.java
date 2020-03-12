package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.ValueNotAvailableException;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class BaseClientValueProvider implements RxBleClientValueProvider {

    private final Map<RxBleClient, RxBleValue> valueMap;
    private final Map<RxBleClient, PublishSubject<RxBleValue>> valuePublisherMap;

    public BaseClientValueProvider() {
        this.valueMap = new HashMap<>();
        this.valuePublisherMap = new HashMap<>();
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
    public Completable setValue(@NonNull RxBleClient client, @NonNull RxBleValue value) {
        return Completable.fromAction(() -> {
            synchronized (valueMap) {
                valueMap.put(client, value);
            }
            getOrCreateValuePublisher(client).onNext(value);
        });
    }

    @Override
    public Observable<RxBleValue> getValueChanges(@NonNull RxBleClient client) {
        return getOrCreateValuePublisher(client);
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

}

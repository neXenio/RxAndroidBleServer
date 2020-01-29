package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

public class BaseSharedValueProvider implements RxBleSharedValueProvider {

    private RxBleValue value;

    private final PublishSubject<RxBleValue> valuePublisher;

    public BaseSharedValueProvider() {
        this.valuePublisher = PublishSubject.create();
    }

    public BaseSharedValueProvider(@NonNull RxBleValue value) {
        this();
        this.value = value;
    }

    @Override
    public Single<RxBleValue> getValue() {
        return Single.defer(() -> Single.just(value));
    }

    @Override
    public Completable setValue(@NonNull RxBleValue value) {
        return Completable.fromAction(() -> {
            this.value = value;
            if (!valuePublisher.hasComplete() && !valuePublisher.hasThrowable()) {
                valuePublisher.onNext(value);
            }
        });
    }

    @Override
    public Observable<RxBleValue> getValueChanges() {
        return valuePublisher;
    }

}

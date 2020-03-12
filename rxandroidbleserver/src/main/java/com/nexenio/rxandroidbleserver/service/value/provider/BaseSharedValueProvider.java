package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class BaseSharedValueProvider implements RxBleSharedValueProvider {

    private RxBleValue value;

    private final PublishSubject<RxBleValue> valuePublisher;

    public BaseSharedValueProvider() {
        this.value = new BaseValue();
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

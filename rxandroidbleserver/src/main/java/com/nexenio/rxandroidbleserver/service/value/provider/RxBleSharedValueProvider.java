package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface RxBleSharedValueProvider {

    Single<RxBleValue> getValue();

    Completable setValue(@NonNull RxBleValue value);

    Observable<RxBleValue> getValueChanges();

}

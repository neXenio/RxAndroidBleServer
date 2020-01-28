package com.nexenio.rxandroidbleserver.service;

import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface RxBleSharedValueProvider {

    Single<RxBleValue> getValue();

    Completable setValue(@NonNull RxBleValue value);

}

package com.nexenio.rxandroidbleserver.service;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface RxBleClientValueProvider {

    Single<RxBleValue> getValue(@NonNull RxBleClient client);

    Completable setValue(@NonNull RxBleValue value, @NonNull RxBleClient client);

}

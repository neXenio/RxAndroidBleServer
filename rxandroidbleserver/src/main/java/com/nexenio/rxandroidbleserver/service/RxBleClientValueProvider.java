package com.nexenio.rxandroidbleserver.service;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface RxBleClientValueProvider {

    Single<byte[]> getValue(@NonNull RxBleClient client);

    Completable setValue(@NonNull byte[] value, @NonNull RxBleClient client);

}

package com.nexenio.rxandroidbleserver.service;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface RxBleSharedValueProvider {

    Single<byte[]> getValue();

    Completable setValue(@NonNull byte[] value);

}

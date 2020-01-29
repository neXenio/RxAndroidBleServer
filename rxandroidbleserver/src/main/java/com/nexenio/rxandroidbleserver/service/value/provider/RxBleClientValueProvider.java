package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface RxBleClientValueProvider {

    Single<RxBleValue> getValue(@NonNull RxBleClient client);

    Completable setValue(@NonNull RxBleClient client, @NonNull RxBleValue value);

    Observable<RxBleValue> getValueChanges(@NonNull RxBleClient client);

}

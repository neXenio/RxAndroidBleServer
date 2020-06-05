package com.nexenio.rxandroidbleserver.service.value.provider;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface RxBleClientValueProvider {

    Single<RxBleValue> getValue(@NonNull RxBleClient client);

    Observable<RxBleValue> getValuesFromAllClients();

    Completable setValue(@NonNull RxBleClient client, @NonNull RxBleValue value);

    Completable setValueForAllClients(@NonNull RxBleValue value);

    Observable<RxBleValue> getValueChanges(@NonNull RxBleClient client);

    Observable<RxBleValue> getValueChangesFromAllClients();

}

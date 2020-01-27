package com.nexenio.rxandroidbleserver;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.RxBleService;

import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;

public interface RxBleServer {

    Completable provideServices();

    Completable advertiseService(@NonNull UUID uuid);

    Completable addService(@NonNull RxBleService service);

    Completable removeService(@NonNull RxBleService service);

    Set<RxBleService> getServices();

    Set<RxBleClient> getClients();

    Completable connect(@NonNull RxBleClient client);

    Completable disconnect(@NonNull RxBleClient client);

    Observable<RxBleClient> observerClientConnectionStateChanges();

}

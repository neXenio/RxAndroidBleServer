package com.nexenio.rxandroidbleserver;

import android.content.Context;

import com.nexenio.rxandroidbleserver.service.RxBleService;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Observable;

public class ServerBuilder {

    private final Context context;

    private final Set<RxBleService> services;

    public ServerBuilder(@NonNull Context context) {
        this.context = context;
        services = new HashSet<>();
    }

    public RxBleServer build() {
        BaseServer service = new BaseServer(context);

        Observable.fromIterable(services)
                .flatMapCompletable(service::addService)
                .blockingAwait();

        return service;
    }

    public ServerBuilder withService(@NonNull RxBleService service) {
        this.services.add(service);
        return this;
    }

}

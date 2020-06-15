package com.nexenio.rxandroidbleserver.service.value;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.RxBleServerException;
import com.nexenio.rxandroidbleserver.request.RxBleReadRequest;
import com.nexenio.rxandroidbleserver.request.RxBleWriteRequest;
import com.nexenio.rxandroidbleserver.response.BaseServerResponse;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.response.ServerErrorResponse;
import com.nexenio.rxandroidbleserver.response.ServerWriteResponse;
import com.nexenio.rxandroidbleserver.service.value.provider.BaseClientValueProvider;
import com.nexenio.rxandroidbleserver.service.value.provider.BaseSharedValueProvider;
import com.nexenio.rxandroidbleserver.service.value.provider.RxBleClientValueProvider;
import com.nexenio.rxandroidbleserver.service.value.provider.RxBleSharedValueProvider;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class BaseValueContainer implements RxBleValueContainer {

    protected boolean shareValues = true;
    protected final RxBleSharedValueProvider sharedValueProvider;
    protected final RxBleClientValueProvider clientValueProvider;

    public BaseValueContainer() {
        this.sharedValueProvider = new BaseSharedValueProvider();
        this.clientValueProvider = new BaseClientValueProvider();
    }

    @Override
    public Single<RxBleValue> getValue() {
        return sharedValueProvider.getValue();
    }

    @Override
    public Completable setValue(@NonNull RxBleValue value) {
        return Completable.mergeArray(
                sharedValueProvider.setValue(value),
                clientValueProvider.setValueForAllClients(value)
        );
    }

    @Override
    public Observable<RxBleValue> getValueChanges() {
        return Observable.defer(() -> {
            if (shareValues) {
                return sharedValueProvider.getValueChanges();
            } else {
                return clientValueProvider.getValueChangesFromAllClients();
            }
        });
    }

    @Override
    public Single<RxBleValue> getValue(@NonNull RxBleClient client) {
        return Single.defer(() -> {
            if (shareValues) {
                return sharedValueProvider.getValue();
            } else {
                return clientValueProvider.getValue(client);
            }
        });
    }

    @Override
    public Observable<RxBleValue> getValuesFromAllClients() {
        return Observable.defer(() -> {
            if (shareValues) {
                return sharedValueProvider.getValue().toObservable();
            } else {
                return clientValueProvider.getValuesFromAllClients();
            }
        });
    }

    @Override
    public Completable setValue(@NonNull RxBleClient client, @NonNull RxBleValue value) {
        return Completable.defer(() -> {
            if (shareValues) {
                return sharedValueProvider.setValue(value);
            } else {
                return clientValueProvider.setValue(client, value);
            }
        });
    }

    @Override
    public Completable setValueForAllClients(@NonNull RxBleValue value) {
        return Completable.defer(() -> {
            if (shareValues) {
                return sharedValueProvider.setValue(value);
            } else {
                return clientValueProvider.setValueForAllClients(value);
            }
        });
    }

    @Override
    public Observable<RxBleValue> getValueChanges(@NonNull RxBleClient client) {
        return clientValueProvider.getValueChanges(client);
    }

    @Override
    public Observable<RxBleValue> getValueChangesFromAllClients() {
        return Observable.defer(() -> {
            if (shareValues) {
                return sharedValueProvider.getValueChanges();
            } else {
                return clientValueProvider.getValueChangesFromAllClients();
            }
        });
    }

    public Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleReadRequest request) {
        return getValue(request.getClient())
                .map(value -> new BaseServerResponse(request, value))
                .cast(RxBleServerResponse.class)
                .onErrorReturnItem(new ServerErrorResponse(request));
    }

    public Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleWriteRequest request) {
        Completable writeValue = Completable.defer(() -> {
            if (request.getOffset() == 0) {
                if (shareValues) {
                    return setValue(request.getValue());
                } else {
                    return setValue(request.getClient(), request.getValue());
                }
            } else {
                // TODO: 1/26/2020 implement long writes support
                return Completable.error(new RxBleServerException("Long writes are not yet supported"));
            }
        });

        Maybe<RxBleServerResponse> createResponse = Maybe.just(new ServerWriteResponse(request))
                .cast(RxBleServerResponse.class);

        return writeValue.andThen(Maybe.defer(() -> {
            if (request.isResponseNeeded()) {
                return createResponse;
            } else {
                return Maybe.empty();
            }
        })).onErrorReturnItem(new ServerErrorResponse(request));
    }

    @Override
    public String toString() {
        return "BaseValueContainer{" +
                "shareValues=" + shareValues +
                ", sharedValueProvider=" + sharedValueProvider +
                ", clientValueProvider=" + clientValueProvider +
                '}';
    }

    @Override
    public boolean isSharingValuesBetweenClients() {
        return shareValues;
    }

    @Override
    public void shareValuesBetweenClients(boolean shareValues) {
        this.shareValues = shareValues;
    }

    public RxBleSharedValueProvider getSharedValueProvider() {
        return sharedValueProvider;
    }

    public RxBleClientValueProvider getClientValueProvider() {
        return clientValueProvider;
    }

}

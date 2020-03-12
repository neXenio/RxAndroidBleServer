package com.nexenio.rxandroidbleserver.service.value;

import com.nexenio.rxandroidbleserver.request.RxBleReadRequest;
import com.nexenio.rxandroidbleserver.request.RxBleWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.value.provider.RxBleClientValueProvider;
import com.nexenio.rxandroidbleserver.service.value.provider.RxBleSharedValueProvider;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface RxBleValueContainer extends RxBleClientValueProvider, RxBleSharedValueProvider {

    Single<RxBleServerResponse> createReadRequestResponse(@NonNull RxBleReadRequest request);

    Maybe<RxBleServerResponse> createWriteRequestResponse(@NonNull RxBleWriteRequest request);

}

package com.nexenio.rxandroidbleserver.request;

import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

public interface RxBleWriteRequest extends RxBleServerRequest {

    boolean shouldPreparedWrite();

    boolean isResponseNeeded();

    RxBleValue getValue();

}

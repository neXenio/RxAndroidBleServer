package com.nexenio.rxandroidbleserver.request;

public interface RxBleWriteRequest extends RxBleServerRequest {

    boolean shouldPreparedWrite();

    boolean isResponseNeeded();

    byte[] getValue();

}

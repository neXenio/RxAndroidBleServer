package com.nexenio.rxandroidbleserver.response;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

public interface RxBleServerResponse {

    RxBleClient getClient();

    int getRequestId();

    int getStatus();

    int getOffset();

    RxBleValue getData();

}

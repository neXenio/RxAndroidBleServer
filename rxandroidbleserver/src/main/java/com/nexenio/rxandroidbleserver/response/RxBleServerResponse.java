package com.nexenio.rxandroidbleserver.response;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

public interface RxBleServerResponse {

    RxBleClient getClient();

    int getRequestId();

    int getStatus();

    int getOffset();

    byte[] getData();

}

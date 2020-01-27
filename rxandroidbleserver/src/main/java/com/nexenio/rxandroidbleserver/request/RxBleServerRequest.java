package com.nexenio.rxandroidbleserver.request;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

public interface RxBleServerRequest {

    RxBleClient getClient();

    int getId();

    int getOffset();

}

package com.nexenio.rxandroidbleserver.request;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

public abstract class BaseServerRequest implements RxBleServerRequest {

    protected RxBleClient client;

    protected int requestId;

    protected int offset;

    public BaseServerRequest(RxBleClient client, int requestId, int offset) {
        this.client = client;
        this.requestId = requestId;
        this.offset = offset;
    }

    @Override
    public RxBleClient getClient() {
        return client;
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "BaseServerRequest{" +
                "client=" + client +
                ", requestId=" + requestId +
                ", offset=" + offset +
                '}';
    }

}

package com.nexenio.rxandroidbleserver.request;

import com.nexenio.rxandroidbleserver.client.RxBleClient;

public abstract class BaseServerRequest implements RxBleServerRequest {

    protected RxBleClient client;

    protected int id;

    protected int offset;

    public BaseServerRequest(RxBleClient client, int id, int offset) {
        this.client = client;
        this.id = id;
        this.offset = offset;
    }

    @Override
    public RxBleClient getClient() {
        return client;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "BaseServerRequest{" +
                "client=" + client +
                ", id=" + id +
                ", offset=" + offset +
                '}';
    }

}

package com.nexenio.rxandroidbleserver.request.characteristic;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

public class BaseCharacteristicReadRequest implements RxBleCharacteristicReadRequest {

    private RxBleCharacteristic characteristic;

    private RxBleClient client;

    private int id;

    private int offset;

    public BaseCharacteristicReadRequest(RxBleClient client, RxBleCharacteristic characteristic, int id, int offset) {
        this.characteristic = characteristic;
        this.client = client;
        this.id = id;
        this.offset = offset;
    }

    @Override
    public RxBleCharacteristic getCharacteristic() {
        return characteristic;
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
        return "BaseCharacteristicReadRequest{" +
                "characteristic=" + characteristic +
                ", client=" + client +
                ", id=" + id +
                ", offset=" + offset +
                '}';
    }

}

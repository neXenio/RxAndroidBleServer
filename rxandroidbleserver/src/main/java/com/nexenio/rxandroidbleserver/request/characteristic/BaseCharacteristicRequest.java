package com.nexenio.rxandroidbleserver.request.characteristic;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.BaseServerRequest;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

public class BaseCharacteristicRequest extends BaseServerRequest implements RxBleCharacteristicRequest {

    protected RxBleCharacteristic characteristic;

    public BaseCharacteristicRequest(RxBleClient client, RxBleCharacteristic characteristic, int id, int offset) {
        super(client, id, offset);
        this.characteristic = characteristic;
    }

    @Override
    public RxBleCharacteristic getCharacteristic() {
        return characteristic;
    }

}

package com.nexenio.rxandroidbleserver.request.characteristic;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

public class BaseCharacteristicReadRequest extends BaseCharacteristicRequest implements RxBleCharacteristicReadRequest {

    public BaseCharacteristicReadRequest(RxBleClient client, RxBleCharacteristic characteristic, int id, int offset) {
        super(client, characteristic, id, offset);
    }

    @Override
    public String toString() {
        return "BaseDescriptorReadRequest{" +
                "descriptor=" + characteristic +
                ", client=" + client +
                ", requestId=" + requestId +
                ", offset=" + offset +
                '}';
    }

}

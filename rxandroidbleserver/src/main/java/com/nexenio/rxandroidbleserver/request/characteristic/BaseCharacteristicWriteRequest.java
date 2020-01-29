package com.nexenio.rxandroidbleserver.request.characteristic;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

public class BaseCharacteristicWriteRequest extends BaseCharacteristicRequest implements RxBleCharacteristicWriteRequest {

    protected boolean shouldPrepareWrite;

    protected boolean isResponseNeeded;

    protected RxBleValue value;

    public BaseCharacteristicWriteRequest(RxBleClient client, RxBleCharacteristic characteristic, int id, boolean shouldPrepareWrite, boolean isResponseNeeded, int offset, RxBleValue value) {
        super(client, characteristic, id, offset);
        this.shouldPrepareWrite = shouldPrepareWrite;
        this.isResponseNeeded = isResponseNeeded;
        this.value = value;
    }

    @Override
    public boolean shouldPreparedWrite() {
        return shouldPrepareWrite;
    }

    @Override
    public boolean isResponseNeeded() {
        return isResponseNeeded;
    }

    @Override
    public RxBleValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BaseCharacteristicWriteRequest{" +
                " characteristic=" + characteristic +
                ", client=" + client +
                ", requestId=" + requestId +
                ", offset=" + offset +
                ", shouldPrepareWrite=" + shouldPrepareWrite +
                ", isResponseNeeded=" + isResponseNeeded +
                ", value=" + value +
                '}';
    }

}

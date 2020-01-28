package com.nexenio.rxandroidbleserver.request.characteristic;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

import java.util.Arrays;

public class BaseCharacteristicWriteRequest extends BaseCharacteristicRequest implements RxBleCharacteristicWriteRequest {

    protected boolean shouldPrepareWrite;

    protected boolean isResponseNeeded;

    protected byte[] value;

    public BaseCharacteristicWriteRequest(RxBleClient client, RxBleCharacteristic characteristic, int id, boolean shouldPrepareWrite, boolean isResponseNeeded, int offset, byte[] value) {
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
    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BaseCharacteristicWriteRequest{" +
                " characteristic=" + characteristic +
                ", client=" + client +
                ", id=" + id +
                ", offset=" + offset +
                ", shouldPrepareWrite=" + shouldPrepareWrite +
                ", isResponseNeeded=" + isResponseNeeded +
                ", value=" + Arrays.toString(value) +
                '}';
    }

}

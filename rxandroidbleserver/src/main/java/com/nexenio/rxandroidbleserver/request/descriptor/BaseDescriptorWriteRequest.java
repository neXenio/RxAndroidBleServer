package com.nexenio.rxandroidbleserver.request.descriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

public class BaseDescriptorWriteRequest extends BaseDescriptorRequest implements RxBleDescriptorWriteRequest {

    protected boolean shouldPrepareWrite;

    protected boolean isResponseNeeded;

    protected RxBleValue value;

    public BaseDescriptorWriteRequest(RxBleClient client, RxBleDescriptor descriptor, int id, boolean shouldPrepareWrite, boolean isResponseNeeded, int offset, RxBleValue value) {
        super(client, descriptor, id, offset);
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
        return "BaseDescriptorWriteRequest{" +
                " descriptor=" + descriptor +
                ", client=" + client +
                ", requestId=" + requestId +
                ", offset=" + offset +
                ", shouldPrepareWrite=" + shouldPrepareWrite +
                ", isResponseNeeded=" + isResponseNeeded +
                ", value=" + value +
                '}';
    }

}

package com.nexenio.rxandroidbleserver.request.descriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;

public class BaseDescriptorReadRequest extends BaseDescriptorRequest implements RxBleDescriptorReadRequest {

    public BaseDescriptorReadRequest(RxBleClient client, RxBleDescriptor descriptor, int id, int offset) {
        super(client, descriptor, id, offset);
    }

    @Override
    public String toString() {
        return "BaseDescriptorReadRequest{" +
                "descriptor=" + descriptor +
                ", client=" + client +
                ", requestId=" + requestId +
                ", offset=" + offset +
                '}';
    }

}

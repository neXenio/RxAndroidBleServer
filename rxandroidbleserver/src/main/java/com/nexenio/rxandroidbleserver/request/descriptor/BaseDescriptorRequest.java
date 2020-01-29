package com.nexenio.rxandroidbleserver.request.descriptor;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.BaseServerRequest;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;

public class BaseDescriptorRequest extends BaseServerRequest implements RxBleDescriptorRequest {

    protected RxBleDescriptor descriptor;

    public BaseDescriptorRequest(RxBleClient client, RxBleDescriptor descriptor, int id, int offset) {
        super(client, id, offset);
        this.descriptor = descriptor;
    }

    @Override
    public RxBleDescriptor getDescriptor() {
        return descriptor;
    }

}

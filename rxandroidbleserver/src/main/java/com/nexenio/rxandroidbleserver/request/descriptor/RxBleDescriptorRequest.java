package com.nexenio.rxandroidbleserver.request.descriptor;

import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;

public interface RxBleDescriptorRequest extends RxBleServerRequest {

    RxBleDescriptor getDescriptor();

}

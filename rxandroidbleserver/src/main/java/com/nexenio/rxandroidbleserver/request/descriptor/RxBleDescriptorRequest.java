package com.nexenio.rxandroidbleserver.request.descriptor;

import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;

public interface RxBleDescriptorRequest extends RxBleServerRequest {

    RxBleDescriptor getDescriptor();

}

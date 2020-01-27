package com.nexenio.rxandroidbleserver.request.characteristic;

import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;

public interface RxBleCharacteristicRequest extends RxBleServerRequest {

    RxBleCharacteristic getCharacteristic();

}

package com.nexenio.rxandroidbleserver.callback;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorReadRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.service.RxBleService;

import io.reactivex.rxjava3.subjects.PublishSubject;

public interface RxBleServerCallback {

    PublishSubject<RxBleClient> getClientConnectionStateChangePublisher();

    PublishSubject<RxBleService> getServiceAddedPublisher();

    PublishSubject<RxBleService> getServiceNotAddedPublisher();

    PublishSubject<RxBleCharacteristicReadRequest> getCharacteristicReadRequestPublisher();

    PublishSubject<RxBleCharacteristicWriteRequest> getCharacteristicWriteRequestPublisher();

    PublishSubject<RxBleDescriptorReadRequest> getDescriptorReadRequestPublisher();

    PublishSubject<RxBleDescriptorWriteRequest> getDescriptorWriteRequestPublisher();

}

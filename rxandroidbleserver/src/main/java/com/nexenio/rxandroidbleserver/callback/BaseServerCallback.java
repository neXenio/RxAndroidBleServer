package com.nexenio.rxandroidbleserver.callback;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorReadRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.service.RxBleService;

import io.reactivex.rxjava3.subjects.PublishSubject;

public class BaseServerCallback implements RxBleServerCallback {

    private PublishSubject<RxBleClient> clientConnectionStateChangePublisher;
    private PublishSubject<RxBleService> serviceAddedPublisher;
    private PublishSubject<RxBleService> serviceNotAddedPublisher;
    private PublishSubject<RxBleCharacteristicReadRequest> characteristicReadRequestPublisher;
    private PublishSubject<RxBleCharacteristicWriteRequest> characteristicWriteRequestPublisher;
    private PublishSubject<RxBleDescriptorReadRequest> descriptorReadRequestPublisher;
    private PublishSubject<RxBleDescriptorWriteRequest> descriptorWriteRequestPublisher;

    public BaseServerCallback() {
        clientConnectionStateChangePublisher = PublishSubject.create();
        serviceAddedPublisher = PublishSubject.create();
        serviceNotAddedPublisher = PublishSubject.create();
        characteristicReadRequestPublisher = PublishSubject.create();
        characteristicWriteRequestPublisher = PublishSubject.create();
        descriptorReadRequestPublisher = PublishSubject.create();
        descriptorWriteRequestPublisher = PublishSubject.create();
    }

    @Override
    public PublishSubject<RxBleClient> getClientConnectionStateChangePublisher() {
        return clientConnectionStateChangePublisher;
    }

    @Override
    public PublishSubject<RxBleService> getServiceAddedPublisher() {
        return serviceAddedPublisher;
    }

    @Override
    public PublishSubject<RxBleService> getServiceNotAddedPublisher() {
        return serviceNotAddedPublisher;
    }

    @Override
    public PublishSubject<RxBleCharacteristicReadRequest> getCharacteristicReadRequestPublisher() {
        return characteristicReadRequestPublisher;
    }

    @Override
    public PublishSubject<RxBleCharacteristicWriteRequest> getCharacteristicWriteRequestPublisher() {
        return characteristicWriteRequestPublisher;
    }

    @Override
    public PublishSubject<RxBleDescriptorReadRequest> getDescriptorReadRequestPublisher() {
        return descriptorReadRequestPublisher;
    }

    @Override
    public PublishSubject<RxBleDescriptorWriteRequest> getDescriptorWriteRequestPublisher() {
        return descriptorWriteRequestPublisher;
    }

}

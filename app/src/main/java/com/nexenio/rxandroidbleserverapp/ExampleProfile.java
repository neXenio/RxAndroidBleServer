package com.nexenio.rxandroidbleserverapp;

import android.content.Context;

import com.nexenio.rxandroidbleserver.RxBleServer;
import com.nexenio.rxandroidbleserver.RxBleServerProvider;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.ServiceBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.CharacteristicBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.CharacteristicUserDescription;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.ClientCharacteristicConfiguration;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.DescriptorBuilder;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public final class ExampleProfile {

    public static final UUID EXAMPLE_SERVICE_UUID = UUID.fromString("31372aa1-015c-47fc-8283-2182ace15ef3");
    public static final UUID EXAMPLE_CHARACTERISTIC_UUID = UUID.fromString("aa4cd9de-087c-4951-bb92-4ea1cc8fd7d6");
    public static final UUID EXAMPLE_DESCRIPTOR_UUID = UUID.fromString("c8976395-2170-46c1-be55-b46d31dcb61c");

    private RxBleServer exampleServer;
    private RxBleService exampleService;
    private RxBleCharacteristic exampleCharacteristic;
    private RxBleDescriptor exampleDescriptor;

    public ExampleProfile(@NonNull Context context) {
        createExampleServer(context);
    }

    public Completable updateCharacteristicValues() {
        return Observable.interval(1, TimeUnit.SECONDS)
                .map(count -> "Updated example value #" + count)
                .map(this::createExampleValue)
                .flatMapCompletable(value -> exampleCharacteristic.setValue(value)
                        .andThen(exampleCharacteristic.sendNotifications()));
    }

    public RxBleServer getExampleServer() {
        return exampleServer;
    }

    public RxBleService getExampleService() {
        return exampleService;
    }

    public RxBleCharacteristic getExampleCharacteristic() {
        return exampleCharacteristic;
    }

    public RxBleDescriptor getExampleDescriptor() {
        return exampleDescriptor;
    }

    private RxBleServer createExampleServer(@NonNull Context context) {
        exampleServer = RxBleServerProvider.createServer(context);
        exampleServer.addService(createExampleService()).blockingAwait();
        return exampleServer;
    }

    private RxBleService createExampleService() {
        exampleService = new ServiceBuilder(EXAMPLE_SERVICE_UUID)
                .withCharacteristic(createExampleCharacteristic())
                .isPrimaryService()
                .build();

        return exampleService;
    }

    private RxBleCharacteristic createExampleCharacteristic() {
        exampleCharacteristic = new CharacteristicBuilder(EXAMPLE_CHARACTERISTIC_UUID)
                .withInitialValue(createExampleValue("Initial example value"))
                .withDescriptor(new CharacteristicUserDescription("Example"))
                .withDescriptor(new ClientCharacteristicConfiguration())
                .withDescriptor(createExampleDescriptor())
                .allowRead()
                .allowWrite()
                .supportWritesWithoutResponse()
                .supportNotifications()
                .build();

        return exampleCharacteristic;
    }

    private RxBleDescriptor createExampleDescriptor() {
        exampleDescriptor = new DescriptorBuilder(EXAMPLE_DESCRIPTOR_UUID)
                .withInitialValue(createExampleValue(1337))
                .allowRead()
                .allowWrite()
                .build();

        return exampleDescriptor;
    }

    private RxBleValue createExampleValue(int number) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(number);
        return new BaseValue(buffer.array());
    }

    private RxBleValue createExampleValue(String value) {
        return new BaseValue(value.getBytes(StandardCharsets.UTF_8));
    }

}

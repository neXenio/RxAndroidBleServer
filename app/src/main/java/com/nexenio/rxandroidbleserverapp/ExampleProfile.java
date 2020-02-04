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

import java.nio.ByteBuffer;
import java.util.UUID;

import androidx.annotation.NonNull;

public final class ExampleProfile {

    public static final UUID EXAMPLE_SERVICE_UUID = UUID.fromString("31372aa1-015c-47fc-8283-2182ace15ef3");
    public static final UUID EXAMPLE_CHARACTERISTIC_UUID = UUID.fromString("aa4cd9de-087c-4951-bb92-4ea1cc8fd7d6");
    public static final UUID EXAMPLE_DESCRIPTOR_UUID = UUID.fromString("c8976395-2170-46c1-be55-b46d31dcb61c");

    private ExampleProfile() {

    }

    public static RxBleServer createExampleServer(@NonNull Context context) {
        RxBleServer server = RxBleServerProvider.createServer(context);
        server.addService(createExampleService()).blockingAwait();
        return server;
    }

    private static RxBleService createExampleService() {
        return new ServiceBuilder(EXAMPLE_SERVICE_UUID)
                .withCharacteristic(createExampleCharacteristic())
                .isPrimaryService()
                .build();
    }

    private static RxBleCharacteristic createExampleCharacteristic() {
        return new CharacteristicBuilder(EXAMPLE_CHARACTERISTIC_UUID)
                .withInitialValue(ByteBuffer.allocate(4)
                        .putInt(1337)
                        .array())
                .withDescriptor(new CharacteristicUserDescription("Example"))
                .withDescriptor(new ClientCharacteristicConfiguration())
                .withDescriptor(createExampleDescriptor())
                .allowRead()
                .allowWrite()
                .supportWritesWithoutResponse()
                .supportNotifications()
                .build();
    }

    private static RxBleDescriptor createExampleDescriptor() {
        return new DescriptorBuilder(EXAMPLE_DESCRIPTOR_UUID)
                .withInitialValue(ByteBuffer.allocate(4)
                        .putInt(42)
                        .array())
                .allowRead()
                .allowWrite()
                .build();
    }

}

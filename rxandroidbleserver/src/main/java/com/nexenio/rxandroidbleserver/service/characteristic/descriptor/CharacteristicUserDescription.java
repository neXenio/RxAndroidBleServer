package com.nexenio.rxandroidbleserver.service.characteristic.descriptor;

import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import androidx.annotation.NonNull;

public class CharacteristicUserDescription extends BaseDescriptor {

    public static final UUID UUID = java.util.UUID.fromString("00002901-0000-1000-8000-00805f9b34fb");

    private static final int PERMISSIONS = BluetoothGattDescriptor.PERMISSION_READ;

    public CharacteristicUserDescription(@NonNull String description) {
        super(UUID, PERMISSIONS);
        RxBleValue value = new BaseValue(description.getBytes(StandardCharsets.UTF_8));
        sharedValueProvider.setValue(value).blockingAwait();
    }

}

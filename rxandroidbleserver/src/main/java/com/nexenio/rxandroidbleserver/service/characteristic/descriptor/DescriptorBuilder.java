package com.nexenio.rxandroidbleserver.service.characteristic.descriptor;

import android.bluetooth.BluetoothGattDescriptor;

import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DescriptorBuilder {

    private final UUID uuid;

    private int permissions;

    @Nullable
    private RxBleValue value;

    public DescriptorBuilder(@NonNull UUID uuid) {
        this.uuid = uuid;
    }

    public RxBleDescriptor build() {
        RxBleDescriptor descriptor = new BaseDescriptor(uuid, permissions);

        if (value != null) {
            descriptor.setValue(value);
        }

        return descriptor;
    }

    public DescriptorBuilder withInitialValue(@NonNull byte[] value) {
        this.value = new BaseValue(value);
        return this;
    }

    public DescriptorBuilder withInitialValue(@NonNull RxBleValue value) {
        this.value = value;
        return this;
    }

    /*
        Permissions
     */

    public DescriptorBuilder allowRead() {
        permissions |= BluetoothGattDescriptor.PERMISSION_READ;
        return this;
    }

    public DescriptorBuilder allowEncryptedRead() {
        permissions |= BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED;
        return this;
    }

    public DescriptorBuilder allowMitmProtectedEncryptedRead() {
        permissions |= BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM;
        return this;
    }

    public DescriptorBuilder allowWrite() {
        permissions |= BluetoothGattDescriptor.PERMISSION_WRITE;
        return this;
    }

    public DescriptorBuilder allowEncryptedWrite() {
        permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED;
        return this;
    }

    public DescriptorBuilder allowMitmProtectedEncryptedWrite() {
        permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM;
        return this;
    }

    public DescriptorBuilder allowSignedWrite() {
        permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED;
        return this;
    }

    public DescriptorBuilder allowMitmProtectedSignedWrite() {
        permissions |= BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM;
        return this;
    }

}

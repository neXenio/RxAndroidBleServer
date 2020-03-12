package com.nexenio.rxandroidbleserver.service.characteristic;

import android.bluetooth.BluetoothGattCharacteristic;

import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;
import com.nexenio.rxandroidbleserver.service.value.BaseValue;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Observable;

public class CharacteristicBuilder {

    private final UUID uuid;
    private int properties;
    private int permissions;

    @Nullable
    private RxBleValue value;

    private final Set<RxBleDescriptor> descriptors;

    public CharacteristicBuilder(@NonNull UUID uuid) {
        this.uuid = uuid;
        descriptors = new HashSet<>();
    }

    public RxBleCharacteristic build() {
        RxBleCharacteristic characteristic = new BaseCharacteristic(uuid, properties, permissions);

        if (value != null) {
            characteristic.setValue(value).blockingAwait();
        }

        Observable.fromIterable(descriptors)
                .flatMapCompletable(characteristic::addDescriptor)
                .blockingAwait();

        return characteristic;
    }

    public CharacteristicBuilder withInitialValue(@NonNull byte[] value) {
        this.value = new BaseValue(value);
        return this;
    }

    public CharacteristicBuilder withInitialValue(@NonNull RxBleValue value) {
        this.value = value;
        return this;
    }

    public CharacteristicBuilder withDescriptor(@NonNull RxBleDescriptor descriptor) {
        this.descriptors.add(descriptor);
        return this;
    }

    /*
        Permissions
     */

    public CharacteristicBuilder allowRead() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_READ;
        return supportReads();
    }

    public CharacteristicBuilder allowEncryptedRead() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED;
        return supportReads();
    }

    public CharacteristicBuilder allowMitmProtectedEncryptedRead() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM;
        return supportReads();
    }

    public CharacteristicBuilder allowWrite() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE;
        return supportWrites();
    }

    public CharacteristicBuilder allowEncryptedWrite() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED;
        return supportWrites();
    }

    public CharacteristicBuilder allowMitmProtectedEncryptedWrite() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM;
        return supportWrites();
    }

    public CharacteristicBuilder allowSignedWrite() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED;
        return supportSignedWrites();
    }

    public CharacteristicBuilder allowMitmProtectedSignedWrite() {
        permissions |= BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM;
        return supportSignedWrites();
    }

    /*
        Properties
     */

    public CharacteristicBuilder supportBroadcasts() {
        properties |= BluetoothGattCharacteristic.PROPERTY_BROADCAST;
        return this;
    }

    public CharacteristicBuilder supportReads() {
        properties |= BluetoothGattCharacteristic.PROPERTY_READ;
        return this;
    }

    public CharacteristicBuilder supportWrites() {
        properties |= BluetoothGattCharacteristic.PROPERTY_WRITE;
        return this;
    }

    public CharacteristicBuilder supportWritesWithoutResponse() {
        properties |= BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;
        return this;
    }

    public CharacteristicBuilder supportSignedWrites() {
        properties |= BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE;
        return this;
    }

    public CharacteristicBuilder supportNotifications() {
        properties |= BluetoothGattCharacteristic.PROPERTY_NOTIFY;
        return this;
    }

    public CharacteristicBuilder supportIndications() {
        properties |= BluetoothGattCharacteristic.PROPERTY_INDICATE;
        return this;
    }

    public CharacteristicBuilder hasExtendedProperties() {
        properties |= BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS;
        return this;
    }

}

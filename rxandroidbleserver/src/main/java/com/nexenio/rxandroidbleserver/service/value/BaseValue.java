package com.nexenio.rxandroidbleserver.service.value;

import androidx.annotation.NonNull;

public class BaseValue implements RxBleValue {

    @NonNull
    protected byte[] bytes;

    public BaseValue(@NonNull byte[] bytes) {
        this.bytes = bytes;
    }

    public BaseValue() {
        this(new byte[]{});
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public void setBytes(@NonNull byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "BaseValue{" +
                "bytes=0x" + ValueUtil.bytesToHex(bytes) +
                '}';
    }

}

package com.nexenio.rxandroidbleserver.service.value;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseValue baseValue = (BaseValue) o;
        return Arrays.equals(bytes, baseValue.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }

}

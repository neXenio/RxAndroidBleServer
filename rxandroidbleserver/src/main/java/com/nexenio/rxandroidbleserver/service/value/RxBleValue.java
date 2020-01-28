package com.nexenio.rxandroidbleserver.service.value;

import androidx.annotation.NonNull;

public interface RxBleValue {

    byte[] getBytes();

    void setBytes(@NonNull byte[] bytes);

    // TODO: 2020-01-28 provide convenience methods for encoding / decoding other types

}

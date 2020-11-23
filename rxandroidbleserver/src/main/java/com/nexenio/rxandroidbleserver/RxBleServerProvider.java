package com.nexenio.rxandroidbleserver;

import android.content.Context;

import androidx.annotation.NonNull;

@Deprecated
public class RxBleServerProvider {

    private RxBleServerProvider() {
    }

    @Deprecated
    public static RxBleServer createServer(@NonNull Context context) {
        return new BaseServer(context);
    }

}

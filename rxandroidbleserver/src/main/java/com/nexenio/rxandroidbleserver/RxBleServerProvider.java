package com.nexenio.rxandroidbleserver;

import android.content.Context;

import androidx.annotation.NonNull;

public class RxBleServerProvider {

    private RxBleServerProvider() {
    }

    public static RxBleServer createServer(@NonNull Context context) {
        return new BaseBleServer(context);
    }

}

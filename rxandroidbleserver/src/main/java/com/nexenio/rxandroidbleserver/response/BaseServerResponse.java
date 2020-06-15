package com.nexenio.rxandroidbleserver.response;

import android.bluetooth.BluetoothGatt;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.Arrays;

import androidx.annotation.NonNull;

public class BaseServerResponse implements RxBleServerResponse {

    private final RxBleClient client;

    private final int requestId;

    private final int status;

    private final int offset;

    private final RxBleValue value;

    public BaseServerResponse(@NonNull RxBleClient client, int requestId, int status, int offset, @NonNull RxBleValue value) {
        this.client = client;
        this.requestId = requestId;
        this.status = status;
        this.offset = offset;
        this.value = value;
    }

    public BaseServerResponse(@NonNull RxBleServerRequest request, @NonNull RxBleValue value) {
        this(
                request.getClient(),
                request.getRequestId(),
                BluetoothGatt.GATT_SUCCESS,
                request.getOffset(),
                value
        );
    }

    @Override
    public RxBleClient getClient() {
        return client;
    }

    @Override
    public int getRequestId() {
        return requestId;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    @Override
    public RxBleValue getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BaseServerResponse{" +
                "client=" + client +
                ", requestId=" + requestId +
                ", status=" + status +
                ", offset=" + offset +
                ", sharedValue=" + value +
                '}';
    }

    public static byte[] trimData(byte[] data, int offset) {
        if (offset == 0) {
            return data;
        } else if (offset >= data.length) {
            return new byte[]{};
        } else {
            return Arrays.copyOfRange(data, offset, data.length);
        }
    }

}

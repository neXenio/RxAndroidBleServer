package com.nexenio.rxandroidbleserver.response;

import android.bluetooth.BluetoothGatt;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;
import com.nexenio.rxandroidbleserver.service.value.RxBleValue;

import java.util.Arrays;

public class BaseServerResponse implements RxBleServerResponse {

    private final RxBleClient client;

    private final int requestId;

    private final int status;

    private final int offset;

    private final RxBleValue data;

    public BaseServerResponse(RxBleClient client, int requestId, int status, int offset, RxBleValue data) {
        this.client = client;
        this.requestId = requestId;
        this.status = status;
        this.offset = offset;
        this.data = data;
    }

    public BaseServerResponse(RxBleServerRequest request, RxBleValue data) {
        this(
                request.getClient(),
                request.getId(),
                BluetoothGatt.GATT_SUCCESS,
                request.getOffset(),
                data
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
    public RxBleValue getData() {
        return data;
    }

    @Override
    public String toString() {
        return "BaseServerResponse{" +
                "client=" + client +
                ", requestId=" + requestId +
                ", status=" + status +
                ", offset=" + offset +
                ", data=" + data +
                '}';
    }

    public static byte[] trimData(byte[] data, int offset) {
        if (offset == 0) {
            return data;
        } else {
            return Arrays.copyOfRange(data, offset, data.length);
        }
    }

}

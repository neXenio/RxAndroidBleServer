package com.nexenio.rxandroidbleserver.response;

import android.bluetooth.BluetoothGatt;

import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;

public class ServerErrorResponse extends BaseServerResponse {

    public ServerErrorResponse(RxBleClient client, int requestId, int offset) {
        super(client, requestId, BluetoothGatt.GATT_FAILURE, offset, null);
    }

    public ServerErrorResponse(RxBleServerRequest request) {
        this(request.getClient(), request.getId(), request.getOffset());
    }

    @Override
    public String toString() {
        return "ServerErrorResponse{} " + super.toString();
    }

}

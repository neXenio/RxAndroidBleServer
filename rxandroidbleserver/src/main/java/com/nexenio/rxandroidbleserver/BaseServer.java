package com.nexenio.rxandroidbleserver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;

import com.nexenio.rxandroidbleserver.callback.BaseServerCallback;
import com.nexenio.rxandroidbleserver.callback.RxBleServerCallback;
import com.nexenio.rxandroidbleserver.callback.RxBleServerCallbackMediator;
import com.nexenio.rxandroidbleserver.client.BaseClient;
import com.nexenio.rxandroidbleserver.client.RxBleClient;
import com.nexenio.rxandroidbleserver.exception.BluetoothNotAvailableException;
import com.nexenio.rxandroidbleserver.exception.RxBleServerException;
import com.nexenio.rxandroidbleserver.request.RxBleServerRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicReadRequest;
import com.nexenio.rxandroidbleserver.request.characteristic.RxBleCharacteristicWriteRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorReadRequest;
import com.nexenio.rxandroidbleserver.request.descriptor.RxBleDescriptorWriteRequest;
import com.nexenio.rxandroidbleserver.response.RxBleServerResponse;
import com.nexenio.rxandroidbleserver.service.RxBleService;
import com.nexenio.rxandroidbleserver.service.characteristic.RxBleCharacteristic;
import com.nexenio.rxandroidbleserver.service.characteristic.descriptor.RxBleDescriptor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import timber.log.Timber;

public class BaseServer implements RxBleServer, RxBleServerMapper {

    private static final long ADVERTISING_START_TIMEOUT = TimeUnit.SECONDS.toMillis(1);

    private Context context;

    @Nullable
    private BluetoothGattServer bluetoothGattServer;

    @Nullable
    private RxBleServerCallbackMediator serverCallbackMediator;

    @Nullable
    private RxBleServerCallback serverCallback;

    private final Set<RxBleService> services;
    private final Set<RxBleClient> clients;

    private final PublishSubject<RxBleClient> clientPublisher;
    private final PublishSubject<RxBleServerRequest> requestPublisher;
    private final PublishSubject<RxBleServerResponse> responsePublisher;

    public BaseServer(Context context) {
        this.context = context;
        services = new HashSet<>();
        clients = new HashSet<>();
        clientPublisher = PublishSubject.create();
        requestPublisher = PublishSubject.create();
        responsePublisher = PublishSubject.create();

        requestPublisher.flatMapMaybe(this::createResponse)
                .subscribe(responsePublisher);
    }

    @Override
    public Completable provideServices() {
        serverCallback = new BaseServerCallback();

        Completable bindNewServerCallback = bindServerCallback(serverCallback);

        Completable createNewServer = createBluetoothGattServer(serverCallback)
                .doOnSuccess(bluetoothGattServer -> this.bluetoothGattServer = bluetoothGattServer)
                .ignoreElement();

        Completable addServices = Observable.defer(() -> Observable.fromIterable(getServices()))
                .flatMapCompletable(this::addService);

        Completable respondToRequests = responsePublisher
                .flatMapCompletable(this::sendResponse);

        Completable observeClients = clientPublisher
                .doOnNext(client -> {
                    if (client.isConnected()) {
                        Timber.i("Client connected: %s", client);
                    } else if (client.isDisconnected()) {
                        Timber.i("Client disconnected: %s", client);
                    }
                })
                .ignoreElements();

        Completable waitForDisposal = Completable.never()
                .doFinally(() -> {
                    if (bluetoothGattServer != null) {
                        this.bluetoothGattServer.close();
                        this.bluetoothGattServer = null;
                    }
                });

        return bindNewServerCallback
                .andThen(createNewServer)
                .andThen(addServices)
                .andThen(Completable.mergeArray(
                        respondToRequests.subscribeOn(Schedulers.io()),
                        observeClients.subscribeOn(Schedulers.io()),
                        waitForDisposal.subscribeOn(Schedulers.io())
                ))
                .doOnSubscribe(disposable -> Timber.i("Starting to provide %d service(s)", getServices().size()))
                .doFinally(() -> Timber.i("Services providing stopped"));
    }

    @Override
    public Completable provideServicesAndAdvertise(@NonNull UUID uuid) {
        return Completable.mergeArray(
                provideServices().subscribeOn(Schedulers.io()),
                advertise(uuid).subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Completable advertise(@NonNull UUID uuid) {
        return startAdvertising(uuid)
                .timeout(ADVERTISING_START_TIMEOUT, TimeUnit.MILLISECONDS)
                .flatMapCompletable(disposeAction -> Completable.never()
                        .doOnDispose(disposeAction))
                .doOnSubscribe(disposable -> Timber.i("Starting to advertise service: %s", uuid))
                .doFinally(() -> Timber.i("Service advertising stopped"));
    }

    private Single<Action> startAdvertising(@NonNull UUID uuid) {
        return getBluetoothAdvertiser()
                .flatMap(advertiser -> Single.create(emitter -> {

                    // TODO: 1/25/2020 make settings adjustable

                    AdvertiseSettings settings = new AdvertiseSettings.Builder()
                            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                            .setConnectable(true)
                            .setTimeout(0)
                            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                            .build();

                    AdvertiseData data = new AdvertiseData.Builder()
                            .setIncludeDeviceName(false)
                            .setIncludeTxPowerLevel(false)
                            .addServiceUuid(new ParcelUuid(uuid))
                            .build();

                    AdvertiseCallback callback = createAdvertisingCallback(advertiser, emitter);

                    advertiser.startAdvertising(settings, data, callback);
                }));
    }

    private AdvertiseCallback createAdvertisingCallback(@NonNull BluetoothLeAdvertiser advertiser, @NonNull SingleEmitter<Action> disposeActionEmitter) {
        return new AdvertiseCallback() {

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                if (!disposeActionEmitter.isDisposed()) {
                    disposeActionEmitter.onSuccess(createDisposeAction(this));
                }
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                disposeActionEmitter.tryOnError(new RxBleServerException("Unable to start advertising. Error code: " + errorCode));
            }

            private Action createDisposeAction(AdvertiseCallback callback) {
                return () -> advertiser.stopAdvertising(callback);
            }

        };
    }

    @Override
    public Completable addService(@NonNull RxBleService service) {
        Completable modifyGattServer = getBluetoothGattServer()
                .flatMapCompletable(bluetoothGattServer -> Completable.defer(() -> {
                    BluetoothGattService bluetoothGattService = service.getGattService();
                    boolean success = bluetoothGattServer.addService(bluetoothGattService);
                    if (success) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new RxBleServerException("Unable to add GATT service"));
                    }
                }));

        return Completable.defer(() -> {
            if (bluetoothGattServer != null) {
                return modifyGattServer;
            } else {
                return Completable.complete();
            }
        }).doOnComplete(() -> {
            services.add(service);
            Timber.d("Added service: %s", service);
        });
    }

    @Override
    public Completable removeService(@NonNull RxBleService service) {
        Completable modifyGattServer = getBluetoothGattServer()
                .flatMapCompletable(bluetoothGattServer -> Completable.defer(() -> {
                    BluetoothGattService bluetoothGattService = service.getGattService();
                    boolean success = bluetoothGattServer.removeService(bluetoothGattService);
                    if (success) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new RxBleServerException("Unable to remove GATT service"));
                    }
                }));

        return Completable.defer(() -> {
            if (bluetoothGattServer != null) {
                return modifyGattServer;
            } else {
                return Completable.complete();
            }
        }).doOnComplete(() -> {
            services.remove(service);
            Timber.d("Removed service: %s", service);
        });
    }

    @Override
    public Set<RxBleService> getServices() {
        return Collections.unmodifiableSet(services);
    }

    @Override
    public Set<RxBleClient> getClients() {
        return Collections.unmodifiableSet(clients);
    }

    @Override
    public Completable connect(@NonNull RxBleClient client) {
        return Completable.error(new RxBleServerException("Not implemented"));
    }

    @Override
    public Completable disconnect(@NonNull RxBleClient client) {
        return Completable.error(new RxBleServerException("Not implemented"));
    }

    @Override
    public Observable<RxBleClient> observerClientConnectionStateChanges() {
        return clientPublisher;
    }

    @Override
    public Single<RxBleClient> getClient(@NonNull BluetoothDevice bluetoothDevice) {
        return Observable.defer(() -> Observable.fromIterable(getClients()))
                .filter(client -> client.getBluetoothDevice().equals(bluetoothDevice))
                .firstOrError()
                .onErrorResumeWith(createClient(bluetoothDevice)
                        .doOnSuccess(clients::add));
    }

    private Single<RxBleClient> createClient(@NonNull BluetoothDevice bluetoothDevice) {
        return Single.just(new BaseClient(bluetoothDevice));
    }

    @Override
    public Single<RxBleService> getService(@NonNull BluetoothGattService gattService) {
        return Observable.defer(() -> Observable.fromIterable(getServices()))
                .filter(service -> service.getGattService().equals(gattService))
                .firstOrError();
    }

    @Override
    public Single<RxBleCharacteristic> getCharacteristic(@NonNull BluetoothGattCharacteristic gattCharacteristic) {
        return Observable.defer(() -> Observable.fromIterable(getServices()))
                .map(RxBleService::getCharacteristics)
                .flatMap(Observable::fromIterable)
                .filter(service -> service.getGattCharacteristic().equals(gattCharacteristic))
                .firstOrError();
    }

    @Override
    public Single<RxBleDescriptor> getDescriptor(@NonNull BluetoothGattDescriptor gattDescriptor) {
        return Observable.defer(() -> Observable.fromIterable(getServices()))
                .map(RxBleService::getCharacteristics)
                .flatMap(Observable::fromIterable)
                .map(RxBleCharacteristic::getDescriptors)
                .flatMap(Observable::fromIterable)
                .filter(service -> service.getGattDescriptor().equals(gattDescriptor))
                .firstOrError();
    }

    private Single<BluetoothManager> getBluetoothManager() {
        return Single.defer(() -> {
            BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (manager != null) {
                return Single.just(manager);
            } else {
                return Single.error(new BluetoothNotAvailableException("Bluetooth system service not available"));
            }
        });
    }

    private Single<BluetoothGattServer> getBluetoothGattServer() {
        return Single.defer(() -> {
            if (bluetoothGattServer != null) {
                return Single.just(bluetoothGattServer);
            } else {
                return Single.error(new IllegalStateException("GATT server not available. " +
                        "Make sure you have an active subscription to 'provideServices()'"));
            }
        });
    }

    private Single<BluetoothAdapter> getBluetoothAdapter() {
        return getBluetoothManager()
                .map(bluetoothManager -> {
                    BluetoothAdapter adapter = bluetoothManager.getAdapter();
                    if (adapter != null) {
                        return adapter;
                    } else {
                        throw new RxBleServerException("No Bluetooth adapter available");
                    }
                });
    }

    private Single<BluetoothLeAdvertiser> getBluetoothAdvertiser() {
        return getBluetoothAdapter()
                .map(bluetoothAdapter -> {
                    BluetoothLeAdvertiser advertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
                    if (advertiser != null) {
                        return advertiser;
                    } else {
                        throw new RxBleServerException("No Bluetooth LE advertiser available");
                    }
                });
    }

    private Single<BluetoothGattServer> createBluetoothGattServer(@NonNull RxBleServerCallback callback) {
        return getBluetoothManager()
                .map(bluetoothManager -> {
                    serverCallbackMediator = new RxBleServerCallbackMediator(callback, this);

                    BluetoothGattServerCallback androidCallback = serverCallbackMediator.getAndroidCallback();
                    BluetoothGattServer gattServer = bluetoothManager.openGattServer(context, androidCallback);
                    if (gattServer != null) {
                        return gattServer;
                    } else {
                        throw new RxBleServerException("Unable to open GATT server");
                    }
                });
    }

    private Completable bindServerCallback(@NonNull RxBleServerCallback callback) {
        return Completable.fromAction(() -> {
            callback.getClientConnectionStateChangePublisher().subscribe(clientPublisher);
            callback.getCharacteristicReadRequestPublisher().subscribe(requestPublisher);
            callback.getCharacteristicWriteRequestPublisher().subscribe(requestPublisher);
            callback.getDescriptorReadRequestPublisher().subscribe(requestPublisher);
            callback.getDescriptorWriteRequestPublisher().subscribe(requestPublisher);
        });
    }

    private Maybe<RxBleServerResponse> createResponse(@NonNull RxBleServerRequest request) {
        return Maybe.defer(() -> {
            if (request instanceof RxBleCharacteristicReadRequest) {
                return createRequestResponse((RxBleCharacteristicReadRequest) request).toMaybe();
            } else if (request instanceof RxBleCharacteristicWriteRequest) {
                return createRequestResponse((RxBleCharacteristicWriteRequest) request);
            } else if (request instanceof RxBleDescriptorReadRequest) {
                return createRequestResponse((RxBleDescriptorReadRequest) request).toMaybe();
            } else if (request instanceof RxBleDescriptorWriteRequest) {
                return createRequestResponse((RxBleDescriptorWriteRequest) request);
            } else {
                return Maybe.error(new RxBleServerException("Unable to create response for request: " + request));
            }
        }).doOnSubscribe(disposable -> Timber.d("Processing request: %s", request));
    }

    private Single<RxBleServerResponse> createRequestResponse(@NonNull RxBleCharacteristicReadRequest request) {
        return request.getCharacteristic().createReadRequestResponse(request);
    }

    private Maybe<RxBleServerResponse> createRequestResponse(@NonNull RxBleCharacteristicWriteRequest request) {
        return request.getCharacteristic().createWriteRequestResponse(request);
    }

    private Single<RxBleServerResponse> createRequestResponse(@NonNull RxBleDescriptorReadRequest request) {
        return request.getDescriptor().createReadRequestResponse(request);
    }

    private Maybe<RxBleServerResponse> createRequestResponse(@NonNull RxBleDescriptorWriteRequest request) {
        return request.getDescriptor().createWriteRequestResponse(request);
    }

    private Completable sendResponse(RxBleServerResponse response) {
        return Completable.defer(() -> {
            if (bluetoothGattServer == null) {
                return Completable.error(new RxBleServerException("GATT server not available"));
            }

            boolean success = bluetoothGattServer.sendResponse(
                    response.getClient().getBluetoothDevice(),
                    response.getRequestId(),
                    response.getStatus(),
                    response.getOffset(),
                    response.getValue().getBytes()
            );

            if (success) {
                return Completable.complete();
            } else {
                return Completable.error(new RxBleServerException("Unable to send GATT response: " + response));
            }
        }).doOnSubscribe(disposable -> Timber.d("Sending response: %s", response));
    }

}

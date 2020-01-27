package com.nexenio.rxandroidbleserver;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ToggleButton provideServicesToggleButton;
    private ToggleButton advertiseServicesToggleButton;

    private RxBleServer bleServer;
    private Disposable provideServicesDisposable;
    private Disposable advertiseServicesDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        provideServicesToggleButton = findViewById(R.id.provideServicesToggleButton);
        provideServicesToggleButton.setOnClickListener(v -> toggleProvidingServices());
        advertiseServicesToggleButton = findViewById(R.id.advertiseServicesToggleButton);
        advertiseServicesToggleButton.setOnClickListener(v -> toggleAdvertisingServices());

        bleServer = ExampleProfile.createExampleServer(this);
        bleServer.observerClientConnectionStateChanges()
                .subscribe(
                        client -> Log.d(TAG, "Client state changed: " + client)
                );
    }

    @Override
    protected void onStop() {
        stopProvidingServices();
        stopAdvertisingServices();
        super.onStop();
    }

    /*
        Providing services
     */

    private void startProvidingServices() {
        provideServicesDisposable = bleServer.provideServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::onServiceProvidingStarted)
                .doFinally(this::onServiceProvidingStopped)
                .subscribe(
                        () -> Log.i(TAG, "Stopped providing services"),
                        this::onServiceProvidingError
                );
    }

    private void stopProvidingServices() {
        if (provideServicesDisposable != null && !provideServicesDisposable.isDisposed()) {
            provideServicesDisposable.dispose();
        }
    }

    private void toggleProvidingServices() {
        if (provideServicesToggleButton.isChecked()) {
            startProvidingServices();
        } else {
            stopProvidingServices();
        }
    }

    private void onServiceProvidingStarted(Disposable disposable) {
        Log.d(TAG, "Service providing started");
        //showTemporaryMessage(getString(R.string.status_service_providing_started));
        provideServicesToggleButton.setChecked(true);
    }

    private void onServiceProvidingStopped() {
        Log.d(TAG, "Service providing stopped");
        //showTemporaryMessage(getString(R.string.status_service_providing_stopped));
        provideServicesToggleButton.setChecked(false);
    }

    private void onServiceProvidingError(Throwable throwable) {
        Log.w(TAG, "Unable to provide services", throwable);
        showTemporaryMessage(throwable);
    }

    /*
        Advertising services
     */

    private void startAdvertisingServices() {
        UUID uuid = ExampleProfile.EXAMPLE_SERVICE_UUID;
        advertiseServicesDisposable = bleServer.advertiseService(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(this::onServiceAdvertisingStarted)
                .doFinally(this::onServiceAdvertisingStopped)
                .subscribe(
                        () -> Log.i(TAG, "Stopped advertising services"),
                        this::onServiceAdvertisingError
                );
    }

    private void stopAdvertisingServices() {
        if (advertiseServicesDisposable != null && !advertiseServicesDisposable.isDisposed()) {
            advertiseServicesDisposable.dispose();
        }
    }

    private void toggleAdvertisingServices() {
        if (advertiseServicesToggleButton.isChecked()) {
            startAdvertisingServices();
        } else {
            stopAdvertisingServices();
        }
    }

    private void onServiceAdvertisingStarted(Disposable disposable) {
        Log.d(TAG, "Service advertising started");
        advertiseServicesToggleButton.setChecked(true);
    }

    private void onServiceAdvertisingStopped() {
        Log.d(TAG, "Service advertising stopped");
        advertiseServicesToggleButton.setChecked(false);
    }

    private void onServiceAdvertisingError(Throwable throwable) {
        Log.w(TAG, "Unable to advertise services", throwable);
        showTemporaryMessage(throwable);
    }

    /*
        Utilities
     */

    private void showTemporaryMessage(@NonNull Throwable throwable) {
        if (throwable.getMessage() != null) {
            showTemporaryMessage(throwable.getMessage());
        } else {
            showTemporaryMessage(throwable.getClass().getSimpleName());
        }
    }

    private void showTemporaryMessage(@NonNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}

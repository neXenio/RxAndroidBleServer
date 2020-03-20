package com.nexenio.rxandroidbleserverapp;

import com.google.android.material.snackbar.Snackbar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tbruyelle.rxpermissions3.RxPermissions;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import timber.log.Timber;

public class ExampleActivity extends AppCompatActivity {

    private static final String TAG = ExampleActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    private ExampleViewModel viewModel;
    private RxPermissions rxPermissions;

    private ConstraintLayout constraintLayout;
    private ToggleButton provideServicesToggleButton;
    private ToggleButton advertiseServicesToggleButton;
    private Snackbar errorSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        rxPermissions = new RxPermissions(this);
        viewModel = new ViewModelProvider(this).get(ExampleViewModel.class);

        constraintLayout = findViewById(R.id.coordinatorLayout);
        provideServicesToggleButton = findViewById(R.id.provideServicesToggleButton);
        provideServicesToggleButton.setOnClickListener(v -> viewModel.toggleProvidingServices());
        advertiseServicesToggleButton = findViewById(R.id.advertiseServicesToggleButton);
        advertiseServicesToggleButton.setOnClickListener(v -> viewModel.toggleAdvertisingServices());

        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_unknown, Snackbar.LENGTH_SHORT);

        viewModel.isProvidingServices().observe(this, isProvidingService -> {
            if (isProvidingService) {
                onServiceProvidingStarted();
            } else {
                onServiceProvidingStopped();
            }
        });

        viewModel.isAdvertisingService().observe(this, isAdvertisingService -> {
            if (isAdvertisingService) {
                onServiceAdvertisingStarted();
            } else {
                onServiceAdvertisingStopped();
            }
        });

        viewModel.getErrors().observe(this, throwable -> {
            showTemporaryMessage(throwable);
            performTroubleshooting(throwable);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Flowable.timer(1, TimeUnit.SECONDS)
                .ignoreElements()
                .andThen(Completable.fromAction(this::checkPermissions))
                .subscribe();
    }

    /*
        Server observation callbacks
     */

    private void onServiceProvidingStarted() {
        provideServicesToggleButton.setChecked(true);
    }

    private void onServiceProvidingStopped() {
        provideServicesToggleButton.setChecked(false);
    }

    private void onServiceAdvertisingStarted() {
        advertiseServicesToggleButton.setChecked(true);
    }

    private void onServiceAdvertisingStopped() {
        advertiseServicesToggleButton.setChecked(false);
    }

    /*
        Permissions
     */

    private void checkPermissions() {
        int bluetoothPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH);
        int bluetoothAdminPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN);
        if (bluetoothPermission != PackageManager.PERMISSION_GRANTED || bluetoothAdminPermission != PackageManager.PERMISSION_GRANTED) {
            showMissingPermissionError();
        }
    }

    private void showMissingPermissionError() {
        errorSnackbar.dismiss();
        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_missing_permissions, Snackbar.LENGTH_INDEFINITE);
        errorSnackbar.setAction(R.string.action_grant_permission, v -> requestMissingPermissions());
        errorSnackbar.show();
    }

    @SuppressLint("CheckResult")
    private void requestMissingPermissions() {
        rxPermissions.request(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
                .subscribe(permissionsGranted -> {
                    if (!permissionsGranted) {
                        showMissingPermissionError();
                    }
                });
    }

    /*
        Bluetooth
     */

    private void checkBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            showBluetoothDisabledError();
        }
    }

    private void showBluetoothDisabledError() {
        errorSnackbar.dismiss();
        errorSnackbar = Snackbar.make(constraintLayout, R.string.error_bluetooth_disabled, Snackbar.LENGTH_INDEFINITE);
        errorSnackbar.setAction(R.string.action_enable, v -> enableBluetooth());
        errorSnackbar.show();
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH);
    }

    /*
        Utilities
     */

    private void performTroubleshooting(@NonNull Throwable throwable) {
        checkPermissions();
        checkBluetoothEnabled();
    }

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

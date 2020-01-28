package com.nexenio.rxandroidbleserverapp;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ExampleActivity extends AppCompatActivity {

    private static final String TAG = ExampleActivity.class.getSimpleName();

    private ExampleViewModel viewModel;

    private ToggleButton provideServicesToggleButton;
    private ToggleButton advertiseServicesToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        viewModel = new ViewModelProvider(this).get(ExampleViewModel.class);

        provideServicesToggleButton = findViewById(R.id.provideServicesToggleButton);
        provideServicesToggleButton.setOnClickListener(v -> viewModel.toggleProvidingServices());
        advertiseServicesToggleButton = findViewById(R.id.advertiseServicesToggleButton);
        advertiseServicesToggleButton.setOnClickListener(v -> viewModel.toggleAdvertisingServices());

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

        viewModel.getErrors().observe(this, this::showTemporaryMessage);
    }

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

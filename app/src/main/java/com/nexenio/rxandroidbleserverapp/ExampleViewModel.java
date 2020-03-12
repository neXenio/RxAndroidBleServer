package com.nexenio.rxandroidbleserverapp;

import android.app.Application;
import android.util.Log;

import com.nexenio.rxandroidbleserver.RxBleServer;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExampleViewModel extends AndroidViewModel {

    private final String TAG = ExampleViewModel.class.getSimpleName();

    private final CompositeDisposable viewModelDisposable = new CompositeDisposable();

    private RxBleServer bleServer;
    private Disposable provideServicesDisposable;
    private Disposable advertiseServicesDisposable;

    private MutableLiveData<Boolean> isProvidingServices = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdvertisingService = new MutableLiveData<>();
    private MutableLiveData<Throwable> errors = new MutableLiveData<>();

    public ExampleViewModel(@NonNull Application application) {
        super(application);

        bleServer = ExampleProfile.createExampleServer(application);
        isProvidingServices.setValue(false);
        isAdvertisingService.setValue(false);
    }

    @Override
    protected void onCleared() {
        viewModelDisposable.dispose();
        super.onCleared();
    }

    /*
        Providing services
     */

    public LiveData<Boolean> isProvidingServices() {
        return isProvidingServices;
    }

    public void toggleProvidingServices() {
        if (!isProvidingServices.getValue()) {
            startProvidingServices();
        } else {
            stopProvidingServices();
        }
    }

    private void startProvidingServices() {
        Log.d(TAG, "Starting to provide services");
        provideServicesDisposable = bleServer.provideServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isProvidingServices.postValue(true))
                .doFinally(() -> isProvidingServices.postValue(false))
                .subscribe(
                        () -> Log.i(TAG, "Stopped providing services"),
                        this::postError
                );

        viewModelDisposable.add(provideServicesDisposable);
    }

    private void stopProvidingServices() {
        Log.d(TAG, "Stopping to provide services");
        if (provideServicesDisposable != null && !provideServicesDisposable.isDisposed()) {
            provideServicesDisposable.dispose();
        }
    }

    /*
        Advertising services
     */

    public LiveData<Boolean> isAdvertisingService() {
        return isAdvertisingService;
    }

    public void toggleAdvertisingServices() {
        if (!isAdvertisingService.getValue()) {
            startAdvertisingServices();
        } else {
            stopAdvertisingServices();
        }
    }

    private void startAdvertisingServices() {
        Log.d(TAG, "Starting to advertise services");
        UUID uuid = ExampleProfile.EXAMPLE_SERVICE_UUID;
        advertiseServicesDisposable = bleServer.advertise(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isAdvertisingService.postValue(true))
                .doFinally(() -> isAdvertisingService.postValue(false))
                .subscribe(
                        () -> Log.i(TAG, "Stopped advertising services"),
                        this::postError
                );

        viewModelDisposable.add(advertiseServicesDisposable);
    }

    private void stopAdvertisingServices() {
        Log.d(TAG, "Stopping to advertise services");
        if (advertiseServicesDisposable != null && !advertiseServicesDisposable.isDisposed()) {
            advertiseServicesDisposable.dispose();
        }
    }

    /*
        Errors
     */

    private void postError(@NonNull Throwable throwable) {
        Log.w(TAG, throwable);
        errors.postValue(throwable);
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

}

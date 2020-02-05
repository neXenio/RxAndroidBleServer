package com.nexenio.rxandroidbleserverapp;

import android.app.Application;

import com.nexenio.rxandroidbleserver.RxBleServer;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ExampleViewModel extends AndroidViewModel {

    private final CompositeDisposable viewModelDisposable = new CompositeDisposable();

    private final ExampleProfile exampleProfile;
    private RxBleServer bleServer;
    private Disposable provideServicesDisposable;
    private Disposable advertiseServicesDisposable;
    private Disposable updateValueDisposable;

    private MutableLiveData<Boolean> isProvidingServices = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdvertisingService = new MutableLiveData<>();
    private MutableLiveData<Throwable> errors = new MutableLiveData<>();

    public ExampleViewModel(@NonNull Application application) {
        super(application);

        exampleProfile = new ExampleProfile(application);
        bleServer = exampleProfile.getExampleServer();

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
        Timber.d("Starting to provide services");
        provideServicesDisposable = bleServer.provideServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isProvidingServices.postValue(true))
                .doFinally(() -> isProvidingServices.postValue(false))
                .subscribe(
                        () -> Timber.i("Stopped providing services"),
                        this::postError
                );

        updateValueDisposable = exampleProfile.updateCharacteristicValues()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> Timber.d("Done updating characteristic values"),
                        this::postError
                );

        viewModelDisposable.add(provideServicesDisposable);
        viewModelDisposable.add(updateValueDisposable);
    }

    private void stopProvidingServices() {
        Timber.d("Stopping to provide services");
        if (provideServicesDisposable != null && !provideServicesDisposable.isDisposed()) {
            provideServicesDisposable.dispose();
        }
        if (updateValueDisposable != null && !updateValueDisposable.isDisposed()) {
            updateValueDisposable.dispose();
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
        Timber.d("Starting to advertise services");
        UUID uuid = ExampleProfile.EXAMPLE_SERVICE_UUID;
        advertiseServicesDisposable = bleServer.advertise(uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> isAdvertisingService.postValue(true))
                .doFinally(() -> isAdvertisingService.postValue(false))
                .subscribe(
                        () -> Timber.i("Stopped advertising services"),
                        this::postError
                );

        viewModelDisposable.add(advertiseServicesDisposable);
    }

    private void stopAdvertisingServices() {
        Timber.d("Stopping to advertise services");
        if (advertiseServicesDisposable != null && !advertiseServicesDisposable.isDisposed()) {
            advertiseServicesDisposable.dispose();
        }
    }

    /*
        Errors
     */

    private void postError(@NonNull Throwable throwable) {
        Timber.w(throwable);
        errors.postValue(throwable);
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    }

}

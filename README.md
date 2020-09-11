[![Travis](https://img.shields.io/travis/neXenio/RxAndroidBleServer/master.svg)](https://travis-ci.org/neXenio/RxAndroidBleServer/builds) [![GitHub release](https://img.shields.io/github/release/neXenio/RxAndroidBleServer.svg)](https://github.com/neXenio/RxAndroidBleServer/releases) [![JitPack](https://img.shields.io/jitpack/v/neXenio/RxAndroidBleServer.svg)](https://jitpack.io/#neXenio/RxAndroidBleServer/) [![Codecov](https://img.shields.io/codecov/c/github/nexenio/RxAndroidBleServer.svg)](https://codecov.io/gh/neXenio/RxAndroidBleServer) [![license](https://img.shields.io/github/license/neXenio/RxAndroidBleServer.svg)](https://github.com/neXenio/RxAndroidBleServer/blob/master/LICENSE)

# RxAndroidBleServer

An Android Bluetooth Low Energy GATT Server Library with RxJava3 interface.

## Usage

### Integration

You can get the latest artifacts from [JitPack][jitpack]:

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.neXenio:RxAndroidBleServer:master-SNAPSHOT'
}
```

Replace `master-SNAPSHOT` with the a release version number or a commit hash to get reproducable builds.

### Overview

This library provides all commonly used Bluetooth GATT server functionality through [RxJava][rxjava] powered interfaces. There's a default implementation available for each of those interfaces, so that you don't have to manually deal with all the request and response handling that is usually required when providing Android's `BluetoothGattServerCallback`.

It also introduces a few wrappers to make it more convenient to work with the BLE related stuff, for instance:

- `RxBleValue` instead of raw `byte[]` arrays with offsets
- `RxBleServerRequest` and `RxBleServerResponse` instead of the overloaded callback parameters
	- Further extended for characteristic and descriptor reads and writes

### GATT Server

The `RxBleServer` interface provides the functionality of Android's `BluetoothGattServer`. It's capable of providing and advertising services as well as connecting and disconnecting clients.

To get an instance of the default implementation, use the `ServerBuilder`.

You can customize the default implementation by extending `BaseServer`.

#### Providing & Advertising

To actually open the server and make it visible to remote clients, there are two methods that you should subscribe to: `provideServices()` and `advertise(UUID)`.

You can provide a GATT server without advertising any service UUID. You can also advertise a service UUID that you don't actually provide a GATT server for. While most of the time you might want to do both, that may not always be the case. You could, for instance, stop advertising while a given client is connected. For convenience, there's also a merged version: `provideServicesAndAdvertise(UUID)`.

These methods will return a `Completable` that never terminates (unless an error accurs, e.g. when Bluetooth is disabled). You need to manually dispose the `Completable` subscriptions to stop the server (e.g. when your application lifecycle comes to an end).

### GATT Services

The `RxBleService` interface provides the functionality of Android's `BluetoothGattService`. It's basically just a container for one ore more characteristics.

To get an instance of the default implementation, use the `ServiceBuilder`:

```java
RxBleService service = new ServiceBuilder(EXAMPLE_SERVICE_UUID)
        .withCharacteristic(createFirstCharacteristic())
        .withCharacteristic(createSecondCharacteristic())
        .isPrimaryService()
        .build();
```

You can customize the default implementation by extending `BaseService`.

### GATT Characteristics

The `RxBleCharacteristic` interface provides the functionality of Android's `BluetoothGattCharacteristic`. It can hold a value, zero or more descriptors, permissions and properties.

To get an instance of the default implementation, use the `CharacteristicBuilder`:

```java
RxBleCharacteristic characteristic = new CharacteristicBuilder(EXAMPLE_CHARACTERISTIC_UUID)
        .withInitialValue(DEFAUL_CHARACTERISTIC_VALUE)
        .withDescriptor(createFirstDescriptor())
        .withDescriptor(createSecondDescriptor())
        .allowRead()
        .allowWrite()
        .supportWritesWithoutResponse()
        .supportNotifications()
        .build();
```

You can customize the default implementation by extending `BaseCharacteristic`.

### GATT Descriptors

The `RxBleDescriptor` interface provides the functionality of Android's `BluetoothGattDescriptor`. It can hold a value and permissions.

To get an instance of the default implementation, use the `DescriptorBuilder`.

```java
RxBleDescriptor descriptor = new DescriptorBuilder(EXAMPLE_DESCRIPTOR_UUID)
        .withInitialValue(DEFAUL_DESCRIPTOR_VALUE)
        .allowRead()
        .build();
```

Some common descriptors might already be avialble pre-configured, such as the `CharacteristicUserDescription`:

```java
RxBleDescriptor descriptor = new CharacteristicUserDescription("Example characteristic")
```

You can customize the default implementation by extending `BaseDescriptor`.

### Value Providers

Both `RxBleCharacteristic` and `RxBleDescriptor` extend the `RxBleSharedValueProvider` as well as the `RxBleClientValueProvider` interface. That means they have methods to get or set values, and to observe value changes (e.g. if set by a write request from a remote client).

While a `RxBleSharedValueProvider` provides one single value for all clients, the `RxBleClientValueProvider` can provide a dedicated value for each client. Both might come in handy, depending on your usecase.

```java
// valueProvider may be a characteristic or descriptor
valueProvider.getValueChanges()
        .subscribe(value -> Log.d(TAG, "Shared value changed: " + value));

// someClientOfInterest may be a client that we care about
valueProvider.getValueChanges(someClientOfInterest)
        .subscribe(value -> Log.d(TAG, "Interesting client's value changed: " + value));
```

Note that the difference between these two is not just a `.filter()` by device, different clients can actually read and write different values on the same `RxBleClientValueProvider`.

### Clients

The `RxBleClient` interface provides the functionality of Android's `BluetoothDevice`. It represents a remote device that connected to the server. Client instances will be automatically created by the library for your once they start connecting.

You can observe clients (and their connection states) using the server instance:

```java
server.observerClientConnectionStateChanges()
		.subscribe(client -> Log.d(TAG, "Client state updated: " + client));
```

Client instances can also be used to manually trigger a connect or disconnect:

```java
server.connect(someClientOfInterest)
        .subscribe(
                () -> Log.d(TAG, "Client connected: " + client),
                throwable -> Log.w(TAG, "Unable to connect client: " + client, throwable)
        );
```

[releases]: https://github.com/neXenio/RxAndroidBleServer/releases
[jitpack]: https://jitpack.io/#neXenio/RxAndroidBleServer/
[rxjava]: https://github.com/ReactiveX/RxJava

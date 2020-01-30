[![Travis](https://img.shields.io/travis/neXenio/RxAndroidBleServer/master.svg)](https://travis-ci.org/neXenio/RxAndroidBleServer/builds) [![GitHub release](https://img.shields.io/github/release/neXenio/RxAndroidBleServer.svg)](https://github.com/neXenio/RxAndroidBleServer/releases) [![JitPack](https://img.shields.io/jitpack/v/neXenio/RxAndroidBleServer.svg)](https://jitpack.io/#neXenio/RxAndroidBleServer/) [![Codecov](https://img.shields.io/codecov/c/github/nexenio/RxAndroidBleServer.svg)](https://codecov.io/gh/neXenio/RxAndroidBleServer) [![license](https://img.shields.io/github/license/neXenio/RxAndroidBleServer.svg)](https://github.com/neXenio/RxAndroidBleServer/blob/master/LICENSE)

# RxAndroidBleServer

An Android Bluetooth Low Energy GATT Server Library with RxJava2 interface.

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
    implementation 'com.github.neXenio:RxKeyStore:master-SNAPSHOT'
}
```

Replace `master-SNAPSHOT` with the latest release version number to get reproducable builds.


[releases]: https://github.com/neXenio/RxAndroidBleServer/releases
[jitpack]: https://jitpack.io/#neXenio/RxAndroidBleServer/
[rxjava]: https://github.com/ReactiveX/RxJava
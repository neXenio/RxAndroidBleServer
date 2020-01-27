[![Travis](https://img.shields.io/travis/neXenio/RxKeyStore/master.svg)](https://travis-ci.org/neXenio/RxKeyStore/builds) [![GitHub release](https://img.shields.io/github/release/neXenio/RxKeyStore.svg)](https://github.com/neXenio/RxKeyStore/releases) [![JitPack](https://img.shields.io/jitpack/v/neXenio/RxKeyStore.svg)](https://jitpack.io/#neXenio/RxKeyStore/) [![Codecov](https://img.shields.io/codecov/c/github/nexenio/RxKeyStore.svg)](https://codecov.io/gh/neXenio/RxKeyStore) [![license](https://img.shields.io/github/license/neXenio/RxKeyStore.svg)](https://github.com/neXenio/RxKeyStore/blob/master/LICENSE)

# RxKeyStore

This library provides an [RxJava][rxjava] wrapper for the [Android Keystore][androidkeystoretraining], as well as utilities to use it for cryptographic operations.

## Features

- CRUD for keys and certificates
- Symmetric cryptography (AES)
    - Generate secret keys
    - Encrypt & Decrypt
- Asymmetric cryptography (RSA | EC)
    - Generate key pairs
    - Encrypt & Decrypt
    - Sign & Verify

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
    implementation 'com.github.neXenio:RxKeyStore:dev-SNAPSHOT'
}
```

### Overview

The entrypoint of this library is [RxKeyStore][rxkeystore]. It provides the functionality of the [Android keystore][keystore] in a reactive fashion.

To make use of it, the library also provides the [RxCryptoProvider][rxcryptoprovider] interface, further extended in [RxSymmetricCryptoProvider][rxsymmetriccryptoprovider] and [RxAsymmetricCryptoProvider][rxasymmetriccryptoprovider]. You can implement them suiting your needs or use default implementations for [AES][rxaescryptoprovider], [RSA][rxrsacryptoprovider] or [EC][rxeccryptoprovider].

For usage samples, check out the [instrumentation tests][connectedtests].

### Get a keystore

To get an `RxKeyStore` instance, use the default constructor. You can also specify the keystore type, in case you want to use a custom one.
```java
defaultKeyStore = new RxKeyStore(); // will use the default android keystore
customKeyStore = new RxKeyStore(RxKeyStore.TYPE_BOUNCY_CASTLE);
```

The actual `KeyStore` from the Android framework will be initialized lazily once its needed. You can also directly access it using `getLoadedKeyStore()`.

You can use the `RxKeyStore` instance to get or delete entries and to initialize an `RxCryptoProvider`.

### Get a crypto provider

An `RxCryptoProvider` is in charge of generating keys and using them for cryptographic operations. You can use default implementations for [AES][rxaescryptoprovider], [RSA][rxrsacryptoprovider] or [EC][rxeccryptoprovider]. You can also create your own by implementing `RxSymmetricCryptoProvider` or `RxAsymmetricCryptoProvider`.

```java
cryptoProvider = new RxRSACryptoProvider(keyStore);
```

### Generate keys

```java
cryptoProvider.generateKeyPair("my_fancy_keypair", context)
        .subscribe(keyPair -> {
            PublicKey publicKey = keyPair.getPublic();
            // transfer public key to second party
        });
```

### Encrypt data

```java
byte[] unencryptedBytes = ...;

cryptoProvider.getKeyPair("my_fancy_keypair")
        .flatMap(keyPair -> cryptoProvider.encrypt(
                unencryptedBytes,
                keyPair.getPublic()
        ))
        .subscribe(encryptedBytesAndIV -> {
            byte[] encryptedBytes = encryptedBytesAndIV.first;
            byte[] initializationVector = encryptedBytesAndIV.second;
            // transfer encrypted data and IV
        });
```

### Decrypt data

```java
byte[] encryptedBytes = ...;
byte[] initializationVector = ...;

cryptoProvider.getKeyPair("my_fancy_keypair")
        .flatMap(keyPair -> cryptoProvider.decrypt(
                encryptedBytes,
                initializationVector,
                keyPair.getPrivate()
        ))
        .subscribe(decryptedBytes -> {
            // process decrypted data
        });
```

### Create a signature

```java
byte[] data = ...;

cryptoProvider.getKeyPair("my_fancy_keypair")
        .flatMap(keyPair -> cryptoProvider.sign(
                data,
                keyPair.getPrivate()
        ))
        .subscribe(signature -> {
            // transfer signature
        });
```

### Verify a signature

```java
byte[] signature = ...;
byte[] data = ...;

cryptoProvider.getKeyPair("my_fancy_keypair")
        .flatMapCompletable(keyPair -> cryptoProvider.verify(
                data,
                signature,
                keyPair.getPublic()
        ))
        .subscribe(() -> {
            // signature is valid
        }, throwable -> {
            // signature is invalid
        });
```

If you don't want to treat invalid signatures as an error, you can also use `getVerificationResult` instead of `verify`, which will emit a `boolean` that you can check.

[releases]: https://github.com/neXenio/RxKeyStore/releases
[jitpack]: https://jitpack.io/#neXenio/RxKeyStore/
[rxjava]: https://github.com/ReactiveX/RxJava
[androidkeystoretraining]: https://developer.android.com/training/articles/keystore
[keystore]: https://developer.android.com/reference/java/security/KeyStore.html
[rxkeystore]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/RxKeyStore.java
[rxcryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/RxCryptoProvider.java
[rxsymmetriccryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/symmetric/RxSymmetricCryptoProvider.java
[rxasymmetriccryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/asymmetric/RxAsymmetricCryptoProvider.java
[rxaescryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/symmetric/aes/RxAESCryptoProvider.java
[rxrsacryptoprovider]: https://github.com/neXenio/RxKeyStore/blob/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/asymmetric/rsa/RxRSACryptoProvider.java
[rxeccryptoprovider]: https://github.com/neXenio/RxKeyStore/tree/master/rxkeystore/src/main/java/com/nexenio/rxkeystore/provider/asymmetric
[connectedtests]: https://github.com/neXenio/RxKeyStore/tree/master/rxkeystore/src/androidTest/java/com/nexenio/rxkeystore

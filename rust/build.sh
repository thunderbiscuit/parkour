#!/bin/bash

# Set default Rust compiler
rustup default 1.84.1

PATH="$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/darwin-x86_64/bin:$PATH"
CFLAGS="-D__ANDROID_MIN_SDK_VERSION__=24"
AR="llvm-ar"
LIB_NAME="libark.so"
COMPILATION_TARGET_ARM64_V8A="aarch64-linux-android"
# COMPILATION_TARGET_X86_64="x86_64-linux-android"
# COMPILATION_TARGET_ARMEABI_V7A="armv7-linux-androideabi"
RESOURCE_DIR_ARM64_V8A="arm64-v8a"
# RESOURCE_DIR_X86_64="x86_64"
# RESOURCE_DIR_ARMEABI_V7A="armeabi-v7a"

# Build the Rust library
rustup target add $COMPILATION_TARGET_ARM64_V8A
CC="aarch64-linux-android24-clang" CARGO_TARGET_AARCH64_LINUX_ANDROID_LINKER="aarch64-linux-android24-clang" cargo build --release --target $COMPILATION_TARGET_ARM64_V8A

# Generate Kotlin bindings using uniffi-bindgen
cargo run --bin uniffi-bindgen generate --library ./target/$COMPILATION_TARGET_ARM64_V8A/release/$LIB_NAME --language kotlin --out-dir ../app/src/main/java/ --no-format

# Copy the binary to the Android resources directory
mkdir -p ../app/src/main/jniLibs/$RESOURCE_DIR_ARM64_V8A/
cp ./target/$COMPILATION_TARGET_ARM64_V8A/release/$LIB_NAME ../app/src/main/jniLibs/$RESOURCE_DIR_ARM64_V8A/

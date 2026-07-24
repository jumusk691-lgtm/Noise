cmake_minimum_required(VERSION 3.22.1)
project("noise")

# Oboe package ko import karein (jo Prefab ke through link hoga)
find_package(oboe REQUIRED CONFIG)

add_library(
    noise_engine
    SHARED
    native-lib.cpp
)

target_link_libraries(
    noise_engine
    oboe::oboe
    android
    log
)

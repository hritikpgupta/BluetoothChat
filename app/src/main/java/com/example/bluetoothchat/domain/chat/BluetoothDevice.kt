package com.example.bluetoothchat.domain.chat


typealias BluetoothDeviceDomain = BluetoothDevice
data class BluetoothDevice(
    val name: String?,
    val address: String,
    val isConnected: Boolean = false
)

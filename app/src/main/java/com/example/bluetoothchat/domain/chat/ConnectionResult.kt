package com.example.bluetoothchat.domain.chat

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult


    data class Error(val message: String) : ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage) : ConnectionResult
}
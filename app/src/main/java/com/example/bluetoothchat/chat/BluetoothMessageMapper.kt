package com.example.bluetoothchat.chat

import com.example.bluetoothchat.domain.chat.BluetoothMessage

fun BluetoothMessage.toByteArray() : ByteArray {
    return "$senderName£$message".encodeToByteArray()
}

fun String.toBluetoothMessage(isFromLocalUser: Boolean) : BluetoothMessage {
    val name = substringBeforeLast("£")
    val message = substringAfter("£")
    return BluetoothMessage(message,name,isFromLocalUser)
}
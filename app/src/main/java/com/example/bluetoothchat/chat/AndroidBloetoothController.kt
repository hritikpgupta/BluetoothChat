package com.example.bluetoothchat.chat

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import com.example.bluetoothchat.domain.chat.BluetoothController
import com.example.bluetoothchat.domain.chat.BluetoothDevice
import com.example.bluetoothchat.domain.chat.BluetoothDeviceDomain
import com.example.bluetoothchat.domain.chat.FoundDeviceReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@SuppressLint("MissingPermission")
class AndroidBluetoothController(
    private val context : Context
) : BluetoothController {

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())

    override val scannedDevices: StateFlow<List<BluetoothDevice>>
        get() = _scannedDevices.asStateFlow()

    override val pairedDevices: StateFlow<List<BluetoothDevice>>
        get() = _pairedDevices.asStateFlow()

    private val foundDeviceReceiver = FoundDeviceReceiver{ device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices +newDevice
        }
    }

    init {
        updatePairedDevices()
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){return}
        context.registerReceiver(foundDeviceReceiver, IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND))
        updatePairedDevices()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){return}

        bluetoothAdapter?.cancelDiscovery()
    }

    override fun release() {
        context.unregisterReceiver(foundDeviceReceiver)
    }

    private fun updatePairedDevices(){
        if (!hasPermission(android.Manifest.permission.BLUETOOTH_CONNECT)){ return}

        bluetoothAdapter?.bondedDevices?.map {
            it.toBluetoothDeviceDomain()
        }.also {_pairedDevices.update { it } }
    }

    private fun hasPermission(permission : String) : Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }
}
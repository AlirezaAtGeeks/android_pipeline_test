(function () {
    "use strict";
	 document.addEventListener('DOMContentLoaded', function () {
		 var pairedDevices = document.querySelector('#pairedDevices');
		 pairedDevices.addEventListener('click', function () {
			console.log("PairedDevices button is clicked");
			var result = window.androidInjectSetting.getBluetoothPairedDevices();
			alert("Bluetooth Paired Devices: \n" + result);
		 });
		 
		 var bluetoothTurnOff = document.querySelector('#bluetoothTurnOff');
		 bluetoothTurnOff.addEventListener('click', function () {
			console.log("Bluetooth turn off button is clicked");
			window.androidInjectSetting.turnOffBluetooth();
			alert("Bluetooth is turned off");
		 });
		 
		 var nearbyDevices = document.querySelector('#nearbyDevices');
		 nearbyDevices.addEventListener('click', function () {
			console.log("Bluetooth NearbyDevices button is clicked");
			var result = window.androidInjectSetting.getBluetoothNearbyDevices();
			alert("Bluetooth Neaby Devices: \n" + result);
		 });
		 
		 var bluetoothPairDevice = document.querySelector('#bluetoothPairDevice');
		 bluetoothPairDevice.addEventListener('click', function () {
			console.log("Pair bluetooth device button is clicked");
			var sel = document.getElementById('pairOptions');
			window.androidInjectSetting.pairBluetoothDevice(sel.value);
		 });
		 
		 var bluetoothUnpairDevice = document.querySelector('#bluetoothUnpairDevice');
		 bluetoothUnpairDevice.addEventListener('click', function () {
			console.log("Unpair bluetooth device button is clicked");
			var sel = document.getElementById('pairOptions');
			window.androidInjectSetting.unpairBluetoothDevice(sel.value);
		 });
		 
		 var bluetoothIsConnected = document.querySelector('#bluetoothIsConnected');
		 bluetoothIsConnected.addEventListener('click', function () {
			console.log("bluetooth is connected button is clicked");
			var result = window.androidInjectSetting.isBluetooth

			PrinterConnected();
			if(result)
				alert("Bluetooth is connected");
			else
				alert("Bluetooth is not connected");
		 });
	 });
})();
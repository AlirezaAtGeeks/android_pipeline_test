(function () {
    "use strict";
	 document.addEventListener('DOMContentLoaded', function () {
		 var wifi = document.querySelector('#wifi');
		 wifi.addEventListener('click', function () {
			console.log("Wifi button is clicked");
			var list = window.androidInjectSetting.getWifiNetworks();
			console.log(list);
			alert(list);	
		 });
		 
		 var wifiAsync = document.querySelector('#wifiAsync');
		 wifiAsync.addEventListener('click', function () {
			console.log("Wifi Async button is clicked");
			var list = window.androidInjectSetting.getWifiNetworksAsync();
			list.then = function(callback){
				var name = "callback_"+Math.floor((Math.random() * 100000));
				window[name] = callback
				return list.thenJS(name);
			}
			list.then(function(a){
				console.log(a);
				alert(a);	
			});
			
		 });
		 
		 var wifiturnoff = document.querySelector('#wifiTurnoff');
		 wifiturnoff.addEventListener('click', function () {
			console.log("Wifi turnoff button is clicked");
			window.androidInjectSetting.turnOffWifi();
		 });
		 
		 var wifiDisconnect = document.querySelector('#wifiDisconnect');
		 wifiDisconnect.addEventListener('click', function () {
			console.log("disconnectWifi button is clicked");
			window.androidInjectSetting.disconnectWifi();
		 });
		 
		 var wifiIsConnected = document.querySelector('#wifiIsConnected');
		 wifiIsConnected.addEventListener('click', function () {
			console.log("wifiIsConnected button is clicked");
			alert("Is wifi connected: " + window.androidInjectSetting.isWifiConnected());
		 });
		 
		 var wifiIsEnabled = document.querySelector('#wifiIsEnabled');
		 wifiIsEnabled.addEventListener('click', function () {
			console.log("wifiIsEnabled button is clicked");
			alert("Is wifi enabled: " + window.androidInjectSetting.isWifiEnabled());
		 });
		 
		  var wificonnect = document.querySelector('#wifiConnect');
			wificonnect.addEventListener('click', function () {
			console.log("Wifi disconnect button is clicked");
			var result = window.androidInjectSetting.connectToWifi("GeeksGuest", "GeEkS199");
			result ? alert("GeeksGuest connected") : alert("Can not connect to wifi network")
		 });
	 });
})();
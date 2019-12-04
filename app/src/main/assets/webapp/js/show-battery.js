(function () {
    "use strict";
	 document.addEventListener('DOMContentLoaded', function () {
		 var battery = document.querySelector('#battery');
		 battery.addEventListener('click', function () {
			console.log("Battery button is clicked");
			var level = window.androidInjectSetting.getBatteryPercentage();
			alert("Battery: " + level +"%");
		 });
		 
		 var batteryWithState = document.querySelector('#batteryWithState');
		 batteryWithState.addEventListener('click', function () {
			console.log("Battery with state button is clicked");
			var result = window.androidInjectSetting.getBatteryPercentageWithChargingState();
			result.then = function(callback){
				var name = "callback_"+Math.floor((Math.random() * 100000));
				window[name] = callback
				return result.thenJS(name);
			}
			result.then(function(a){
				var json = JSON.parse(a)
				console.log(a);
				alert("Battery: " + json.percentage +"% and is Charging " + json.isCharging );
			});
			
		 });
		 
	 });
})();
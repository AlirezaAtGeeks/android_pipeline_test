(function () {
    "use strict";
	 document.addEventListener('DOMContentLoaded', function () {
		 var deviceId = document.querySelector('#deviceId');
		 deviceId.addEventListener('click', function () {
			console.log("DeviceId button is clicked");
			var sn = window.androidInjectSetting.getDeviceSerialNumber();
			alert("Serial Number: " + sn);
		 });
	 });
})();
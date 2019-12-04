(function () {
    "use strict";
	 document.addEventListener('DOMContentLoaded', function () {
		 var getKey = document.querySelector('#getKey');
		 getKey.addEventListener('click', function () {
			console.log("getKey button is clicked");
			var key = window.androidInject.getKey();
			console.log(key);
		 });
		 
		 var getCert = document.querySelector('#getCert');
		 getCert.addEventListener('click', function () {
			console.log("getCert button is clicked");
			var cert = window.androidInject.getCertificate();
			console.log(cert);
		 });
	 });
	 
})();
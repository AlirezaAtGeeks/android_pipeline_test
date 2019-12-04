(function () {
    "use strict";
	 document.addEventListener('DOMContentLoaded', function () {
		 var brightness = document.querySelector('#brightness');
		 brightness.addEventListener('click', function () {
			console.log("Brightness button is clicked");
			var sel = document.getElementById('percentages');
			window.androidInjectSetting.setBrightness(Number(sel.value));
		 });
		 
		 var showBrightness = document.querySelector('#showBrightness');
		 showBrightness.addEventListener('click', function () {
			console.log("Show Brightness button is clicked");
			var value = window.androidInjectSetting.getBrightness();
			alert("Value is " + value);
		 });
		 
	 });
})();
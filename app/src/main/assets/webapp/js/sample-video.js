(function () {
    "use strict";

    navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia;
    window.URL = window.URL || window.webkitURL;

    document.addEventListener('DOMContentLoaded', function () {

        var video = document.querySelector('#video'),
            toggle = document.querySelector('#toggle'),
            stream = null;

        if (!navigator.getUserMedia) {
            console.error('getUserMedia not supported');
        }

        toggle.addEventListener('click', function () {
            if (null === stream) {
                // This call to "getUserMedia" initiates a PermissionRequest in the WebView.
                navigator.getUserMedia({ video: true }, function (s) {
                    stream = s;
                    video.src = window.URL.createObjectURL(stream);
                    toggle.value = 'CloseVideo';
                    console.log('Started');
                }, function (error) {
                    console.error('Error starting camera. Denied.');
                });
            } else {
                if (stream.getTracks) {
                    stream.getTracks().forEach(function (track) {
                        track.stop();
                    });
                } else if (stream.stop) {
                    stream.stop();
                }
                stream = null;
                toggle.value = 'ShowVideo';
                console.log('Stopped');
            }
        });

        console.log('Page loaded');

    });

})();

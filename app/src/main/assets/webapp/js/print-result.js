(function () {
    "use strict";

    window.printResult = function (result){
	    console.log(result);
	    var resp = JSON.parse(result)
		    console.log("status: "+ resp.status);
		    console.log("msg: "+ JSON.stringify(resp.msg));
	    alert('**Receipt Printed**\n' + "status: " + resp.status + ",  msg: " + JSON.stringify(resp.msg))
    };


})();

validationFieldAndCall = function() {
	"use strict";
	let forms = $(".needs-validation");
	forms.on("submit", function(event) {
		if (forms.get(0).checkValidity() === true) {
			ajaxDeleteMember();
		}
		event.preventDefault();
		event.stopPropagation();
		forms.addClass("was-validated");
	});
}

ajaxDeleteMember = function() {
	let inputPassword = $("#inputPassword");
	$.ajax({
		type : "delete",
		contentType : "application/json",
		url : "/api/users",
		dataType : "text",
		data : JSON.stringify({
			password : inputPassword.val()
		}),
		success : function(msg) {
			alert(msg);
			window.location.href = "/";
		},
		error : function(res) {
			if (res.status == 400) {
				inputPassword.val("");
				$("#invalid-password").text(res.responseText);
			} else {
		    	alert(res.responseText);
		    }
		}
	});
}

$(document).ready(function() {
	validationFieldAndCall();
});
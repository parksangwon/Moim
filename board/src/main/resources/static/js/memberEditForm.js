validationFieldAndCall = function() {
	"use strict";
	let forms = $(".needs-validation");
	forms.on("submit", function(event) {
		if (forms.get(0).checkValidity() === true) {
			ajaxEditMember();
		}
		event.preventDefault();
		event.stopPropagation();
		forms.addClass("was-validated");
	});
}

ajaxEditMember = function() {
	$.ajax({
		type : "put",
		contentType : "application/json",
		url : "/api/users",
		dataType : "text",
		data : JSON.stringify({
			password : $("#inputPassword").val(),
			name : $("#inputName").val(),
			phone : $("#inputPhone").val(),
			company : $("#inputCompany").val(),
			introduction : $("#inputIntroduction").val()
		}),
		success : function(msg) {
			alert(msg);
			window.location.href = "/member";
		},
		error : function(res) {
			if (res.status == 400) {
				$("#inputPassword").val("");
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
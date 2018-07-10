ajaxLogin = function() {
	let email = $("#email");
	let password = $("#password");
	
	$.ajax({
		type : "post",
		contentType : "application/json",
		url : "/api/login",
		dataType : "text",
		data : JSON.stringify({
			email : email.val(),
			password : password.val()
		}),
		success : function() {
			window.location.href = "/";
		},
		error : function(res) {
			errorHandler(res.responseText);
		}
	});
}

errorHandler = function(message) {
	errorDiv = $("#error-message");
	if (errorDiv.css("display") === "none") {
		errorDiv.css("display", "block");
	}
	errorDiv.text(message);
}

$(document).ready(function() {
	$(".form-signin").on("submit", function() {
		event.preventDefault();
		event.stopPropagation();
		ajaxLogin();
	});
	$("#signup").click(function() {
		window.location.href="/signUp";
	});
});
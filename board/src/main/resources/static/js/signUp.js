ajaxSignUp = function() {
	if (!isPasswordValid()) {
		togglePasswordValid();
		return;
	}
	
	let email = $("#email");
	let password = $("#password");
	let passwordConfirm = $("#password-confirm");
	let name = $("#name");
	let phone = $("#phone");
	
	$.ajax({
		type : "post",
		contentType : "application/json",
		url : "/api/signUp",
		dataType : "text",
		data : JSON.stringify({
			email : email.val(),
			password : password.val(),
			passwordConfirm : passwordConfirm.val(),
			name : name.val(),
			phone : phone.val()
		}),
		success : function(res) {
			alert(res);
			window.location.href = "/login";
		},
		error : function(res) {
			showEmailError(res.responseText);
			email.focus();
			email.select();
		}
	});
}

showEmailError = function(message) {
	emailHelpBlock = $("#emailHelpBlock");
	if (emailHelpBlock.css("display") === "none") {
		emailHelpBlock.css("display", "block");
	}
	emailHelpBlock.children("font").text(message);
}

isPasswordValid = function() {
	const passwordObj = $("#password");
	const passwordConfirmObj = $("#password-confirm");

	const password = passwordObj.val();
	const passwordConfirm = passwordConfirmObj.val();

	if (password === passwordConfirm) {
		return true;
	} else {
		return false;
	}
}

togglePasswordValid = function() {
	if (!isPasswordValid()) {
		$("#passwordHelpBlock").css("display", "block");
	} else {
		$("#passwordHelpBlock").css("display", "none");
	}
}

$(document).ready(function() {
	$(".form-signup").on("submit", function(event) {
		event.preventDefault();
		event.stopPropagation();
		ajaxSignUp();
	});
	$("#password-confirm").on("keyup", function() {
		togglePasswordValid();
	});
	$("#password").on("keyup", function() {
		togglePasswordValid();
	});
});
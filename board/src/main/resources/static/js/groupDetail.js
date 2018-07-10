$(document).ready(function() {

	let groupId = $(".jumbotron").attr("id");

	addEventCommentPost(groupId, userId);
	addEventCommentDelete(groupId);
	addEventModifyGroup(groupId);
	addEventDeleteGroup(groupId);
	addEventRecommendGroup(groupId);
	addEventApplyGroup();
	addEventApplyCancleGroup(groupId);
});

addEventCommentPost = function(groupId, userId) {
	let commentPostButton = $("#comment-button");
	commentPostButton.on("click", function() {
		let commentInput = $("#comment-input");
		if (commentInput.val().length === 0) {
			return;
		} else if (commentInput.val().length >= 200) {
			alert("200자 이하로 입력해주세요.");
			commentInput.focus();
			commentInput.select();
			return;
		}

		$.ajax({
			type : "post",
			contentType : "application/json",
			url : "/api/comment",
			dataType : "json",
			data : JSON.stringify({
				groupId : groupId,
				content : commentInput.val()
			}),
			success : function(commentList) {
				$("[name='comment-card']").remove();

				let commentListDiv = $("#comment-list");
				for (let i = 0; i < commentList.length; i++) {
					let comment = commentList[i];

					let card = $("<div />", {
						"class" : "card",
						"name" : "comment-card"
					});
					let cardBody = $("<div />", {
						"class" : "card-body"
					});
					let row = $("<div />", {
						"class" : "row"
					});
					let col1 = $("<div />", {
						"class" : "col-1",
						"text" : comment.user.name
					});
					let col7 = $("<div />", {
						"class" : "col-7",
						"text" : comment.content
					});
					
					let dateTime = (comment.createTime).split("T");
					let date = dateTime[0];
					let time = dateTime[1].substring(0, 5);
					let col3 = $("<div />", {
						"class" : "col-3",
						"text" : date + " " + time
					});
					
					row.append(col1);
					row.append(col7);
					row.append(col3);
					
					if (userId === comment.user.userId) {
						let cross = $("<button />", {
							"type" : "button",
							"class" : "close",
							"aria-label" : "Close",
							"name" : "comment-delete",
							"data-id" : comment.commentId
						});
						cross.append($("<span />", {
							"aria-hidden" : "true",
							"text" : "×"
						}));

						row.append(cross);
					}
					
					cardBody.append(row);
					card.append(cardBody);

					commentListDiv.append(card);
				}
				$("#comment-input").val("");
				addEventCommentDelete(groupId);
			},
			error : function(res) {
				if (res.status == 403) {
					alert(res.responseJSON.message);
					window.location.href="/login";
				} else {
					alert(res.responseText);
				}
			}
		});
	});
}

addEventCommentDelete = function(groupId) {
	let commentDeleteButton = $("[name='comment-delete']");
	commentDeleteButton.on("click", function() {
		let button = $(this);
		$.ajax({
			type : "delete",
			contentType : "application/json",
			url : "/api/comment",
			data : JSON.stringify({
				groupId : groupId,
				commentId : button.attr("data-id")
			}),
			success : function() {
				button.parents().map(function() {
					if ($(this).hasClass("card")) {
						return this;
					}
				}).remove();
			},
			error : function(res) {
				if (res.status == 403) {
					alert(res.responseJSON.message);
					window.location.href="/login";
				} else {
					alert(res.responseText);
				}
			}
		});
	});
}

addEventModifyGroup = function(groupId) {
	let modifyGroupButton = $("#modify-group");
	modifyGroupButton.on("click", function() {
		window.location.href = "/group/edit/" + groupId;
	});
}

addEventDeleteGroup = function(groupId) {
	let deleteGroupButton = $("#delete-group");
	deleteGroupButton.on("click", function() {
		$.ajax({
			type : "delete",
			contentType : "application/json",
			url : "/api/groups",
			dataType : "text",
			data : JSON.stringify({
				groupId : groupId
			}),
			success : function(msg) {
				alert(msg)
				window.location.href = "/";
			},
			error : function(res) {
				if (res.status == 403) {
					alert(res.responseJSON.message);
					window.location.href="/login";
				} else {
					alert(res.responseText);
				}
			}
		});
	});
}

applyButtonCondition = function(button, condition) {
	button.removeClass();
	
	switch (condition) {
	case "NONE":
	case "SELECTION":
		button.addClass("btn btn-primary btn-lg btn-block");
		button.text("신청하기");
		break;
	case "APPLIED":
		button.addClass("btn btn-primary btn-lg btn-block active");
		button.text("신청 취소");
		break;
	case "FULLOFPERSONNEL":
		button.addClass("btn btn-secondary btn-lg btn-block");
		button.text("정원 도달");
		break;
	case "RECRUITFINISHED":
		button.addClass("btn btn-secondary btn-lg btn-block");
		button.text("신청 종료");
		break;
	case "PROJECTFINISHED":
		button.addClass("btn btn-secondary btn-lg btn-block");
		button.text("일정 종료");
		break;
	}
}

changeApplies = function(amount) {
	$("#applies").val(amount);
}

addEventApplyGroup = function() {
	let groupId = $(".jumbotron").attr("id");
	let applyGroupButton = $("#apply-button");
	applyGroupButton.on("click", function() {
		$.ajax({
			type : "post",
			contentType : "application/json",
			url : "/api/applys",
			dataType : "json",
			data : JSON.stringify({
				groupId : groupId
			}),
			success : function(data) {
				applyButtonCondition(applyGroupButton, data.condition);
				changeApplies(data.applyAmount);
			},
			error : function(res) {
				if (res.status == 403) {
					alert(res.responseJSON.message);
					window.location.href="/login";
				} else {
					alert(res.responseText);
					window.location.reload();
				}
			}
		});
	});
}

recommendButtonCondition = function(button, amount, condition) {
	switch (condition) {
	case "NONE":
		button.removeClass();
		button.addClass("btn btn-outline-warning btn-lg btn-block");
		break;
	case "RECOMMEND":
		button.removeClass();
		button.addClass("btn btn-warning btn-lg btn-block");
		break;
	}
	button.children().text(amount);
}

addEventRecommendGroup = function(groupId) {
	let recommendGroupButton = $("#recommend-button");
	recommendGroupButton.on("click", function() {
		$.ajax({
			type : "post",
			contentType : "application/json",
			url : "/api/recommend",
			dataType : "json",
			data : JSON.stringify({
				groupId : groupId
			}),
			success : function(res) {
				recommendButtonCondition(recommendGroupButton,
						 				 res.recommendAmount,
										 res.condition);
			},
			error : function(res) {
				if (res.status == 403) {
					alert(res.responseJSON.message);
					window.location.href="/login";
				} else {
					alert(res.responseText);
				}
			}
		});
	});
}

addEventApplyCancleGroup = function(groupId) {
	let applyDeleteButton = $("[name='apply-delete']");
	applyDeleteButton.on("click", function() {
		let button = $(this);
		$.ajax({
			type : "delete",
			contentType : "application/json",
			url : "/api/applys",
			dataType : "text",
			data : JSON.stringify({
				applyId : $(this).attr("data-id"),
				groupId: groupId
			}),
			success : function(msg) {
				alert(msg);
				button.parents().map(function() {
					if ($(this).hasClass("card")) {
						return this;
					}
				}).remove();
			},
			error : function(res) {
				if (res.status == 403) {
					alert(res.responseJSON.message);
					window.location.href="/login";
				} else {
					alert(res.responseText);
				}
			}
		});
	});
}

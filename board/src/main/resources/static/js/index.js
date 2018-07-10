attachClickEventEachCard = function() {
	$(".card").each(function() {
		$(this).on("click", function() {
			window.location.href="/group/details/"+$(this).attr("id");
		})
	})
}

filteringButtonToggle = function(button) {
	if (button.hasClass("btn-outline-primary")) {
		button.addClass("btn-primary");
		button.addClass("checked");
		button.removeClass("btn-outline-primary");
	} else {
		button.addClass("btn-outline-primary");
		button.removeClass("checked");
		button.removeClass("btn-primary");
	}
}

attachFilteringEvent = function() {
	$("#keywordSearch").on("keypress", function(event) {
		if (event.keyCode == 13) {
			ajaxSearchGroup();
			event.preventDefault();
			event.stopPropagation();
		}
	});
	$(".btn-group input.btn").on("click", function(event) {
		filteringButtonToggle($(this));
		ajaxSearchGroup();
		event.preventDefault();
		event.stopPropagation();
	});
}

gatherFilteringOptions = function() {
	let keywordFilter = $("#keywordSearch");
	let categoryFilter = $("#categoryButtons input.checked");
	let addressFilter = $("#addressButtons input.checked");
	let dateFilter = $("#dateButtons input.checked");
	let timeFilter = $("#timeButtons input.checked");
	
	let data = new Object();
	
	let category = new String();
	let address = new String();
	let weekdate = new String();
	let time = new String();
	
	data.keyword = keywordFilter.val();

	if (categoryFilter.length === 0) {
		$("#categoryButtons input").each(function() {
			let value = $(this).val();
			category = category.concat(value + "|");
		});
	} else {
		categoryFilter.each(function() {
			let value = $(this).val();
			category = category.concat(value + "|");
		});
	}
	
	if (addressFilter.length === 0) {
		$("#addressButtons input").each(function() {
			let value = $(this).val();
			address = address.concat(value + "|");
		});
	} else {
		addressFilter.each(function() {
			let value = $(this).val();
			address = address.concat(value + "|");
		});
	}

	if (dateFilter.length !== 0 && dateFilter.length !== $("#dateButtons input").length) {
		dateFilter.each(function() {
			let value = $(this).val();
			weekdate = weekdate.concat(value + "|");
		});
	}
	
	if (timeFilter.length === 0) {
		$("#timeButtons input").each(function() {
			let value = $(this).val();
			time = time.concat(value + "|");
		});
	} else {
		timeFilter.each(function() {
			let value = $(this).val();
			time = time.concat(value + "|");
		});
	}
	
	data.category = category.toString();
	data.address = address.toString();
	data.weekdate = weekdate.toString();
	data.time = time;
	
	return data;
}

processGroupList = function(groupList) {
	let groupContainer = $("#group-list-container");
	
	if (!Array.isArray(groupList) || groupList.length === 0) {
		// 없다고 알림
		groupContainer.empty();
		let noGroups = $("<div />", {
			"class": "col-12"
		}).append($("<p />", {
			"class": "text-center",
			"text": "해당되는 모임이 없습니다."
		}));
		groupContainer.append(noGroups);
	} else {
		groupContainer.empty();
		for (let i = 0; i < groupList.length; i++) {
			let group = groupList[i];
			
			let groupCard = $("<div />", {
				"class": "card shadow-sm",
				"id": group.groupId
			});
			
			let imgUrl = "/img/groups/sample.png";
			if (group.imgAddr !== null && group.imgAddr !== "") {
				imgUrl = "/img/groups/" + group.groupId + "/" + group.imgAddr;
			}
			
			groupCard.append($("<img />", {
				"class": "card-img-top",
				"src": imgUrl
			}));
			
			let cardBody = $("<div />", {
				"class": "card-body"
			}).append($("<h5 />", {
				"class": "card-title",
				"text": group.title
			})).append($("<p />", {
				"class": "card-text",
				"text": group.simpleInfo
			}));
			
			let cardClass = "btn btn-primary";
			let availablePersonnel = group.personnel - group.applyList.length;
			let text = availablePersonnel + "명 신청가능";
			
			if (group.recruitingPolicy === "FCFS") { // 선착순일 경우
				// 신청 가능인원이 0이하면 정원도달 정보로 변경
				if (availablePersonnel <= 0) {
					cardClass = "btn btn-secondary",
					text = "정원 도달"
				}
			} else { // 개설자 선정일 경우
				text = group.personnel + "명 모집 중";
			}
			
			cardBody.append($("<a />", {
				"class": cardClass,
				"href": "/group/details/"+group.groupId,
				"text": text
			}));

			groupCard.append(cardBody)
			groupContainer.append(groupCard);
		}
	}
	attachClickEventEachCard();
}

ajaxSearchGroup = function() {
	let data = gatherFilteringOptions();
	$.ajax({
		type : "post",
		contentType : "application/json",
		url : "/api/groups/filtering",
		dataType : "json",
		data : JSON.stringify(data),
		success : function(res) {
			processGroupList(res);
		},
		error : function(res) {
			alert(res.responseText);
		}
	});
}

$(document).ready(function() {
	attachClickEventEachCard();
	attachFilteringEvent();
});

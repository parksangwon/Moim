function ajaxSubmitGroup() {
	let formData = new FormData($("#group-form")[0]);
    $.ajax({
        type : 'post',
        url : '/api/groups',
        data : formData,
        processData : false,
        contentType : false,
        success : function(groupId) {
        	window.location.href="/group/details/"+groupId;
        },
        error : function(res) {
            alert(res.responseText);
        }
    });
}

function validationFieldAndCall() {
	let forms = $(".needs-validation");
	forms.on("submit", function(event) {
		if (forms.get(0).checkValidity() === true) {
			ajaxSubmitGroup();
		}
		event.preventDefault();
		event.stopPropagation();
		forms.addClass("was-validated");
	});
}

attachClickEventAddressForm = function() {
	$("#find-address-button").on("click", function() {
		findAddress();
	});
	$("#address-input").on("click", function () {
		findAddress();
	}).on("focus", function () {
		$("#find-address-button").focus();
	})
}

findAddress = function() {
	var pop = window.open("/jusoPopup","pop", "width=590,height=420,scrollbars=yes,resizable=yes");
}

function jusoCallBack(roadFullAddr, roadAddrPart1, addrDetail, roadAddrPart2, engAddr, jibunAddr, zipNo,
		admCd, rnMgtSn, bdMgtSn, detBdNmList, bdNm, bdKdcd, siNm, sggNm, emdNm, liNm, rn, udrtYn, buldMnnm,
		buldSlno, mtYn, lnbrMnnm, lnbrSlno, emdNo) {
	$("#address-input").val(roadAddrPart1 + " " + addrDetail);
}

$(document).ready(function() {

	$('#recruit-start-datetime').datetimepicker({
		format : "YYYY-MM-DD HH:mm"
	});
	$('#recruit-finish-datetime').datetimepicker({
		format : "YYYY-MM-DD HH:mm",
		useCurrent : false
	});
	$("#recruit-start-datetime").on("change.datetimepicker", function(e) {
		$('#recruit-finish-datetime').datetimepicker('minDate', e.date);
	});
	$("#recruit-finish-datetime").on("change.datetimepicker", function(e) {
		$('#recruit-start-datetime').datetimepicker('maxDate', e.date);
		$('#project-start-datetime').datetimepicker('minDate', e.date); // 모임 시작 시간을 모집 종료 시간 이후로 설정
	});

	$('#project-start-datetime').datetimepicker({
		format : "YYYY-MM-DD HH:mm"
	});
	$('#project-finish-datetime').datetimepicker({
		format : "YYYY-MM-DD HH:mm",
		useCurrent : false
	});
	$("#project-start-datetime").on("change.datetimepicker", function(e) {
		$('#project-finish-datetime').datetimepicker('minDate', e.date);
	});
	$("#project-finish-datetime").on("change.datetimepicker", function(e) {
		$('#project-start-datetime').datetimepicker('maxDate', e.date);
	});
	
	validationFieldAndCall();
	
	attachClickEventAddressForm();
});

function getGroupId() {
	let pathArray = $(location).prop("pathname").split("/");
	return pathArray[pathArray.length-1];
}

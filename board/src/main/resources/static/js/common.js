/**
 * 검색버튼 이벤트 등록. /search?keyword={검색어}
 */
attachEventSearchForm = function() {
	$("#buttonSearch").on("click", function(event) {
		let keyword = $("#inputSearch").val();
		window.location.href="/search?keyword="+keyword;
		event.preventDefault(); // 검색 버튼이 <button>이라 추가 동작 하지 않도록 함.
		event.stopPropagation();
	})
}

/**
 * URL에서 paramName에 해당하는 값을 가져온다.
 */
getParam = function(paramName) {
	params = $(location).attr("search").substring(1).split("&");
	for (let i = 0; i < params.length; i++) {
		let param = params[i].split("=");
		let name = param[0];
		if (name === paramName) {
			// uri디코딩해서 리턴
			return decodeURIComponent(param[1]);
		}
	}
}

setSearchForm = function() {
	let keyword = getParam("keyword");
	$("#inputSearch").val(keyword);
	$("#keywordSearch").val(keyword);
}

$(document).ready(function() {
	attachEventSearchForm();
	setSearchForm();
});
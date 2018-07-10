package com.navercorp.board.web;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.navercorp.board.constant.Const;
import com.navercorp.board.constant.Utils;
import com.navercorp.board.domain.Address;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.GroupNotExistException;
import com.navercorp.board.service.GroupService;
import com.navercorp.board.service.UserService;

@Controller
public class GroupController {

	@Autowired
	GroupService groupService;
	
	@Autowired
	UserService userService;
	
	/**
	 * 시간을 표시하는 문자 생성
	 * @param diff 시간차이
	 * @return 시간을 표시하는 문자열
	 */
	public static String formattingDuration(Duration diff) {
		StringBuilder builder = new StringBuilder();
		
		long minutes = diff.toMinutes();
		
		builder.append(minutes/60/24).append("일 ");
		builder.append(minutes/60%24).append("시간 ");
		builder.append(minutes%60).append("분 남았습니다.");
		return builder.toString();
	}

	@GetMapping("/")
	public String index(Model model) {
		List<Group> groups = groupService.findAll();
		model.addAttribute("groups", groups);
		return "index";
	}
	
	/**
	 * 검색
	 * @param model
	 * @param keyword 검색어
	 * @return 검색어와 일치하는 신청 가능한 모임 목록
	 */
	@GetMapping("/search")
	public String searchFromNavbar(Model model, @RequestParam String keyword) {
		if (keyword.length() == 0) {
			List<Group> groups = groupService.findAll();
			model.addAttribute("groups", groups);
		} else {
			List<Group> groups = groupService.findByKeyword(keyword);
			model.addAttribute("groups", groups);
		}
		return "index";
	}
	
	/**
	 * 주소 검색창을 띄우고 데이터 맵핑하는 메소드
	 * @param address 데이터가 맵핑되는 객체, 처음 요청에는 빈 객체지만 주소를 입력하고 다시 POST로 요청될때는 주소 데이터가 들어가 있다.
	 * @return jusoPopup 페이지를 렌더링한다.
	 */
	@RequestMapping("/jusoPopup")
    public String jusoPopup(@ModelAttribute("address") Address address) {
        return "jusoPopup";
    }
	
	/**
	 * 모임 상세 조회를 보여주는 메소드
	 * @param model 요청한 groupId로 조회한 모임을 포함한다.
	 * @param groupId 조회할 모임의 groupId
	 * @return 해당되는 그룹이 있으면 groupDetail을 렌더링하고, 없으면 errorPage를 렌더링한다.
	 */
	@GetMapping("/group/details/{groupId}")
	public String showGroupDetails(Model model, @PathVariable Long groupId, HttpServletRequest request) {
		// 그룹을 가져온다.
		try {
			Group group = groupService.findByGroupId(groupId);
			User writer = group.getUser();
			
			String groupCondition = Const.NONE;
			String recommendCondition = Const.NONE;
			HttpSession session = request.getSession(false);
			if (session == null) { // 로그인을 하지 않았을 때 모임을 조회 하면 *표로 작성자 정보를 표시한다.
				writer.setEmail(Utils.changeToAsterisk(writer.getEmail()));
				writer.setPhone(Utils.changeToAsterisk(writer.getPhone()));
			} else { // 로그인을 했을 때는 유저의 지원상태를 확인하고 버튼의 상태를 결정한다.
				Long userId = (Long) session.getAttribute("userId");
				groupCondition = Utils.checkGroupCondition(group, userId);
				recommendCondition = Utils.checkRecommendCondition(group, userId);
			}
			
			Duration restTime = Duration.between(LocalDateTime.now(), group.getRfDate());
			
			// 그룹정보, 지원정보로 컨디션을 파악
			model.addAttribute("group", group);
			model.addAttribute("writer", writer);
			model.addAttribute("groupCondition", groupCondition);
			model.addAttribute("recommendCondition", recommendCondition);
			model.addAttribute("restTime", formattingDuration(restTime));
			return "groupDetail";
		} catch (NullPointerException e) {
		    throw new GroupNotExistException();
		}
	}

	/**
	 * 모임 수정 폼을 보여주는 메소드, 로그인이 필요한 기능
	 * @param model 요청한 groupId와 세션에 포함된 userId로 조회한 모임을 포함한다.
	 * @param groupId 수정할 모임의 groupId
	 * @param request 세션을 가져오기 위한 객체
	 * @return 세션이 존재하지 않으면 로그인 페이지로 redirect, 세션이 존재하나 연관된 모임이 없으면 메인 페이지로
	 *         redirect, 모임이 있으면 model에 그룹을 담아서 groupForm을 렌더링한다.
	 */
	@GetMapping("/group/edit/{groupId}")
	public String getEditGroupForm(Model model, @PathVariable Long groupId, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		User user = userService.findByUserId(userId);
		Group group = groupService.findByIdAndUserId(groupId, user);
		
		if (group == null) {
		    throw new GroupNotExistException();
		}
		model.addAttribute(group);
		return "groupForm";
	}

	/**
	 * 모임 개설 폼을 보여주는 메소드, 로그인이 필요한 기능
	 * @param request 세션을 가져오기 위한 객체
	 * @return 세션이 존재하지 않으면 로그인페이지로 redirect, 존재하면 groupForm을 렌더링한다.
	 */
	@GetMapping("/group/new")
	public String getGroupForm(HttpServletRequest request) {
		return "groupForm";
	}

	@ExceptionHandler(GroupNotExistException.class)
	public String notExistExceptionHandler(Model model) {
		model.addAttribute("message", "존재하지 않는 모임입니다.");
		return "error";
	}
	
	@ExceptionHandler(Exception.class)
	public String exceptionHandler(Model model) {
		model.addAttribute("message", "올바르지 않은 요청입니다.");
		return "error";
	}
}

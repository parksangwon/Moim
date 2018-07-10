package com.navercorp.board.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

import com.navercorp.board.domain.Apply;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.UserNotExistException;
import com.navercorp.board.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	UserService userService;

	/**
	 * 로그인
	 * @param request 세션을 가져오기 위함
	 * @return 로그인 페이지로 이동, 세션이 없으면 로그인 페이지로 이동
	 */
	@GetMapping("/login")
	public String getLoginForm(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			return "redirect:/";
		} else {
			return "login";
		}
	}

	/**
	 * 로그아웃
	 * @param session
	 * @return
	 */
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/";
	}

	/**
	 * 회원 가입
	 * @return 회원가입 페이지로 이동
	 */
	@GetMapping("/signUp")
	public String getSignUpForm() {
		return "signUp";
	}
	
	/**
	 * 마이페이지
	 * @param model
	 * @param session
	 * @return 마이페이지로 이동
	 */
	@GetMapping("/member")
	public String getMyPage(Model model, HttpSession session) {
		try {
			Long userId = (Long) session.getAttribute("userId");
			User user = userService.findByUserId(userId);
			if (user == null) {
				throw new UserNotExistException();
			}
			List<Apply> applyList = user.getApplyList();
			List<Group> groupList = user.getGroupList();
			model.addAttribute("user", user);
			model.addAttribute("applyList", applyList);
			model.addAttribute("groupList", groupList);
			return "myPage";
		} catch (NullPointerException e) {
			throw new UserNotExistException();
		}
	}
	
	/**
	 * 회원정보페이지
	 * @param model
	 * @param session
	 * @return 회원정보 페이지로 이동
	 */
	@GetMapping("/member/edit")
	public String getEditMemberPage(Model model, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		User user = userService.findByUserId(userId);
		if (user == null) {
			throw new UserNotExistException();
		}
		model.addAttribute("user", user);
		return "memberEditForm";
	}
	
	/**
	 * 회원탈퇴 페이지
	 * @param model
	 * @param session
	 * @return 회원 탈퇴 페이지로 이동
	 */
	@GetMapping("/member/delete")
	public String getDeleteMemberPage(Model model, HttpSession session) {
		Long userId = (Long) session.getAttribute("userId");
		User user = userService.findByUserId(userId);
		if (user == null) {
			throw new UserNotExistException();
		}
		model.addAttribute("user", user);
		return "memberDelete";
	}
	
	@ExceptionHandler(UserNotExistException.class)
	public String notExistExceptionHandler(Model model) {
		model.addAttribute("message", "존재하지 않는 유저입니다.");
		return "error";
	}
}

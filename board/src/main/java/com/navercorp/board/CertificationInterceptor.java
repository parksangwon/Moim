package com.navercorp.board;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CertificationInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession(false);

		// rest api는 responseEntity로 전송, 그냥 request는 로그인페이지로 이동
		if (session == null) {
			if (request.getRequestURI().indexOf("/api") >= 0) {
				response.sendError(403, "로그인이 필요한 기능입니다.");
			} else {
				response.sendRedirect("/login");
			}
			return false;
		} else {
			return true;
		}
	}
}

package com.navercorp.board.api;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.navercorp.board.domain.User;
import com.navercorp.board.exception.AuthenticationException;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.ConflictEmailException;
import com.navercorp.board.exception.MismatchPatternException;
import com.navercorp.board.exception.NotEnoughDataException;
import com.navercorp.board.exception.UserNotExistException;
import com.navercorp.board.service.UserService;

@RestController
public class UserRestController {

	@Autowired
	UserService userService;
	
	/**
	 * 로그인 rest api
	 * @param loginData 로그인 정보
	 * @param request 세션을 가져오기 위함
	 * @return 성공시  OK
	 */
	@PostMapping("/api/login")
	public ResponseEntity<String> login(@RequestBody Properties loginData, HttpServletRequest request) {
		String email = loginData.getProperty("email", "");
		String password = loginData.getProperty("password", "");
		
		if (email.equals("") || password.equals("")) {
			throw new NotEnoughDataException("데이터를 모두 입력하세요");
		}
		
		try {
			User storedUser = userService.checkUserForLogin(email, password);
			HttpSession session = request.getSession();
			session.setMaxInactiveInterval(3600);
			session.setAttribute("userName", storedUser.getName());
			session.setAttribute("userId", storedUser.getUserId());
			session.setAttribute("userEmail", storedUser.getEmail());
			return ResponseEntity.ok(null);
		} catch (MismatchPatternException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (AuthenticationException e) {
			throw new BadRequestException("이메일이나 비밀번호를 다시 확인해주세요.");
		}
	}

	/**
	 * 회원가입하는 rest api
	 * @param signUpData 회원가입 정보
	 * @return 성공시 메시지와 OK
	 */
	@PostMapping("/api/signUp")
	public ResponseEntity<String> signUp(@RequestBody Properties signUpData) {
		String email = signUpData.getProperty("email", "");
		String password = signUpData.getProperty("password", "");
		String passwordConfirm = signUpData.getProperty("passwordConfirm", "");
		String name = signUpData.getProperty("name", "");
		String phone = signUpData.getProperty("phone", "");
		
		if (email.equals("") || password.equals("") || passwordConfirm.equals("") 
				|| name.equals("") || phone.equals("") || !passwordConfirm.equals(password)) {
			throw new NotEnoughDataException("데이터를 모두 입력하거나 비밀번호를 확인해주세요.");
		}
		
		try {
			User newUser = new User();
			newUser.setEmail(email);
			newUser.setPassword(password);
			newUser.setName(name);
			newUser.setPhone(phone);
			userService.submitUser(newUser);
			return new ResponseEntity<String>("회원가입이 완료되었습니다. 로그인 하세요.", HttpStatus.OK);
		} catch (MismatchPatternException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (ConflictEmailException e) {
			throw new BadRequestException("사용할 수 없는 이메일 입니다.");
		} catch (BadRequestException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		}
	}
	
	/**
	 * 회원정보 수정 rest api
	 * @param userData 회원정보
	 * @param session user_id추출
	 * @return 성공하면 메시지와 OK
	 */
	@PutMapping("/api/users")
	private ResponseEntity<String> updateUserInformation(@RequestBody Properties userData, HttpSession session) {
		String password = userData.getProperty("password", "");
		String name = userData.getProperty("name", "");
		String phone = userData.getProperty("phone", "");
		String company = userData.getProperty("company", "");
		String introduction = userData.getProperty("introduction", "");
		
		if (password.equals("") || name.equals("") || phone.equals("")) {
			throw new NotEnoughDataException("데이터를 모두 입력하거나 비밀번호를 확인해주세요.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			
			User newInfoUser = new User();
			newInfoUser.setUserId(userId);
			newInfoUser.setPassword(password);
			newInfoUser.setName(name);
			newInfoUser.setPhone(phone);
			newInfoUser.setCompany(company);
			newInfoUser.setIntroduction(introduction);
			userService.updateUser(newInfoUser);
			return new ResponseEntity<String>("회원 정보 업데이트 성공", HttpStatus.OK);
		} catch (MismatchPatternException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (AuthenticationException e) {
			throw new BadRequestException("비밀번호를 다시 확인해주세요.");
		} catch (UserNotExistException e) {
			throw new BadRequestException("사용자 정보 업데이트 실패");
		}
	}

	/**
	 * 회원 탈퇴 rest api
	 * @param userData 비밀번호를 포함한 객체
	 * @param session user_id를 추출할때 사용
	 * @return 성공시 메시지와 OK
	 */
	@DeleteMapping("/api/users")
	private ResponseEntity<String> deleteUser(@RequestBody Properties userData, HttpSession session) {
		String password = userData.getProperty("password", "");
		if (password.equals("")) {
			throw new NotEnoughDataException("비밀번호를 확인해주세요.");
		}

		try {
			Long userId = (Long) session.getAttribute("userId");
			userService.deleteUser(userId, password);
			session.invalidate();
			return new ResponseEntity<String>("이용해 주셔서 감사합니다.", HttpStatus.OK);
		} catch (MismatchPatternException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (AuthenticationException e) {
			throw new BadRequestException("비밀번호를 다시 확인해주세요.");
		} catch (UserNotExistException e) {
			throw new BadRequestException("회원탈퇴 실패");
		}
	}

}

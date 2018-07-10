package com.navercorp.board.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navercorp.board.BoardApplication;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.AuthenticationException;
import com.navercorp.board.exception.ConflictEmailException;
import com.navercorp.board.exception.MismatchPatternException;
import com.navercorp.board.exception.UserNotExistException;
import com.navercorp.board.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	/**
	 * 유저 아이디로 유저를 탐색한다.
	 * @param userId 유저아이디
	 * @return 탐색된 유저
	 */
	public User findByUserId(Long userId) {
		return userRepository.findByUserId(userId);
	}

	/**
	 * 로그인
	 * @param email 이메일
	 * @param password 패스워드
	 * @return 성공하면 유저 정보를 반환
	 * @throws MismatchPatternException validation 통과 못할 시 발생
	 * @throws AuthenticationException 비밀번호가 틀렸을 때 발생
	 */
	public User checkUserForLogin(String email, String password) throws MismatchPatternException, AuthenticationException {
		boolean emailCheck = Pattern.matches(".+\\@.+\\..+", email);
		boolean passwordCheck = Pattern.matches(".{6,20}", password);
		if ( !(emailCheck && passwordCheck) ) {
			throw new MismatchPatternException();
		}
		
		try {
			User storedUser = userRepository.findByEmail(email);
			if (!BCrypt.checkpw(password, storedUser.getPassword())) {
				throw new AuthenticationException();
			}
			return storedUser;
		} catch (NullPointerException e) {
			throw new AuthenticationException();
		}
	}

	/**
	 * 회원가입
	 * @param newUser 유저 정보
	 * @return 저장된 유저정보
	 * @throws MismatchPatternException validation 통과 못할 때 발생
	 * @throws ConflictEmailException 중복된 이메일일때 발생
	 */
	@Transactional
	public User submitUser(User newUser) throws MismatchPatternException, ConflictEmailException {
		boolean emailCheck = Pattern.matches(".+\\@.+\\..+", newUser.getEmail());
		boolean passwordCheck = Pattern.matches(".{6,20}", newUser.getPassword());
		boolean nameCheck = Pattern.matches(".{2,10}", newUser.getName());
		boolean phoneCheck = Pattern.matches(".{10,20}", newUser.getPhone());
		if ( !(emailCheck && passwordCheck && nameCheck && phoneCheck) ) {
			throw new MismatchPatternException();
		}
		
		newUser.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt()));
		try {
			return userRepository.save(newUser);
		} catch (DataIntegrityViolationException e) {
			throw new ConflictEmailException();
		}
	}

	/**
	 * 회원정보수정
	 * @param newInfoUser 유저 정보
	 * @return 갱신된 유저
	 * @throws MismatchPatternException validation에 통과 못할 떄 발생
	 * @throws AuthenticationException 비밀번호가 일치하지 않을 때 발생
	 * @throws UserNotExistException 존재하지 않는 유저일때 발생
	 */
	@Transactional
	public User updateUser(User newInfoUser) throws MismatchPatternException, AuthenticationException, UserNotExistException{
		boolean passwordCheck = Pattern.matches(".{6,20}", newInfoUser.getPassword());
		boolean nameCheck = Pattern.matches(".{2,10}", newInfoUser.getName());
		boolean phoneCheck = Pattern.matches(".{10,20}", newInfoUser.getPhone());
		boolean companyCheck = Pattern.matches(".{0,30}", newInfoUser.getCompany());
		boolean introductionCheck = Pattern.matches(".{0,100}", newInfoUser.getIntroduction());
		if ( !(passwordCheck && nameCheck && phoneCheck && companyCheck && introductionCheck) ) {
			throw new MismatchPatternException();
		}
		
		try {
			User storedUser = userRepository.findByUserId(newInfoUser.getUserId());
			if (!BCrypt.checkpw(newInfoUser.getPassword(), storedUser.getPassword())) {
				throw new AuthenticationException();
			}
			storedUser.update(newInfoUser);
			return userRepository.save(storedUser);
		} catch (NullPointerException e) {
			throw new UserNotExistException();
		}
	}

	/**
	 * 회원탈퇴
	 * @param userId 유저 일련번호
	 * @param password 비밀번호
	 * @throws MismatchPatternException validation 통과 못할 시 발생
	 * @throws AuthenticationException 비밀번호가 일치하지 않을때 발생
	 * @throws UserNotExistException 존재하지 않는 유저일 떄 발생
	 */
	@Transactional
	public void deleteUser(Long userId, String password) throws MismatchPatternException, AuthenticationException, UserNotExistException {
		boolean passwordCheck = Pattern.matches(".{6,20}", password);
		if ( !(passwordCheck) ) {
			throw new MismatchPatternException();
		}
		
		try {
			User storedUser = userRepository.findByUserId(userId);
			if (!BCrypt.checkpw(password, storedUser.getPassword())) {
				throw new AuthenticationException();
			}
			List<Group> groupList = storedUser.getGroupList();
			List<Long> groupIdList = new ArrayList<Long>();
			
			for (Group group: groupList) {
				groupIdList.add(group.getGroupId());
			}
			
			userRepository.delete(storedUser);
			
			String path = "C:/img";
			String OS = BoardApplication.OS;
			if (OS.indexOf("win") >= 0) {
			    path = path + File.separator + "groups";
			} else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0) {
			    path = "/home/img/groups";
			}
			
			for (Long groupId : groupIdList) {
			    File dir = new File(path + File.separator + groupId);
			    if (dir.isDirectory()) {// 디렉토리가 존재하면 있던 이미지 모두 삭제
				File[] files = dir.listFiles();
				for (File file : files) {
				    file.delete();
				}
				dir.delete();
			    }
			}
		} catch (NullPointerException e) {
			throw new UserNotExistException();
		}
	}
}

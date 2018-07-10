package com.navercorp.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navercorp.board.constant.Const;
import com.navercorp.board.constant.Utils;
import com.navercorp.board.domain.Apply;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.repository.ApplyRepository;
import com.navercorp.board.repository.GroupRepository;
import com.navercorp.board.repository.UserRepository;

@Service
public class ApplyService {

	@Autowired
	ApplyRepository applyRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	GroupRepository groupRepository;

	/**
	 * 마이페이지에서 신청한 이력을 조회
	 * @param user 
	 * @return
	 */
	public List<Apply> findByUser(User user) {
		return applyRepository.findByUser(user);
	}
	
	/**
	 * 모임에 유저가 신청한다.
	 * @param groupId 모임 아이디
	 * @param userId 유저 아이디
	 * @return 
	 */
	@Transactional
	public Group apply(Long groupId, Long userId) throws BadRequestException, NotExistException {
		try {
			User storedUser = userRepository.findByUserId(userId);
			Group storedGroup = groupRepository.findByGroupId(groupId);
			
			String condition = Utils.checkGroupCondition(storedGroup, userId);
			switch (condition) { // APPLIED, NONE, METHOD_SELECTION 일 경우 통과
			case Const.PROJECT_FINISHED:
			case Const.RECRUIT_FINISHED:
			case Const.BEFORE_RECRUIT:
			case Const.FULL_OF_PERSONNEL:
				throw new BadRequestException();
			}
			
			List<Apply> storedApplyList = applyRepository.findByGroupAndUser(storedGroup, storedUser);
			if (storedApplyList.size() == 0) { // 신청하지 않았으면 신청
				Apply newApply = new Apply(storedGroup, storedUser);
				applyRepository.save(newApply);
			} else { // 신청했으면 신청 취소
				storedGroup.removeApply(storedApplyList.get(0));
				applyRepository.deleteByGroupAndUser(storedGroup, storedUser);
			}
			return storedGroup;
		} catch (NullPointerException e) {
			throw new NotExistException();
		}
	}
	
	/**
	 * 신청 삭제(개설자만 사용 가능)
	 * @param groupId 모임 아이디
	 * @param userId 요청자 아이디
	 * @param applyId 신청 아이디
	 * @throws BadRequestException 올바른 요청이 아닐 때 발생
	 * @throws NotExistException 존재하지 않는 신청일 때 발생
	 */
	@Transactional
	public void applyCancle(Long groupId, Long userId, Long applyId) throws BadRequestException, NotExistException {
	    try {
		User storedUser = userRepository.findByUserId(userId);
		Group storedGroup = groupRepository.findByGroupId(groupId);
		
		Long writerId = storedGroup.getUser().getUserId();
		Long requestUserId = storedUser.getUserId();
		
		if (!writerId.equals(requestUserId)) {
			throw new BadRequestException();
		}
		
		applyRepository.deleteById(applyId);
	    } catch (EmptyResultDataAccessException e) {
		throw new NotExistException();
	    } catch (NullPointerException e) {
		throw new NotExistException();
	    } catch (IllegalArgumentException e) {
		throw new BadRequestException();
	    }
	}
}

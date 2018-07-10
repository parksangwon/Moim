package com.navercorp.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.Recommend;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.repository.GroupRepository;
import com.navercorp.board.repository.RecommendRepository;
import com.navercorp.board.repository.UserRepository;

@Service
public class RecommendService {

	@Autowired
	RecommendRepository recommendRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	GroupRepository groupRepository;

	/**
	 * 모임을 추천한다.
	 * @param groupId 모임 아이디
	 * @param userId 요청자 아이디
	 * @return 해당 모임을 반환
	 * @throws NotExistException 존재하지않는 모임이나 유저인 경우 발생
	 */
	@Transactional
	public Group recommend(Long groupId, Long userId) throws NotExistException {
		try {
			User storedUser = userRepository.findByUserId(userId);
			Group storedGroup = groupRepository.findByGroupId(groupId);
			
			if (storedUser == null || storedGroup == null) {
				throw new NotExistException();
			}
			
			List<Recommend> storedRecommendList
				= recommendRepository.findByGroupAndUser(storedGroup, storedUser);
			
			if (storedRecommendList.size() == 0) { // 추천하지 않았으면 추천
				Recommend newRecommend = new Recommend();
				newRecommend.setGroup(storedGroup);
				newRecommend.setUser(storedUser);
				recommendRepository.save(newRecommend);
			} else {
				recommendRepository.deleteByGroupAndUser(storedGroup, storedUser);
			}
			return storedGroup;
		} catch (NullPointerException e) {
			throw new NotExistException();
		}
	}
}

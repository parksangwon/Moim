package com.navercorp.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navercorp.board.domain.Apply;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

	/**
	 * 신청 목록을 조회한다.
	 * @param user 신청자
	 * @return 신청 목록
	 */
	List<Apply> findByUser(User user);
	
	/**
	 * 신청 여부를 조회한다.
	 * @param group 신청한 모임
	 * @param user 신청한 유저
	 * @return 신청 데이터
	 */
	List<Apply> findByGroupAndUser(Group group, User user);

	/**
	 * 신청을 취소한다.
	 * @param group 신청한 모임
	 * @param user 신청한 유저
	 */
	void deleteByGroupAndUser(Group group, User user);

}

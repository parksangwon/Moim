package com.navercorp.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.Recommend;
import com.navercorp.board.domain.User;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {

	List<Recommend> findByGroupAndUser(Group storedGroup, User storedUser);

	void deleteByGroupAndUser(Group group, User user);
	
}

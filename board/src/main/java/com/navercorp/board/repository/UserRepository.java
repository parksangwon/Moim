package com.navercorp.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navercorp.board.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserId(Long userId);

	User findByEmail(String email);

}

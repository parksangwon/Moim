package com.navercorp.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.navercorp.board.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	Comment findByCommentId(Long commentId);
	
}

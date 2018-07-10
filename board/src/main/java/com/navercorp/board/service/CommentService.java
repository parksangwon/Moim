package com.navercorp.board.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.navercorp.board.domain.Comment;
import com.navercorp.board.domain.Group;
import com.navercorp.board.domain.User;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.MismatchPatternException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.repository.CommentRepository;
import com.navercorp.board.repository.GroupRepository;
import com.navercorp.board.repository.UserRepository;

@Service
public class CommentService {

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	/**
	 * 댓글을 생성한다.
	 * @param groupId 모임 아이디
	 * @param userId 작성자 아이디
	 * @param content 내용
	 * @return 댓글 목록
	 * @throws MismatchPatternException validation통가 못할 때 발생
	 * @throws NotExistException 존재하지 않는 모임이나 유저일때 발생
	 */
	@Transactional
	public List<Comment> createComment(Long groupId, Long userId, String content) throws MismatchPatternException, NotExistException{
		boolean contentCheck = Pattern.matches(".{1,200}", content);
		if (!contentCheck) {
			throw new MismatchPatternException();
		}
		
		try {
			User user = userRepository.findByUserId(userId);
			Group group = groupRepository.findByGroupId(groupId);
			if (user == null || group == null) {
				throw new NotExistException();
			}
			Comment comment = new Comment(user, group, content);
			commentRepository.save(comment);
			return group.getCommentList();
		} catch (NullPointerException e) {
			throw new NotExistException();
		}
		
	}

	/**
	 * 댓글을 삭제한다.
	 * @param groupId 모임 아이디
	 * @param userId 요청자 아이디
	 * @param commentId 댓글 아이디
	 * @throws NotExistException 존재하지 않는 댓글이나 유저나 모임일때 발생
	 * @throws BadRequestException 작성자와 요청자가 일치하지 않을 때 발생
	 */
	@Transactional
	public void deleteComment(Long groupId, Long userId, Long commentId) throws NotExistException, BadRequestException {
		try {
			User user = userRepository.findByUserId(userId);
			Group group = groupRepository.findByGroupId(groupId);
			if (user == null || group == null) {
				throw new NotExistException();
			}
			Comment comment = commentRepository.findByCommentId(commentId);
			if (!comment.getGroup().getGroupId().equals(groupId) || !comment.getUser().getUserId().equals(userId)) {
				throw new BadRequestException();
			}
			commentRepository.delete(comment);
		} catch (IllegalArgumentException e) {
			throw new NotExistException();
		} catch (NullPointerException e) {
			throw new NotExistException();
		}

	}

}

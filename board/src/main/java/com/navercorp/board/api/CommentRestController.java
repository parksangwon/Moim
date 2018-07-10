package com.navercorp.board.api;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navercorp.board.domain.Comment;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.MismatchPatternException;
import com.navercorp.board.exception.NotEnoughDataException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.service.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentRestController {

	@Autowired
	CommentService commentService;

	/**
	 * 댓글을 등록한다.
	 * @param commentData commentData
	 * @param session
	 * @return 해당 그룹의 댓글 전체 목록을 가져온다.
	 */
	@PostMapping
	private ResponseEntity<List<Comment>> postComment(@RequestBody Properties commentData, HttpSession session) {
		String groupIdString = commentData.getProperty("groupId", "");
		String content = commentData.getProperty("content", "");
		if (groupIdString.equals("") || content.equals("")) {
			throw new NotEnoughDataException("데이터가 부족합니다.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			Long groupId = Long.valueOf(groupIdString);
			
			List<Comment> commentList = commentService.createComment(groupId, userId, content);
			return new ResponseEntity<List<Comment>>(commentList, HttpStatus.OK);
		} catch (MismatchPatternException e) {
			throw new BadRequestException("입력을 다시 확인해주세요.");
		} catch (NotExistException e) {
			throw new BadRequestException("존재하지 않는 모임이나 사용자 입니다.");
		}
	}

	/**
	 * 댓글을 삭제한다.
	 * @param commentData groupId, commentId를 전달 받는다.
	 * @param session
	 * @return 삭제에 성공하면 HttpStatus.OK를 전달한다.
	 */
	@DeleteMapping
	private ResponseEntity<?> deleteComment(@RequestBody Properties commentData, HttpSession session) {
		String commentIdString = commentData.getProperty("commentId", "");
		String groupIdString = commentData.getProperty("groupId", "");
		if (groupIdString.equals("") || commentIdString.equals("")) {
			throw new NotEnoughDataException("데이터가 부족합니다.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			Long commentId = Long.valueOf(commentIdString);
			Long groupId = Long.valueOf(groupIdString);
			
			commentService.deleteComment(groupId, userId, commentId);
			return ResponseEntity.ok(null);
		} catch (NotExistException e) {
			throw new BadRequestException("존재하지 않는 모임이나 사용자 입니다.");
		} catch (BadRequestException e) {
			throw new BadRequestException("댓글이 존재하지 않거나 권한이 없습니다.");
		}
	}
}

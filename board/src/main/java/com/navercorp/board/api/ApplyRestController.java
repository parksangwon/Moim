package com.navercorp.board.api;

import java.util.HashMap;
import java.util.Map;
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

import com.navercorp.board.constant.Utils;
import com.navercorp.board.domain.Group;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.NotEnoughDataException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.service.ApplyService;

@RestController
@RequestMapping("/api/applys")
public class ApplyRestController {

	@Autowired
	ApplyService applyService;

	/**
	 * 모임에 신청하는 api
	 * @param applyData 전달 받는 데이터는 {groupId : groupId}이다. groupId는 신청되는 모임의 아이디
	 * @param request 세션을 확인하고 userId를 가져올 객체
	 * @return 세션이 존재하지 않으면 UNAUTHORIZED, 신청되면 CREATED, 다른 오류로 신청되지 않는다면
	 *         BAD_REQUEST와 각 메시지를 전달한다.
	 */
	@PostMapping
	private ResponseEntity<Map<String, String>> postApply(@RequestBody Properties applyData, HttpSession session) {
		String groupIdString = applyData.getProperty("groupId");
		if (groupIdString.equals("")) {
			throw new NotEnoughDataException("데이터가 부족합니다.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			Long groupId = Long.valueOf(groupIdString);
			
			Group group = applyService.apply(groupId, userId);
			Map<String, String> groupData = new HashMap<String, String>();
			groupData.put("applyAmount", String.valueOf(group.getApplyList().size()));
			groupData.put("condition", Utils.checkGroupCondition(group, userId));

			return new ResponseEntity<Map<String, String>>(groupData, HttpStatus.OK);
		} catch (NotExistException e) {
			throw new BadRequestException("유저나 모임이 존재하지 않습니다.");
		} catch (BadRequestException e) {
			throw new BadRequestException("신청할 수 없는 모임입니다.");
		} catch (NumberFormatException | IllegalStateException e) {
			throw new BadRequestException("유효하지 않은 모임이나 유저입니다.");
		}
	}
	
	/**
	 * 개설자가 신청을 삭제하는 api
	 * @param applyData
	 * @param session
	 * @return 삭제되면 ok
	 */
	@DeleteMapping
	private ResponseEntity<String> removeApply(@RequestBody Properties applyData, HttpSession session) {
		String applyIdString = applyData.getProperty("applyId");
		String groupIdString = applyData.getProperty("groupId");
		if (applyIdString.equals("") || applyIdString.equals("")) {
			throw new NotEnoughDataException("데이터가 부족합니다.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			Long groupId = Long.valueOf(groupIdString);
			Long applyId = Long.valueOf(applyIdString);
			
			applyService.applyCancle(groupId, userId, applyId);
			return new ResponseEntity<String>("신청이 삭제되었습니다.", HttpStatus.OK);
		} catch (NotExistException e) {
			throw new BadRequestException("존재하지 않는 모임신청입니다.");
		} catch (BadRequestException e) {
			throw new BadRequestException("잘못된 요청입니다.");
		} catch (NumberFormatException | IllegalStateException e) {
			throw new BadRequestException("유효하지 않은 모임이나 유저입니다.");
		}
	}
}

package com.navercorp.board.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.navercorp.board.constant.Utils;
import com.navercorp.board.domain.Group;
import com.navercorp.board.exception.BadRequestException;
import com.navercorp.board.exception.NotEnoughDataException;
import com.navercorp.board.exception.NotExistException;
import com.navercorp.board.service.RecommendService;

@RestController
@RequestMapping("/api/recommend")
public class RecommendRestController {

	@Autowired
	RecommendService recommendService;

	/**
	 * 추천 rest api
	 * @param recommendData 모임 ID정보를 가진 객체
	 * @param session userId를 추출하는데 사용
	 * @return 추천이 안되어있으면 추천하고 추천 데이터 return, 추천되어있으면 추천취소하고 데이터 return
	 */
	@PostMapping
	private ResponseEntity<Map<String, String>> recommend(@RequestBody Properties recommendData, HttpSession session) {
		String groupIdString = recommendData.getProperty("groupId","");
		if (groupIdString.equals("")) {
			throw new NotEnoughDataException("데이터가 부족합니다.");
		}
		
		try {
			Long userId = (Long) session.getAttribute("userId");
			Long groupId = Long.valueOf(groupIdString);
			
			Group group = recommendService.recommend(groupId, userId);
			Map<String, String> groupData = new HashMap<String, String>();
			groupData.put("recommendAmount", String.valueOf(group.getRecommendList().size()));
			groupData.put("condition", Utils.checkRecommendCondition(group, userId));
			
			return new ResponseEntity<Map<String, String>>(groupData, HttpStatus.OK);
		} catch (NotExistException e) {
			throw new BadRequestException("유저나 모임이 존재하지 않습니다.");
		} catch (NumberFormatException | IllegalStateException e) {
			throw new BadRequestException("유효하지 않은 모임이나 유저입니다.");
		}
	}
}
